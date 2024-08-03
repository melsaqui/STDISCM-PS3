import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.JFrame;

public class DriverExplorer extends JFrame{
    private final ExplorerPanel explorerPanel;

    public DriverExplorer(){
        explorerPanel = new ExplorerPanel(null);
        initUI();
    }

    private void initUI() {
        setTitle("Explorer Mode");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(explorerPanel);
        pack(); 
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            //client this is not yet working
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1", 5001), 1000);
            System.out.println("Connection Successful!");
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            //dataOut.writeUTF("Hello, This is coming from Client!");
           // Threadmanager serverMessage = dataIn.readAllBytes();
            //System.out.println(serverMessage);

            dataIn.close();
            dataOut.close();
            socket.close();
            //DriverExplorer driver = new DriverExlorer();
            //ControlPanel controlPanel = new ControlPanel(driver.simulationPanel.getThreadManager(),driver.simulationPanel);
           // driver.explorerPanel
            // Position ControlPanel to the right of Driver window
            //controlPanel.setLocation( driver.getX()+driver.getWidth()-100, driver.getY()-100);
            //controlPanel.setVisible(true);
        });
    }
}
