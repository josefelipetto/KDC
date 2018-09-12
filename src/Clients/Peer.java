//package Clients;
//
//import Utils.AES;
//import Utils.TCP;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.*;
//
//public class Peer {
//
//    private int port;
//
//    private final int kdcPort = 6578;
//
//    private String name;
//
//    private TCP tcp;
//
//    private String key;
//
//    private String kSession;
//
//    private List<Map<String,Integer>> contacts = new ArrayList<>();
//
//    public void run() {
//
//        if(args[0].equals("contacts"))
//        {
//            this.contacts.stream().map(i -> i.entrySet().iterator().next()).forEach(System.out::println);
//        }
//    }
//
//    static class PeerListener implements Runnable
//    {
//        private Peer peer;
//
//
//        public PeerListener(Peer peer) {
//            this.peer = peer;
//        }
//
//        @Override
//        public void run() {
//
//            try
//            {
//                ServerSocket svSocket = new ServerSocket(this.peer.getPort() + 500);
//
//                Socket socket = null;
//
//                while (true)
//                {
//
//                    socket = svSocket.accept();
//
//                    System.out.println("Accepted");
//
//                    (new ListenerHandler(this.peer,socket)).run();
//
//                    socket.close();
//                }
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//
//
//        }
//    }
//
//    static class ListenerHandler implements Runnable
//    {
//        private Peer peer;
//
//        private Socket clientSocket;
//
//        private int expected;
//
//        public ListenerHandler(Peer peer, Socket clientSocket) {
//            this.peer = peer;
//            this.clientSocket = clientSocket;
//        }
//
//        @Override
//        public void run() {
//            try
//            {
//
//                if(args[0].equalsIgnoreCase(this.peer.getName()) && args[1].equalsIgnoreCase("KSESSION"))
//                {
//
//                    this.peer.getTcp().send(Base64.getEncoder().encodeToString(command.getBytes()),Integer.parseInt(args[3]));
//
//                }
//                else if(args[0].equalsIgnoreCase("CHECK"))
//                {
//                    int result = this.peer.check(Integer.parseInt(args[1]));
//
//                    this.peer.getTcp().send(Base64.getEncoder().encodeToString(("CHECKRESULT|" + new String(AES.cifra(Integer.toString(result),this.peer.getkSession()))).getBytes()),Integer.parseInt(args[2]));
//                }
//                else if(args[0].equalsIgnoreCase("CHECKRESULT"))
//                {
//                    System.out.println("CHECKgg:" + args[1]);
//                }
//
//
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//
//        }
//
//        private void respond(String response) throws IOException
//        {
//            DataOutputStream dataOutputStream = new DataOutputStream(this.clientSocket.getOutputStream());
//
//            dataOutputStream.writeUTF(response);
//        }
//    }
//
//
//
//}
