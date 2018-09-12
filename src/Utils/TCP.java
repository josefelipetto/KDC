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

    private Socket receiver;

    private Socket sender;

    public void send(String message, int portTo)
    {
        try
        {
            this.sender = new Socket("localhost",portTo);

            DataOutputStream dataOutputStream = new DataOutputStream(this.sender.getOutputStream());

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

            this.receiver = serverSocket.accept();

            DataInputStream dataInputStream = new DataInputStream(this.receiver.getInputStream());

            message = dataInputStream.readUTF();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return message;

    }

    public String sendAndReceive(String message, int portTo)
    {
        String response = null;

        try
        {
            this.sender = new Socket("localhost",portTo);

            DataOutputStream dataOutputStream = new DataOutputStream(this.sender.getOutputStream());

            dataOutputStream.writeUTF(message);

            DataInputStream dataInputStream = new DataInputStream(this.sender.getInputStream());

            response = dataInputStream.readUTF();

            this.sender.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public Socket getReceiver() {
        return receiver;
    }

    public Socket getSender() {
        return sender;
    }

    private int getPort()
    {
        return port;
    }
}
