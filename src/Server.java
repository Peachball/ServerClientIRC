import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    public static void main(String[] args) throws IOException {
        final Object clientLock = new Object();
        ServerSocket server = new ServerSocket(9900);
        List<Client> client = new CopyOnWriteArrayList<Client>();

        Thread clientKicker = new Thread(new ClientKicker(client));
        Thread clientAccepter = new Thread(new ClientAccepter(client, server));
        Thread broadcast = new Thread(new Broadcast(client));

        clientAccepter.start();
        clientKicker.start();
        broadcast.start();
    }
}

class ClientKicker implements Runnable {

    private List<Client> clients;

    public ClientKicker(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (Client client : clients) {
                if (client == null) {
                    clients.remove(client);
                    System.out.println("Client has been kicked");
                } else if (!client.client.isConnected()) {
                    clients.remove(client);
                    System.out.println("Client has been kicked");
                }

                try {
                    Thread.sleep(18);
                } catch (InterruptedException ex) {
                    System.out.println("Why is this happening...");
                }
            }
        }
    }

}

class ClientAccepter implements Runnable {

    private List<Client> clients;
    private ServerSocket server;

    public ClientAccepter(List<Client> clients, ServerSocket server) {
        this.clients = clients;
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("Client Accepter on");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket buffer = server.accept();
                if (buffer != null) {
                    clients.add(new Client(buffer));
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

    public List<Client> clients;

    public Broadcast(List<Client> clients) {
        this.clients = clients;

    }

    @Override
    public void run() {
        System.out.println("Broadcast started");
        while (!Thread.currentThread().isInterrupted()) {
            for (Client client : clients) {
                try {
                    ObjectInputStream reader = client.reader;
                    if (reader.available() <= 0) {
                        continue;
                    }
                    Object buffer = reader.readObject();
                    System.out.println("Buffer recieved");
                    if (buffer != null) {
                        System.out.println("Input good");
                        for (Client asdf : clients) {
                            ObjectOutputStream output = new ObjectOutputStream(asdf.client.getOutputStream());
                            output.writeObject(buffer);
                            output.flush();
                        }
                    } else {
                        System.out.println("Input not good B");
                    }
                } catch (IOException ex) {
                    try {
                        client.client.close();
                        clients.remove(client);
                        System.out.println("Client has been kicked from reader?");
                    } catch (IOException ex1) {
                        System.out.println("DAFU IS HAPPENING HERE");
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Broadcast.class.getName()).log(Level.SEVERE, null, ex);
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
