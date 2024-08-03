import java.awt.Color;
import java.awt.Graphics;

public class Explorer {
    double x, y, vx, vy;

    public Explorer(int x, int y) {
        this.x = x;
        this.y = y;
        this.vx = 0; // Initialize velocity to 0
        this.vy = 0; // Initialize velocity to 0
    }

    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    void update(int canvasWidth, int canvasHeight) {
        x += vx;
        y += vy;

        // Boundary conditions to stop the explorer at the edges
        if (x < 0) {
            x = 0;
        } else if (x > canvasWidth - 15) {
            x = canvasWidth - 15;
        }

        if (y < 0) {
            y = 0;
        } else if (y > canvasHeight - 15) {
            y = canvasHeight - 15;
        }
    }

    void draw(Graphics g, int canvasHeight) {
        int invertedY = canvasHeight - (int) y - 15;
        g.setColor(Color.RED);
        g.fillRect((int) x, invertedY, 15, 15);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}