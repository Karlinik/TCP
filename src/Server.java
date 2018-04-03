import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by gc2karl on 3/23/2018.
 */
public class Server {
    public static void main(String[] args){
        ServerSocket socket = null;
        try{
            socket = new ServerSocket(5321);
        } catch (IOException e) {
            System.out.println("Can't open socket: " + e);
            return;
        }

        System.out.println("Waiting for robots at: " + socket.getLocalSocketAddress());

        while (true) {
            Socket clientSocket = null;
            try{
                clientSocket = socket.accept();
            } catch (IOException e){
                System.out.println("Can't accept connection: " + e);
                return;
            }
            System.out.println("Connection accepted from: " + clientSocket.getInetAddress());

            Median median = null;
            try{
                median = new Median(clientSocket);
            }catch(Exception e){
                System.out.println("Can't initialize Median: " + e);
            }

            new Thread(median).start();
            System.out.println("Starting thread");
        }

    }
}
