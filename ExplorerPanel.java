import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ExplorerPanel extends JPanel{
    private final DrawPanel drawPanel;
    private final ThreadManager threadManager;
    private Thread gameThread;
    private volatile boolean running = false;
    private FPSCounter fpsTracker = new FPSCounter();

    public ExplorerPanel(ThreadManager threadManager) {
        drawPanel = new DrawPanel();
        setLayout(new BorderLayout());
        add(drawPanel, BorderLayout.CENTER);
        this.threadManager=threadManager;
        threadManager.addExplorer(new Explorer(400, 100));

        drawPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                drawPanel.removeComponentListener(this);
                startGameLoop();
            }
        });
    }
    public ExplorerPanel() {
        drawPanel = new DrawPanel();
        setLayout(new BorderLayout());
        add(drawPanel, BorderLayout.CENTER);
        //this.threadManager=threadManager;
        this.threadManager = new ThreadManager();
        threadManager.addExplorer(new Explorer(400, 100));
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
    private void listen_dev(){
        try {
        ServerSocket server = new ServerSocket(5000);
        Socket s = server.accept();
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        double[][] from_dev= (double[][])in.readObject();
        for(int i =0;i<from_dev.length;i++){
            threadManager.addParticle(new Particle((int)from_dev[i][0],(int)from_dev[i][1],from_dev[i][2],from_dev[i][3]));

        }
            //ThreadManager threadManager = new ThreadManager();
            
       } catch (Exception e) {
            System.out.println(e);
       }
    }
    private void gameLoop() {
        listen_dev();
        final long targetDelay = 1000 / 60; 
        long lastFpsDisplayTime = System.currentTimeMillis(); 

        while (running) {
            long now = System.currentTimeMillis();
            fpsTracker.update();
            if (now - lastFpsDisplayTime >= 500 && fpsTracker.getFPS() != 0) {
                drawPanel.setFps(fpsTracker.getFPS());
                threadManager.checkAndAdjustThread();
                lastFpsDisplayTime = now;
            }

            updateAndRepaint();
            threadManager.updateProcessingTimes();

            long sleepTime = targetDelay - (System.currentTimeMillis() - now);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        }
    }

    private void updateAndRepaint() {
        threadManager.updateParticles();
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
        drawPanel.repaint(); // This will cause the DrawPanel to redraw and update the particle count display
    }


    private class DrawPanel extends JPanel {
        private double fpsToDisplay = 0;

        public void setFps(double fps) {
            this.fpsToDisplay = fps;
        }

        @Override
        public Dimension getPreferredSize() {
            
            return new Dimension(1080, 720);
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
            setBackground(Color.GRAY);
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
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int canvasHeight = getHeight();
            threadManager.drawParticles(g, canvasHeight);

            g.setColor(Color.WHITE);
            if (threadManager.getExplorerCount()>0){
             //   threadManager.drawParticles(g, canvasHeight);
                threadManager.drawExplorer(g, canvasHeight);
                 zoomToExplorer(g);
            }
            if (fpsToDisplay >= 60){
                g.setColor(Color.GREEN);
            } else if (fpsToDisplay >= 30){
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.RED);
            }
            
            g.drawString(String.format("FPS: %.2f", fpsToDisplay), 10, 20);
            g.setColor(Color.BLUE);
            g.drawString(String.format("Number of Particles: %d", threadManager.getParticleCount()), 100, 20);
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
