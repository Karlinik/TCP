import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by gc2karl on 3/23/2018.
 */
public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket socket = null;
        Socket clientSocket = null;

        try{
            socket = new ServerSocket(5321);
        } catch (IOException e) {
            System.err.println("Failed to listen on port: " + 5321 + ". Exit.");
            System.exit(1);
        }

        System.out.println("Waiting for robots at: " + socket.getLocalSocketAddress());

        while (true) {
            try{
                clientSocket = socket.accept();
            } catch (IOException e){
                System.err.println("Client was not accepted. Exit.");
                System.exit(1);
            }
            System.out.println("Connection accepted from: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            Thread thread = new Thread(new Median(clientSocket));
            thread.start();
        }
    }
}
