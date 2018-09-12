package Server;

import Utils.AES;
import Utils.MessageBus;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Base64;

public class TalkCommand implements Commandable {

    private Socket clientSocket;

    private KDC kdc;

    private String[] params;

    public TalkCommand(Socket clientSocket, KDC kdc, String[] params) {
        this.clientSocket = clientSocket;
        this.kdc = kdc;
        this.params = params;
    }


    @Override
    public void handle() {

        try
        {
            MessageBus clientBus = new MessageBus(this.clientSocket);

            String senderKey = this.getUserKey();

            if(senderKey == null)
            {
                clientBus.send("User not found");
                return ;
            }

            String destination = new String(
                    AES.decifra(Base64.getDecoder().decode(this.params[1]),senderKey),
                    "UTF-8"
            );

            String destinationKey = this.getDestinationKey(destination);

            if(destinationKey == null)
            {
                clientBus.send("Destination not found");
                return;
            }

            String kSession = this.generateKSession(senderKey,destinationKey);

            clientBus.send("KSESSION|"+kSession);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                this.clientSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private String getUserKey()
    {

        return this.kdc.getKey(this.params[0]);
    }

    private String getDestinationKey(String destination)
    {
        return this.kdc.getKey(destination);
    }

    private String generateKSession(String senderKey, String destinationKey) throws Exception
    {
        String random = this.kdc.random();

        return Base64.getEncoder().encodeToString(AES.cifra(random,senderKey))
                + "_SEPARATOR_"
                + Base64.getEncoder().encodeToString(AES.cifra(random,destinationKey));
    }

}
