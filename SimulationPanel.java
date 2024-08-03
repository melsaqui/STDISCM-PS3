import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;

public class SimulationPanel extends JPanel {
    private final DrawPanel drawPanel;
    private final ThreadManager threadManager;
    private Thread gameThread;
    private volatile boolean running = false;
    private final FPSCounter fpsCounter = new FPSCounter();

    public SimulationPanel() {
        drawPanel = new DrawPanel();
        setLayout(new BorderLayout());
        add(drawPanel, BorderLayout.CENTER);
        threadManager = new ThreadManager();

        drawPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                drawPanel.removeComponentListener(this);
                startGameLoop();
            }
        });
    }

    public void startGameLoop() {
        threadManager.setCanvasSize(drawPanel.getWidth(), drawPanel.getHeight());
        running = true;
        gameThread = new Thread(this::gameLoop);
        gameThread.start();
    }

    private void gameLoop() {
        final long targetDelay = 1000 / 60; // Target delay for 60 FPS
        long lastFpsDisplayTime = System.currentTimeMillis();

        while (running) {
            long startTime = System.currentTimeMillis();
            fpsCounter.update();

            if (System.currentTimeMillis() - lastFpsDisplayTime >= 500 && fpsCounter.getFPS() != 0) {
                drawPanel.setFps(fpsCounter.getFPS());
                threadManager.checkAndAdjustThread();
                lastFpsDisplayTime = System.currentTimeMillis();
            }

            updateAndRepaint();
            threadManager.updateProcessingTimes();

            long sleepTime = targetDelay - (System.currentTimeMillis() - startTime);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    running = false;
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void updateAndRepaint() {
        threadManager.updateParticles();
        threadManager.updateExplorer(); // Ensure explorer is updated
        SwingUtilities.invokeLater(drawPanel::repaint);
    }

    public void stopGameLoop() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public ThreadManager getThreadManager() {
        return threadManager;
    }

    public void refreshDisplay() {
        drawPanel.repaint();
    }

    private class DrawPanel extends JPanel {
        private double fpsToDisplay = 0;

        public void setFps(double fps) {
            this.fpsToDisplay = fps;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1280, 720);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public DrawPanel() {
            super();
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int canvasHeight = getHeight();

            g.setColor(Color.WHITE);
            threadManager.drawParticles(g, canvasHeight);
            if (threadManager.getExplorerCount() > 0) {
                threadManager.drawExplorer(g, canvasHeight);
                zoomToExplorer(g);
            }

            if (fpsToDisplay >= 60) {
                g.setColor(Color.GREEN);
            } else if (fpsToDisplay >= 30) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.RED);
            }

            g.drawString(String.format("FPS: %.2f", fpsToDisplay), 10, 20);
            g.setColor(Color.BLUE);
            g.drawString(String.format("Number of Particles: %d", threadManager.getParticleCount()), 10, 40);
        }

        private void zoomToExplorer(Graphics g) {
            Explorer explorer = threadManager.getExplorerController().getExplorer();
            if (explorer != null) {
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int explorerX = (int) explorer.getX();
                int explorerY = (int) explorer.getY();
                g.translate(centerX - explorerX, centerY - explorerY);
            }
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus();
        KeyListener explorerController = threadManager.getExplorerController();
        if (explorerController != null) {
            addKeyListener(explorerController); // Ensure the key listener is added
        }
    }
}