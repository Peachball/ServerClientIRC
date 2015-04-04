import java.io.Serializable;

public class Message implements Serializable {

    public Message(String message) {
        this.message = message;
        sender = (int) Math.round(Math.random() * 20);
    }
    public String message;
    public int sender;
}

class Coord implements Serializable {

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }
    int x;
    int y;
}
