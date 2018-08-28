package Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP {

    private int port;

    public TCP(int port)
    {
        this.port = port;
    }

    public void send(String message, int portTo)
    {
        try
        {
            Socket socket = new Socket("localhost",portTo);

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(message);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String receive()
    {
        String message = null;

        try
        {
            ServerSocket serverSocket = new ServerSocket(this.getPort());

            Socket socket = serverSocket.accept();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            message = dataInputStream.readUTF();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return message;

    }

    private int getPort()
    {
        return port;
    }
}
