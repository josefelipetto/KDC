package Clients;

import Utils.AES;
import Utils.MessageBus;

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
                System.out.println("Client " + this.client.getName() + " waiting.. ");

                Socket clientSocket = serverSocket.accept();

                MessageBus clientBus = new MessageBus(clientSocket);

                System.out.println(this.client.getName() + " received a connection. ");

                String message = clientBus.receive();

                String[] command = this.decode(message);


                if(command[1].equalsIgnoreCase("KSESSION"))
                {

                    String receivedKey = new String(AES.decifra(Base64.getDecoder().decode(command[2]),this.client.getKey()),"UTF-8");

                    this.client.setkSession(receivedKey);

                    int nonce = this.getNonce();

                    System.out.println(this.client.getName() + " gerou um nonce = " + nonce );

                    this.client.setExpectedNonceResult(nonce);

                    System.out.println("O nonce esperado por " + this.client.getName() + " = " + this.client.getExpectedNonceResult() );

                    String checkCommand = this.cifra(Integer.toString(nonce),this.client.getkSession());

                    clientBus.send(checkCommand);

                    String peerResponse = clientBus.receive();

                    // Continuar aqui
                    System.out.println("O Outro peer mandou " + (peerResponse.split("\\|"))[1]);

                    if(this.client.getExpectedNonceResult() != Integer.parseInt((peerResponse.split("\\|"))[1]) )
                    {
                        System.out.println(" A verificação falhou ");
                        clientBus.send("FAILED");
                        continue;
                    }

                    System.out.println("A checagem está completa");

                    clientBus.send("TRUE");

                }
                else if( command[0].equalsIgnoreCase("CHECK"))
                {
                    System.out.println("CHECK");

                    int nonce = this.client.check( Integer.parseInt(command[1]));

                    clientBus.send("CHECKRESULT|" + Integer.toString(nonce));
                }
                else if(command[0].equalsIgnoreCase("FAILED") || command[0].equalsIgnoreCase("SUCCESS"))
                {
                    System.out.println("Worked ? " + command[0]);
                }
                else
                {
                    System.out.println(" Client: " + this.client.getName() + ". Command " + command[1] + " not found");
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
        return Base64.getEncoder().encodeToString(AES.cifra(message,key));
    }


}
