import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    
    Socket client;
    ObjectInputStream reader;
    ObjectOutputStream sender;
    
    public Client(String ip, int port) throws IOException{
        client = new Socket(ip,port);
        reader = new ObjectInputStream(client.getInputStream());
        sender = new ObjectOutputStream(client.getOutputStream());
    }
    
    public Client(Socket client) throws IOException{
        this.client = client;
        reader = new ObjectInputStream(client.getInputStream());
    }

    public static void main(String[] args) throws IOException {
        Socket client = new Socket("192.168.0.4", 9900);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
        Thread messageListener = new Thread(new MessageListener(new Client(client)));
        messageListener.start();
        Scanner input = new Scanner(System.in);
        while (true) {
            Message buffer = new Message(input.nextLine());
            out.writeObject(buffer);
            out.flush();
        }
    }
}

class MessageListener implements Runnable {

    private Client client;

    public MessageListener(Client input) {
        client = input;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ObjectInputStream in = client.reader;
                Object buffer = in.readObject();
                if(buffer instanceof Message){
                    System.out.println(((Message) buffer).message + ((Message) buffer).sender);
                }
            } catch (IOException ex) {
                System.out.println("recieved weird input");
            } catch (ClassNotFoundException ex) {
                System.out.println("Found no class?");
            }
        }
    }

}
