package Clients;

import Utils.AES;
import Utils.MessageBus;

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

            MessageBus messageBus = new MessageBus(serverSocket);

            byte[] ciphered = AES.cifra(this.talkTo.getBytes(),this.client.getKey());

            String base64Ciphered = Base64.getEncoder().encodeToString(ciphered);

            messageBus.send("TALK:" + this.client.getName() + "," + base64Ciphered );

            String kdcAnswer = messageBus.receive();

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

                MessageBus destinationBus = new MessageBus(destinationSocket);

                byte[] message = (this.talkTo + "|" + "KSESSION|" + decodedMessage[1]).getBytes();

                destinationBus.send( Base64.getEncoder().encodeToString(message) );

                String peerResponse = destinationBus.receive();

                String decodedResponse = new String(AES.decifra(Base64.getDecoder().decode(peerResponse),this.client.getkSession()),"UTF-8");

                System.out.println(this.client.getName() + " recebeu o número " + decodedResponse );

                int nonce = this.client.check(Integer.parseInt(decodedResponse));

                System.out.println(this.client.getName() + " passou o nonce pela função de chegacem e obteve " + nonce);

                destinationBus.send("CHECKRESULT|" + Integer.toString(nonce));

                String worked = destinationBus.receive();

                System.out.println(worked.equals("TRUE") ? "Comunicação realizada com sucesso. " : " Falha na comunicação ");
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
