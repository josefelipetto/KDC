public class Peer {

    private int port;

    private int kdcID;

    private String key;

    public Peer(int port, int kdcID, String key) {

        this.port = port;

        this.kdcID = kdcID;

        this.key = key;
    }

    public void message(int destID, String message)
    {

    }

}
