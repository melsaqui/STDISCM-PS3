import java.awt.Graphics;

public class Particle {
    private double x, y, vx, vy;

    public Particle(int x, int y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public void update(int canvasWidth, int canvasHeight) {
        x += vx;
        y += vy;

        // Bounce off the canvas edges
        if (x <= 0 || x >= canvasWidth) {
            vx *= -1;
        }
        if (y <= 0 || y >= canvasHeight) {
            vy *= -1;
        }
    }

    public void draw(Graphics g, int canvasHeight) {
        int invertedY = canvasHeight - (int) y - 10; // Adjust for particle size
        g.fillOval((int) x, invertedY, 10, 10);
    }
    /*void zoomedDraw(Graphics g, int canvasHeight){
        int invertedY = 720 - (int)y;
        g.fillOval((int)x, invertedY, 100, 100);
    }*/
}
