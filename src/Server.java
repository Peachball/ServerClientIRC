import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9900);
        CopyOnWriteArrayList<Socket> client = new CopyOnWriteArrayList<Socket>();

        Thread clientKicker = new Thread(new ClientKicker(client));
        Thread clientAccepter = new Thread(new ClientAccepter(client, server));
        Thread broadcast = new Thread(new Broadcast(client));

        clientAccepter.start();
        clientKicker.start();
        broadcast.start();
    }
}

class ClientKicker implements Runnable {

    private List<Socket> clients;

    public ClientKicker(List<Socket> clients) {
        this.clients = clients;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (Socket client : clients) {
                if (client == null) {
                    clients.remove(client);
                    System.out.println("Client has been kicked");
                } else if (!client.isConnected()) {
                    clients.remove(client);
                    System.out.println("Client has been kicked");
                }

            }
            try {
                Thread.sleep(18);
            } catch (InterruptedException ex) {
                System.out.println("Why is this happening...");
            }
        }
    }

}

class ClientAccepter implements Runnable {

    private List<Socket> clients;
    private ServerSocket server;

    public ClientAccepter(List<Socket> clients, ServerSocket server) {
        this.clients = clients;
        this.server = server;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket buffer = server.accept();
                if (buffer != null) {
                    clients.add(buffer);
                    System.out.println("New client accepted");
                }
            } catch (IOException ex) {
                System.out.println("Failed to add client");
            }
            try {
                Thread.sleep(19);
            } catch (InterruptedException ex) {
                System.out.println("Why is this happening...");
            }
        }
    }

}

class Broadcast implements Runnable {

    public List<Socket> clients;

    public Broadcast(List<Socket> clients) {
        this.clients = clients;

    }

    @Override
    public void run() {
        System.out.println("Broadcast started");
        while (!Thread.currentThread().isInterrupted()) {
            for (Socket client : clients) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String buffer = reader.readLine();
                    System.out.println("Buffer recieved");
                    if (buffer != null) {
                        System.out.println("Input good");
                        for (Socket asdf : clients) {
                            PrintWriter output = new PrintWriter(asdf.getOutputStream());
                            output.println(buffer);
                            output.flush();
                        }
                    } else {
                        System.out.println("Input not good");
                    }

                } catch (IOException ex) {
                    clients.remove(client);
                    System.out.println("Client has been kicked");
                }

            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                System.out.println("Why is this happening...");
            }
        }
        System.out.println("Broadcast done");
    }

}
