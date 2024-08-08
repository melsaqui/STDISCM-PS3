import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.event.KeyListener;

import javax.swing.*;
import java.awt.*;

public class DriverExplorer extends JFrame {
   // private final ExplorerPanel explorerPanel;

    public DriverExplorer(){
       // explorerPanel = new ExplorerPanel();

        //initUI();
    }

   
    private void initUI(ExplorerPanel explorerPanel) {
        setTitle("Explorer Mode");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(explorerPanel);
        pack(); 
        setLocationRelativeTo(null);
        setResizable(false);
        setFocusable(true); 

        setFocusTraversalKeysEnabled(false); 

        setVisible(true);

    }
    public static void main(String[] args) {
       // EventQueue.invokeLater(() -> {
       try {
        ServerSocket server = new ServerSocket(5000);
        Socket s = server.accept();
        //ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        .//ThreadManager threadManager  = (ThreadManager) in.readObject();
            ThreadManager threadManager = new ThreadManager();
            //threadManager.addExplorer(new Explorer(400, 100));
            DriverExplorer driver =new DriverExplorer();
            driver.initUI(new ExplorerPanel(threadManager));
       } catch (Exception e) {
            System.out.println(e);
       }
       
        //});
    }
   
}
