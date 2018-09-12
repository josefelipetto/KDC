package Utils;

import java.io.*;
import java.net.Socket;

public class MessageBus {

    private Socket socket;

    public MessageBus(Socket socket) {
        this.socket = socket;
    }

    public void send(String message) throws IOException
    {
        DataOutputStream dataOutputStream = new DataOutputStream(this.socket.getOutputStream());

        dataOutputStream.writeUTF(message);
    }

    public String receive() throws IOException
    {
        DataInputStream dataInputStream = new DataInputStream(this.socket.getInputStream());

        return dataInputStream.readUTF();
    }
}
