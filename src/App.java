import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.*;
import telas.*;

public class App {
    public static int buffersize = 1024;
    public static DatagramSocket ds;
    public static byte[] buffer = new byte[buffersize];
    public static int serverPort = 998;
    public static int clientPort = 999;
    public static void main(String[] args) throws Exception {      
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {                
                    try {
                        ds = new DatagramSocket(clientPort);
                        new Exibicoes(ds);
                        //new Pagamento();
                    }  catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }  catch (SecurityException e) {
                        e.printStackTrace();
                    }/*   */ catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                
            }
        });
       
    }

}
