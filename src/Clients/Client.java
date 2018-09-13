package Clients;

import Server.ShowContacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Client {

    private int port;

    private final int kdcPort = 6578;

    private String key;

    private String name;

    private String kSession;

    private int expectedNonceResult;

    private List<Map<String,Integer>> contacts = new ArrayList<>();

    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        Client client = new Client();

        client.start();
    }

    public Client() {

        contacts.add(Map.of("BOB",6580));

        contacts.add(Map.of("ALICE",6579));

        this.readInput();

        Thread thread = new Thread( new ClientListener(this) );

        thread.start();

    }

    public void start()
    {
        while (true)
        {
            String[] command = this.readCommand();

            if(command[0].equalsIgnoreCase("talk"))
            {
                (new TalkCommand(this, command[1])).handle();
            }
            else if(command[0].equalsIgnoreCase("show") && command[1].equalsIgnoreCase("contacts"))
            {
                (new ShowContacts(this.contacts)).handle();
            }
            else if(command[0].equalsIgnoreCase("show") && command[1].equalsIgnoreCase("commands"))
            {
                this.displayCommands();
            }

        }
    }

    public int getDestPort(String name)
    {
        try {
            return  contacts.stream()
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
            return 0;
        }

    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getKdcPort() {
        return kdcPort;
    }

    public String getkSession() {
        return kSession;
    }

    public void setkSession(String kSession) {
        this.kSession = kSession;
    }

    public int getPort() {
        return port;
    }

    public int check(int number)
    {
        return  number + 1;
    }

    public int getExpectedNonceResult() {
        return expectedNonceResult;
    }

    public void setExpectedNonceResult(int expectedNonceResult) {
        this.expectedNonceResult = this.check(expectedNonceResult);
    }

    private void readInput()
    {

        System.out.print("Enter user name, port and key, separated by comma  : ");

        String[] params = this.scanner.nextLine().split(",");

        this.name = params[0];

        this.port = Integer.parseInt( params[1] );

        this.key = params[2];

    }

    private String[] readCommand()
    {

        System.out.print("\nEnter a command: ");

        String command = this.scanner.nextLine();

        return command.split(" ");
    }

    private void displayCommands()
    {
        System.out.println(" ============= Available commands ==============");
        System.out.println(" 1 - talk [peerName]");
        System.out.println(" ===============================================");
    }


}
