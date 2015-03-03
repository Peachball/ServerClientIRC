import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket client = new Socket("192.168.0.2", 9900);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());
        Thread messageListener = new Thread(new MessageListener(in));
        messageListener.start();
        Scanner input = new Scanner(System.in);
        while (true) {
            String buffer = input.nextLine();
            out.println(buffer);
            out.flush();
        }
    }
}

class MessageListener implements Runnable {

    private BufferedReader input;

    public MessageListener(BufferedReader input) {
        this.input = input;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                System.out.println(input.readLine());
            } catch (IOException ex) {
                System.out.println("recieved weird input");
            }
        }
    }

}
