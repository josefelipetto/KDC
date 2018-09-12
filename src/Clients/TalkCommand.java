package Clients;

import Utils.AES;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;

public class TalkCommand implements Commandable{

    private Client client;

    private String talkTo;

    public TalkCommand(Client client, String talkTo) {
        this.client = client;
        this.talkTo = talkTo;
    }

    @Override
    public void handle() {

        // find contact
        int destinationPort = this.getDestinationPort();

        if(destinationPort == 0)
        {
            System.out.println("Contact not found");
            return;
        }

        try
        {
            Socket serverSocket = new Socket(
                    InetAddress.getByName("localhost"),
                    this.client.getKdcPort()
            );

            this.sendCommand(serverSocket);

            String kdcAnswer = this.receive(serverSocket);

            System.out.println("KDC Answer: " + kdcAnswer);

            serverSocket.close();

            String[] decoded = this.decodeKdcAnswer(kdcAnswer);

            if(decoded[0].equalsIgnoreCase("KSESSION"))
            {

                String[] decodedMessage = decoded[1].split("_SEPARATOR_");

                this.setClientKSession(decodedMessage[0]);

                Socket destinationSocket = new Socket(
                        InetAddress.getByName("localhost"),
                        destinationPort
                );

                byte[] message = (this.talkTo + "|" + "KSESSION|" + this.client.getkSession() + "|" + this.client.getPort()).getBytes();

                this.send(
                        destinationSocket,
                        Base64.getEncoder().encodeToString(message)
                );

                String peerResponse = this.receive(destinationSocket);

                System.out.println("Response: " + peerResponse);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private int getDestinationPort()
    {
        return this.client.getDestPort(this.talkTo);
    }

    private void sendCommand(Socket serverSocket) throws Exception
    {

        byte[] ciphered = AES.cifra(this.talkTo.getBytes(),this.client.getKey());

        String base64Ciphered = Base64.getEncoder().encodeToString(ciphered);

        this.send(serverSocket, "TALK:" + this.client.getName() + "," + base64Ciphered );
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

    private String[] decodeKdcAnswer(String kdcAnswer)
    {
        return kdcAnswer.split("\\|");
    }

    private void setClientKSession(String kSession) throws Exception
    {

        this.client.setkSession(
                new String(
                        AES.decifra(
                                Base64.getDecoder().decode(kSession),
                                this.client.getKey()
                        ),
                        "UTF-8"
                )
        );
    }

}
