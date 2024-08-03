import java.util.List;

public class ExplorerEngine implements Runnable {
    private ExplorerController explorerController;
    private int canvasWidth, canvasHeight;
    private long lastProcessingTime = 0;
    private volatile boolean running = true; // Add a flag to control the running state

    public ExplorerEngine(int canvasWidth, int canvasHeight) {
        this.explorerController = new ExplorerController();
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public ExplorerEngine(int canvasWidth, int canvasHeight, Explorer explorer) {
        this(canvasWidth, canvasHeight); // Call the other constructor
        addExplorer(explorer);
    }

    @Override
    public void run() {
        while (running) {
            long startTime = System.currentTimeMillis();
            explorerController.updateExplorer(canvasWidth, canvasHeight); // Assuming no walls to manage
            long endTime = System.currentTimeMillis();
            lastProcessingTime = endTime - startTime;

            try {
                Thread.sleep(16); // Roughly 60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void addExplorer(Explorer explorer) {
        explorerController.addExplorer(explorer);
    }

    public ExplorerController getExplorerController() {
        return explorerController;
    }

    public long getLastProcessingTime() {
        return lastProcessingTime;
    }

    public void stop() {
        running = false; // Provide a method to stop the engine
    }
}