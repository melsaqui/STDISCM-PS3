import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager implements serializable{
    private final List<ParticleEngine> processors = new CopyOnWriteArrayList<>();
    private ExplorerEngine explorerEngine;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private int canvasWidth, canvasHeight;
    private int particleCount = 0;
    private int explorerCount = 0;
    private int roundRobinIndex = 0;
    private long lastAverageProcessingTime = 0;
    private final List<Long> processingTimesHistory = new ArrayList<>();
    private static final int PROCESSING_TIME_HISTORY_SIZE = 20;
    private int lastParticleCountAtThreadAddition = 0;

    public void setCanvasSize(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        if (processors.isEmpty()) {
            addProcessor();
        }
    }

    public void clearParticles() {
        for (ParticleEngine engine : processors) {
            engine.clearParticles();
        }
        particleCount = 0;
    }

    private void addProcessor() {
        ParticleEngine engine = new ParticleEngine(canvasWidth, canvasHeight);
        processors.add(engine);
        executorService.execute(engine);
        lastParticleCountAtThreadAddition = particleCount;
    }

    private void addProcessor(List<Particle> particles) {
        ParticleEngine engine = new ParticleEngine(canvasWidth, canvasHeight, particles);
        processors.add(engine);
        executorService.execute(engine);
        lastParticleCountAtThreadAddition = particleCount;
    }

    public void checkAndAdjustThread() {
        if (shouldAddThread()) {
            redistributeParticles();
        }
    }

    public int getExplorerCount() {
        return explorerCount;
    }

    private boolean shouldAddThread() {
        boolean processingTimeIncreasing = false;
        if (!processingTimesHistory.isEmpty() && particleCount >= 1000) {
            long currentAverageProcessingTime = processingTimesHistory.get(processingTimesHistory.size() - 1);
            processingTimeIncreasing = currentAverageProcessingTime > lastAverageProcessingTime;
        }
        boolean significantParticleIncrease = particleCount >= lastParticleCountAtThreadAddition * 1.10;
        return processingTimeIncreasing && processors.size() < Runtime.getRuntime().availableProcessors() && particleCount != 0 && significantParticleIncrease;
    }

    public synchronized void addParticle(Particle particle) {
        if (processors.isEmpty()) {
            addProcessor();
        }
        particleCount++;
        ParticleEngine selectedProcessor = processors.get(roundRobinIndex);
        selectedProcessor.addParticle(particle);
        roundRobinIndex = (roundRobinIndex + 1) % processors.size();
    }

    public synchronized void addExplorer(Explorer explorer) {
        explorerEngine = new ExplorerEngine(canvasWidth, canvasHeight, explorer);
        explorerEngine.addExplorer(explorer);
        explorerCount++;
    }

    public void addParticles(List<Particle> particles) {
        if (processors.isEmpty()) {
            addProcessor();
        }
        for (Particle particle : particles) {
            addParticle(particle);
        }
    }

    public void updateParticles() {
        for (ParticleEngine processor : processors) {
            executorService.submit(processor::run);
        }
    }

    public void updateExplorer() {
        if (explorerEngine != null) {
            executorService.submit(explorerEngine::run);
        }
    }

    public void drawParticles(Graphics g, int canvasHeight) {
        for (ParticleEngine processor : processors) {
            processor.getParticleController().drawParticles(g, canvasHeight);
        }
    }

    public void drawExplorer(Graphics g, int canvasHeight) {
        if (explorerEngine != null) {
            explorerEngine.getExplorerController().drawExplorer(g, canvasHeight);
        }
    }

    public int getParticleCount() {
        return particleCount;
    }

    private void redistributeParticles() {
        int processorCount = processors.size();
        List<Particle> newParticles = new ArrayList<>();
        for (ParticleEngine processor : processors) {
            List<Particle> extractedParticles = processor.getParticleController().getParticles();
            int popCount = extractedParticles.size() / (processorCount + 1);
            newParticles.addAll(extractedParticles.subList(0, popCount));
            extractedParticles.removeAll(newParticles);
        }
        addProcessor(newParticles);
    }

    public void updateProcessingTimes() {
        long totalProcessingTime = 0;
        for (ParticleEngine processor : processors) {
            totalProcessingTime += processor.getLastProcessingTime();
        }
        long currentAverageProcessingTime = totalProcessingTime / processors.size();
        processingTimesHistory.add(currentAverageProcessingTime);
        if (processingTimesHistory.size() > PROCESSING_TIME_HISTORY_SIZE) {
            processingTimesHistory.remove(0);
        }
        lastAverageProcessingTime = processingTimesHistory.stream().mapToLong(Long::longValue).sum() / processingTimesHistory.size();
    }

    public ExplorerController getExplorerController() {
        return explorerEngine != null ? explorerEngine.getExplorerController() : null;
    }
}