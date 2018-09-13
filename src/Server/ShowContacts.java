package Server;

import java.util.List;
import java.util.Map;

public class ShowContacts implements Commandable{

    private List<Map<String,Integer>> contacts;

    public ShowContacts(List<Map<String, Integer>> contacts) {
        this.contacts = contacts;
    }

    @Override
    public void handle() {
        this.contacts.forEach(System.out::println);
    }
}
