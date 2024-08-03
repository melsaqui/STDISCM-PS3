import javax.swing.*;
import java.awt.*;

public class DriverDeveloper extends JFrame {
    private final SimulationPanel simulationPanel;

    public DriverDeveloper() {
        simulationPanel = new SimulationPanel();
        initUI();
    }

    private void initUI() {
        setTitle("Particle Physics Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(simulationPanel);
        pack(); 
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        //server
        EventQueue.invokeLater(() -> {
            DriverDeveloper driver = new DriverDeveloper();
            ControlPanel controlPanel = new ControlPanel(driver.simulationPanel.getThreadManager(),driver.simulationPanel);
            
            // Position ControlPanel to the right of Driver window
            controlPanel.setLocation( driver.getX()+driver.getWidth()-100, driver.getY()-100);
            controlPanel.setVisible(true);
        });
    }
}
