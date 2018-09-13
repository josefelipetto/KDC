package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener {

    private static final int serverPort = 6578;

    public static void main(String[] args)
    {
        Listener listener = new Listener();

        listener.startServer();
    }

    public void startServer() {
        ServerSocket serverSocket = null;

        Socket clientSocket = null;

        try
        {
            serverSocket = new ServerSocket(serverPort);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Listener initiated! ");

        while (true)
        {
            try
            {
                clientSocket = serverSocket.accept();

                // Assign this connection to a thread
                (new KDC(clientSocket)).run();
            }
            catch (IOException | NullPointerException e)
            {
                e.printStackTrace();
            }

        }
    }
}
