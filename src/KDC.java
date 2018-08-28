import java.util.List;
import java.util.Map;

public class KDC {

    private List<Map<Integer,String>> keys;

    public KDC()
    {
        // This experiment does not focus on the first key exchange between peers and KDC, that uses asymmetric keys.
        // With that being said, we're going to use hard-coded keys for now

        keys.add(Map.of(1,"thisispeerOneKey"));
        keys.add(Map.of(2,"thisispeerTwoKey"));
    }

}
