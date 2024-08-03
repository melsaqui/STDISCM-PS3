import java.util.List;

public class ParticleEngine implements Runnable {
    private ParticleController particleController;
    private int canvasWidth, canvasHeight;
    private long lastProcessingTime = 0;
    private volatile boolean running = true; // Flag to control the running state

    public ParticleEngine(int canvasWidth, int canvasHeight) {
        this.particleController = new ParticleController();
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public ParticleEngine(int canvasWidth, int canvasHeight, List<Particle> particles) {
        this.particleController = new ParticleController();
        for (Particle particle : particles) {
            this.particleController.addParticle(particle);
        }
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    @Override
    public void run() {
        while (running) {
            long startTime = System.currentTimeMillis();
            particleController.updateParticles(canvasWidth, canvasHeight); // Update particle positions
            long endTime = System.currentTimeMillis();
            lastProcessingTime = endTime - startTime;

            try {
                Thread.sleep(16); // Roughly 60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void addParticle(Particle particle) {
        particleController.addParticle(particle);
    }

    public ParticleController getParticleController() {
        return particleController;
    }

    public long getLastProcessingTime() {
        return lastProcessingTime;
    }

    public void clearParticles() {
        particleController.clearParticles(); // Clear all particles
    }

    public void stop() {
        running = false; // Provide a method to stop the engine
    }
}
