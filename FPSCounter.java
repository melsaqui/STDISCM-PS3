public class FPSCounter {
    private long lastTimeCheck; 
    private int frameCount; 
    private int fps; 

    public FPSCounter() {
        lastTimeCheck = System.currentTimeMillis();
        frameCount = 0;
        fps = 0;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastTimeCheck;

        frameCount++;

        if (elapsedTime >= 1000) { // If one second has passed
            fps = frameCount; // Update FPS count
            frameCount = 0; // Reset frame count
            lastTimeCheck = currentTime; // Reset last time check
        }
    }

    public int getFPS() {
        return fps;
    }
}
