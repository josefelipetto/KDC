package Server;

import Utils.MessageBus;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class KDC implements Runnable{

    private List<Map<String,String>> keys = new ArrayList<>();



    private Socket clientSocket;

    public KDC(Socket clientSocket)
    {
        keys.add(Map.of("Alice","thisispeerOneKey"));

        keys.add(Map.of("Bob","thisispeerTwoKey"));

        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try
        {
            MessageBus clientBus = new MessageBus(this.clientSocket);

            String messageReceived = clientBus.receive();

            String[] command = messageReceived.split(":");

            if(command[0].equals("TALK"))
            {
                (new TalkCommand(this.clientSocket,this,command[1].split(","))).handle();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public String getKey(String name)
    {
        try
        {
            return this.getKeys().stream()
                    .filter(c -> c.entrySet().iterator().next().getKey().toUpperCase().equals(name.toUpperCase()) )
                    .findAny()
                    .orElse(null)
                    .entrySet()
                    .iterator()
                    .next()
                    .getValue();
        }
        catch (NullPointerException e)
        {
            System.out.println(" Couldnt find user key. ");
            return null;
        }
    }

    public List<Map<String, String>> getKeys() {
        return keys;
    }

    public String random()
    {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789!@#$%*()";

        return new Random().ints( 16,0,chars.length() )
                .mapToObj( i -> "" + chars.charAt(i) )
                .collect(Collectors.joining());
    }

}
