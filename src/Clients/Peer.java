package Clients;

import Utils.AES;
import Utils.TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Peer {

    private int port;

    private final int kdcPort = 6578;

    private String name;

    private TCP tcp;

    private String key;

    private String kSession;

    private List<Map<String,Integer>> contacts = new ArrayList<>();

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o nome,porta e chave, separados por , : ");

        String line = scanner.nextLine();

        String[] params = line.split(",");

        Peer peer = new Peer(Integer.parseInt(params[1]),params[0],params[2]);

        Thread listener = new Thread( new PeerListener(peer));

        listener.start();

        peer.run();


    }

    public Peer(int port, String name, String key) {

        this.port = port;

        this.name = name;

        this.key = key;

        contacts.add(Map.of("BOB",6580));

        contacts.add(Map.of("ALICE",6579));
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);

        this.tcp = new TCP(this.port);

        while (true)
        {
            System.out.print("Digite um comando: ");

            String command = scanner.nextLine();

            String[] args = command.split(" ");

            if(args[0].equals("talk"))
            {
                // find contact
                int destPort = this.getDestPort(args[1]);

                if(destPort == 0)
                {
                    System.out.println("Contato não encontrado");

                    continue;
                }

                try
                {
                    byte[] ciphered = AES.cifra(args[1].getBytes(),this.key);

                    String base64Ciphered = Base64.getEncoder().encodeToString(ciphered);

                    String kdcAnswer = this.tcp.sendAndReceive("TALK:" + this.name + "," + base64Ciphered,kdcPort);

                    System.out.println("Server.KDC Answer: " + kdcAnswer);

                    String[] decoded = kdcAnswer.split("\\|");

                    if(decoded[0].equalsIgnoreCase("KSESSION"))
                    {

                        String[] decodedMessage = decoded[1].split("_SEPARATOR_");

                        this.kSession = new String(

                                AES.decifra(
                                        Base64.getDecoder().decode(decodedMessage[0]),
                                        this.key
                                ),
                                "UTF-8"
                        );

                        int listPort = (this.getPort() + 500);

                        String response = this.tcp.sendAndReceive(Base64.getEncoder().encodeToString((args[1] + "|" + "KSESSION|" + this.kSession + "|" + listPort).getBytes()),destPort + 500);

                        System.out.println("Response: " + response);

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if(args[0].equals("contacts"))
            {
                this.contacts.stream().map(i -> i.entrySet().iterator().next()).forEach(System.out::println);
            }
        }
    }

    private int getDestPort(String name)
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

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getkSession() {
        return kSession;
    }

    public void setkSession(String kSession) {
        this.kSession = kSession;

        System.out.println("kSession setted to " + this.kSession);
    }

    public TCP getTcp() {
        return tcp;
    }

    public int check(int number)
    {
        return  number + 1;
    }

    static class PeerListener implements Runnable
    {
        private Peer peer;


        public PeerListener(Peer peer) {
            this.peer = peer;
        }

        @Override
        public void run() {

            try
            {
                ServerSocket svSocket = new ServerSocket(this.peer.getPort() + 500);

                Socket socket = null;

                while (true)
                {

                    socket = svSocket.accept();

                    System.out.println("Accepted");

                    (new ListenerHandler(this.peer,socket)).run();

                    socket.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


        }
    }

    static class ListenerHandler implements Runnable
    {
        private Peer peer;

        private Socket clientSocket;

        private int expected;

        public ListenerHandler(Peer peer, Socket clientSocket) {
            this.peer = peer;
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try
            {
                DataInputStream dataInputStream = new DataInputStream(this.clientSocket.getInputStream());

                String message = dataInputStream.readUTF();

                String[] args = new String(Base64.getDecoder().decode(message.getBytes()),"UTF-8").split("\\|");

                Arrays.stream(args).map(i -> i).forEach(System.out::println);

                if(args[0].equalsIgnoreCase(this.peer.getName()) && args[1].equalsIgnoreCase("KSESSION"))
                {
                    this.peer.setkSession(args[2]);

                    Random r = new Random();

                    int nonce = r.nextInt(1000);

                    this.expected = this.peer.check(nonce);

                    System.out.println("Expected: " + this.expected);

                    String command = "CHECK|" + new String(AES.cifra(Integer.toString(nonce),this.peer.getkSession())) + "|" + args[3];

                    System.out.println("client port " + args[3]);

                    this.peer.getTcp().send(Base64.getEncoder().encodeToString(command.getBytes()),Integer.parseInt(args[3]));

                }
                else if(args[0].equalsIgnoreCase("CHECK"))
                {
                    int result = this.peer.check(Integer.parseInt(args[1]));

                    this.peer.getTcp().send(Base64.getEncoder().encodeToString(("CHECKRESULT|" + new String(AES.cifra(Integer.toString(result),this.peer.getkSession()))).getBytes()),Integer.parseInt(args[2]));
                }
                else if(args[0].equalsIgnoreCase("CHECKRESULT"))
                {
                    System.out.println("CHECKgg:" + args[1]);
                }


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        private void respond(String response) throws IOException
        {
            DataOutputStream dataOutputStream = new DataOutputStream(this.clientSocket.getOutputStream());

            dataOutputStream.writeUTF(response);
        }
    }



}
