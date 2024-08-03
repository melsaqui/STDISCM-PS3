import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.EmptyBorder;

public class ControlPanel extends JFrame {
    private final ThreadManager threadManager;
    private final JTextArea feedbackTextArea;
    private final List<String> feedbackMessages = new ArrayList<>();
    private SimulationPanel currentFrame;


    public ControlPanel(ThreadManager threadManager,SimulationPanel sim) {
        this.currentFrame =sim;
        this.threadManager = threadManager;
        setTitle("Particle Controls");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(750, 400);
    
        feedbackTextArea = new JTextArea(10, 50);
        feedbackTextArea.setEditable(false);
        JScrollPane feedbackScrollPane = new JScrollPane(feedbackTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
        JLabel feedbackTitle = new JLabel("Output");
        feedbackTitle.setHorizontalAlignment(JLabel.CENTER);
        feedbackTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        feedbackTitle.setOpaque(true);
        feedbackTitle.setBackground(Color.WHITE);
    
        JPanel feedbackPanel = new JPanel(new BorderLayout());
        feedbackPanel.add(feedbackTitle, BorderLayout.NORTH);
        feedbackPanel.add(feedbackScrollPane, BorderLayout.CENTER);
    
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(createParticleInputPanel());
        mainPanel.add(createBatchAdditionPanel()); // Add batch addition panel
        setupClearButton(mainPanel); // Pass the mainPanel when calling the method.
        //setUpExplorer(mainPanel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.NORTH);
        getContentPane().add(feedbackPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private void setupClearButton(JPanel mainPanel) {
        JButton clearButton = new JButton("Clear Particles");
        clearButton.addActionListener(e -> {
            threadManager.clearParticles(); // Clear all particles in ThreadManager
            clearFeedbackDisplay(); // Clear the output panel
        });
    
        JPanel clearButtonPanel = new JPanel();
        clearButtonPanel.add(clearButton);
        mainPanel.add(clearButtonPanel, BorderLayout.SOUTH); // Add clear button at the bottom of the main panel
    }

    private void setUpExplorer(JPanel mainPanel) {
        JButton explorerButton = new JButton("Explorer Mode");
        explorerButton.addActionListener(e -> {
            dispose();
            threadManager.addExplorer(new Explorer(100, 500));
            ExplorerPanel explorerMode = new ExplorerPanel(this.threadManager);
            DriverDeveloper driver = (DriverDeveloper) SwingUtilities.getWindowAncestor(currentFrame);
            driver.remove(currentFrame);
            driver.add(explorerMode);
            driver.pack();
            driver.setVisible(true);
            dispose();
        });

        JPanel explorerButtonPanel = new JPanel();
        explorerButtonPanel.add(explorerButton);
        mainPanel.add(explorerButtonPanel, BorderLayout.SOUTH); 
    }
    
    private void clearFeedbackDisplay() {
        feedbackMessages.clear(); // Clear the list of messages
        feedbackTextArea.setText(""); // Reset the content of the feedback text area
    }

    private JPanel createParticleInputPanel() {
        JPanel particlePanel = new JPanel(new BorderLayout());
        JPanel particleInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel particleButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel particleLabel = new JLabel("Add particles");
        particleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JTextField numberInput = new JTextField("1", 5);
        JTextField xInput = new JTextField(5);
        JTextField yInput = new JTextField(5);
        JTextField angleInput = new JTextField(5);
        JTextField velocityInput = new JTextField(5);
        JButton addButton = new JButton("Add by Particle");

        particleInputPanel.add(new JLabel("No. of Particles:"));
        particleInputPanel.add(numberInput);
        particleInputPanel.add(new JLabel("X:"));
        particleInputPanel.add(xInput);
        particleInputPanel.add(new JLabel("Y:"));
        particleInputPanel.add(yInput);
        particleInputPanel.add(new JLabel("Angle:"));
        particleInputPanel.add(angleInput);
        particleInputPanel.add(new JLabel("Velocity:"));
        particleInputPanel.add(velocityInput);

        particleButtonPanel.add(addButton);

        particlePanel.add(particleLabel, BorderLayout.NORTH);
        particlePanel.add(particleInputPanel, BorderLayout.CENTER);
        particlePanel.add(particleButtonPanel, BorderLayout.EAST);

        addButton.addActionListener(e -> handleParticleAddition(e, numberInput, xInput, yInput, angleInput, velocityInput));
        return particlePanel;
    }

    private JPanel createBatchAdditionPanel() {
        JPanel batchPanel = new JPanel(new GridLayout(3, 1));

        // Batch by Position
        JPanel byPositionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        byPositionPanel.add(new JLabel("No. of Particles:"));
        JTextField numberInput = new JTextField(5);
        byPositionPanel.add(numberInput);
        byPositionPanel.add(new JLabel("Start X:"));
        JTextField startXInput = new JTextField(5);
        byPositionPanel.add(startXInput);
        byPositionPanel.add(new JLabel("End X:"));
        JTextField endXInput = new JTextField(5);
        byPositionPanel.add(endXInput);
        byPositionPanel.add(new JLabel("Y:"));
        JTextField yInput = new JTextField(5);
        byPositionPanel.add(yInput);
        byPositionPanel.add(new JLabel("Velocity:"));
        JTextField velocityInput = new JTextField(5);
        byPositionPanel.add(velocityInput);
        byPositionPanel.add(new JLabel("Angle:"));
        JTextField angleInput = new JTextField(5);
        byPositionPanel.add(angleInput);
        JButton addByPositionButton = new JButton("Add by Position");
        addByPositionButton.addActionListener(e -> handleBatchAdditionByPosition(e, numberInput, startXInput, endXInput, yInput, velocityInput, angleInput));
        byPositionPanel.add(addByPositionButton);
        batchPanel.add(byPositionPanel);

        // Batch by Angle
        JPanel byAnglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        byAnglePanel.add(new JLabel("No. of Particles:"));
        JTextField numberInput2 = new JTextField(5);
        byAnglePanel.add(numberInput2);
        byAnglePanel.add(new JLabel("X:"));
        JTextField xInput2 = new JTextField(5);
        byAnglePanel.add(xInput2);
        byAnglePanel.add(new JLabel("Y:"));
        JTextField yInput2 = new JTextField(5);
        byAnglePanel.add(yInput2);
        byAnglePanel.add(new JLabel("Velocity:"));
        JTextField velocityInput2 = new JTextField(5);
        byAnglePanel.add(velocityInput2);
        byAnglePanel.add(new JLabel("Start Angle:"));
        JTextField startAngleInput = new JTextField(5);
        byAnglePanel.add(startAngleInput);
        byAnglePanel.add(new JLabel("End Angle:"));
        JTextField endAngleInput = new JTextField(5);
        byAnglePanel.add(endAngleInput);
        JButton addByAngleButton = new JButton("Add by Angle");
        addByAngleButton.addActionListener(e -> handleBatchAdditionByAngle(e, numberInput2, xInput2, yInput2, velocityInput2, startAngleInput, endAngleInput));
        byAnglePanel.add(addByAngleButton);
        batchPanel.add(byAnglePanel);

        // Batch by Velocity
        JPanel byVelocityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        byVelocityPanel.add(new JLabel("No. of Particles:"));
        JTextField numberInput3 = new JTextField(5);
        byVelocityPanel.add(numberInput3);
        byVelocityPanel.add(new JLabel("X:"));
        JTextField xInput3 = new JTextField(5);
        byVelocityPanel.add(xInput3);
        byVelocityPanel.add(new JLabel("Y:"));
        JTextField yInput3 = new JTextField(5);
        byVelocityPanel.add(yInput3);
        byVelocityPanel.add(new JLabel("Angle:"));
        JTextField angleInput3 = new JTextField(5);
        byVelocityPanel.add(angleInput3);
        byVelocityPanel.add(new JLabel("Start Velocity:"));
        JTextField startVelocityInput = new JTextField(5);
        byVelocityPanel.add(startVelocityInput);
        byVelocityPanel.add(new JLabel("End Velocity:"));
        JTextField endVelocityInput = new JTextField(5);
        byVelocityPanel.add(endVelocityInput);
        JButton addByVelocityButton = new JButton("Add by Velocity");
        addByVelocityButton.addActionListener(e -> handleBatchAdditionByVelocity(e, numberInput3, xInput3, yInput3, angleInput3, startVelocityInput, endVelocityInput));
        byVelocityPanel.add(addByVelocityButton);
        batchPanel.add(byVelocityPanel);

        return batchPanel;
    }

    private void handleParticleAddition(ActionEvent e, JTextField numberInput, JTextField xInput, JTextField yInput, JTextField angleInput, JTextField velocityInput) {
        try {
            int number = Integer.parseInt(numberInput.getText());
            int x = Integer.parseInt(xInput.getText());
            int y = Integer.parseInt(yInput.getText());
            double angle = Math.toRadians(Double.parseDouble(angleInput.getText()));
            double velocity = Double.parseDouble(velocityInput.getText());

            for (int i = 0; i < number; i++) {
                // Optional: Add a small offset to prevent overlap
                threadManager.addParticle(new Particle(x + i, y + i, Math.cos(angle) * velocity, Math.sin(angle) * velocity));
            }
            String feedback = "Added " + number + " particles at (" + x + ", " + y + ") with angle " + Math.toDegrees(angle) + "° and velocity " + velocity;
            updateFeedbackDisplay(feedback);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    private void handleBatchAdditionByPosition(ActionEvent e, JTextField numberInput, JTextField startXInput, JTextField endXInput, JTextField yInput, JTextField velocityInput, JTextField angleInput) {
        try {
            int number = Integer.parseInt(numberInput.getText());
            double startX = Double.parseDouble(startXInput.getText());
            double endX = Double.parseDouble(endXInput.getText());
            double y = Double.parseDouble(yInput.getText());
            double velocity = Double.parseDouble(velocityInput.getText());
            double angle = Math.toRadians(Double.parseDouble(angleInput.getText()));

            double stepX = (endX - startX) / (number - 1);

            for (int i = 0; i < number; i++) {
                double x = startX + i * stepX;
                // Optional: Add a small offset to prevent overlap
                threadManager.addParticle(new Particle((int) x + i, (int) y + i, Math.cos(angle) * velocity, Math.sin(angle) * velocity));
            }
            updateFeedbackDisplay("Added " + number + " particles from (" + startX + ", " + y + ") to (" + endX + ", " + y + ") with angle " + Math.toDegrees(angle) + "° and velocity " + velocity);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBatchAdditionByAngle(ActionEvent e, JTextField numberInput, JTextField xInput, JTextField yInput, JTextField velocityInput, JTextField startAngleInput, JTextField endAngleInput) {
        try {
            int number = Integer.parseInt(numberInput.getText());
            double x = Double.parseDouble(xInput.getText());
            double y = Double.parseDouble(yInput.getText());
            double velocity = Double.parseDouble(velocityInput.getText());
            double startAngle = Math.toRadians(Double.parseDouble(startAngleInput.getText()));
            double endAngle = Math.toRadians(Double.parseDouble(endAngleInput.getText()));

            double stepAngle = (endAngle - startAngle) / (number - 1);

            for (int i = 0; i < number; i++) {
                double angle = startAngle + i * stepAngle;
                // Optional: Add a small offset to prevent overlap
                threadManager.addParticle(new Particle((int) x + i, (int) y + i, Math.cos(angle) * velocity, Math.sin(angle) * velocity));
            }
            updateFeedbackDisplay("Added " + number + " particles at (" + x + ", " + y + ") with angles from " + Math.toDegrees(startAngle) + "° to " + Math.toDegrees(endAngle) + "° and velocity " + velocity);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBatchAdditionByVelocity(ActionEvent e, JTextField numberInput, JTextField xInput, JTextField yInput, JTextField angleInput, JTextField startVelocityInput, JTextField endVelocityInput) {
        try {
            int number = Integer.parseInt(numberInput.getText());
            double x = Double.parseDouble(xInput.getText());
            double y = Double.parseDouble(yInput.getText());
            double angle = Math.toRadians(Double.parseDouble(angleInput.getText()));
            double startVelocity = Double.parseDouble(startVelocityInput.getText());
            double endVelocity = Double.parseDouble(endVelocityInput.getText());

            double stepVelocity = (endVelocity - startVelocity) / (number - 1);

            for (int i = 0; i < number; i++) {
                double velocity = startVelocity + i * stepVelocity;
                // Optional: Add a small offset to prevent overlap
                threadManager.addParticle(new Particle((int) x + i, (int) y + i, Math.cos(angle) * velocity, Math.sin(angle) * velocity));
            }
            updateFeedbackDisplay("Added " + number + " particles at (" + x + ", " + y + ") with angle " + Math.toDegrees(angle) + "° and velocities from " + startVelocity + " to " + endVelocity);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFeedbackDisplay(String feedback) {
        feedbackMessages.add(feedback);
        StringBuilder feedbackContent = new StringBuilder();
        for (int i = feedbackMessages.size() - 1; i >= 0; i--) {
            feedbackContent.append(feedbackMessages.get(i)).append("\n");
        }
        feedbackTextArea.setText(feedbackContent.toString());
    }
}
