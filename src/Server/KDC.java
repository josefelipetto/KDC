package Server;

import Utils.MessageBus;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class KDC implements Runnable{

    private List<Map<String,String>> keys = new ArrayList<>();

    private String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789!@#$%*()";

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

            print(" Received message: " + messageReceived );

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
            print("Não achou a chave no Server.KDC");
            return null;
        }

    }

    public List<Map<String, String>> getKeys() {
        return keys;
    }

    public String random()
    {
        return new Random().ints( 16,0,this.chars.length() )
                .mapToObj( i -> "" + this.chars.charAt(i) )
                .collect(Collectors.joining());
    }

    private static void print(String message)
    {
        System.out.println(message);
    }

}
