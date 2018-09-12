package Clients;

import Utils.AES;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;

public class ClientListener implements Runnable {

    private Client client;

    public ClientListener(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        try
        {
            ServerSocket serverSocket = new ServerSocket(this.client.getPort());

            while (true)
            {
                Socket clientSocket = serverSocket.accept();

                System.out.println(this.client.getName() + " received a connection. ");

                String message = this.receive(clientSocket);

                String[] command = this.decode(message);

                if(command[1].equalsIgnoreCase("KSESSION"))
                {
                    this.client.setkSession(command[2]);

                    int nonce = this.getNonce();

                    this.client.setExpectedNonceResult(nonce);

                    String checkCommand = this.client.getName() + "|CHECK|" + this.cifra(Integer.toString(nonce),this.client.getkSession()) + "|" + command[3];

                    this.send(clientSocket,checkCommand);

                    String peerResponse = this.receive(clientSocket);

                    // Continuar aqui
                }
                else if( command[1].equalsIgnoreCase("CHECK"))
                {
                    int nonce = this.client.check( Integer.parseInt(command[2]));

                    this.send(clientSocket,this.client.getName() + "|CHECKRESULT|" + Integer.toString(nonce));
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void send(Socket socket, String message) throws IOException
    {

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                socket.getOutputStream()
        );

        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        bufferedWriter.write(message);

        bufferedWriter.flush();
    }

    private String receive(Socket socket) throws IOException
    {
        InputStream inputStream = socket.getInputStream();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return bufferedReader.readLine();
    }


    private String[] decode(String command) throws UnsupportedEncodingException
    {
        return new String(Base64.getDecoder().decode(command.getBytes()),"UTF-8").split("\\|");
    }

    private int getNonce()
    {
        Random r = new Random();

        return r.nextInt(1000);

    }

    private String cifra(String message, String key) throws Exception
    {
        return new String(AES.cifra(message,key));
    }


}
