package Clients;

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

    public Client() {

        contacts.add(Map.of("BOB",6580));

        contacts.add(Map.of("ALICE",6579));

        this.readInput();

        Thread thread = new Thread( new ClientListener(this) );

        thread.run();

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

        System.out.print("Digite o nome,porta e chave, separados por , : ");

        String[] params = this.scanner.nextLine().split(",");

        this.name = params[0];

        this.port = Integer.parseInt( params[1] );

        this.kSession = params[2];

    }

    private String[] readCommand()
    {

        System.out.print("Digite um comando: ");

        String command = this.scanner.nextLine();

        return command.split(" ");
    }


}
