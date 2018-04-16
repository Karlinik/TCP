import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by gc2karl on 3/23/2018.
 */
public class Median implements Runnable{
    private String delimiter;
    private Socket socket;
    private DataOutputStream out;
    private Scanner in;
    private Manipulator manipulator;

    Median(Socket socket) throws Exception {
        delimiter = Character.toString((char)7) + Character.toString((char)8);
        this.socket = socket;
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new Scanner(new InputStreamReader(socket.getInputStream())).useDelimiter(delimiter);
        manipulator = new Manipulator();

    }

    public void run(){
        try {
            out.flush();
            while(true){

                socket.setSoTimeout(manipulator.getTimeout());

                Response response = null;

                if(in.hasNext() && !in.hasNext(".*\b")){
                    response = manipulator.getNextResponse(in.next());
                }else{
                    response = manipulator.preValidate(in.next());
                }

                System.out.println("response: " + response.getResponse());
                if (!response.getResponse().equals("")) {
                    out.writeBytes(response.getResponse() + delimiter);
                    out.flush();
                }
                if (response.endSession()) {
                    System.out.println("closed by the stateMachine");
                    break;
                }
            }
        } catch(Exception e){
            System.out.println("Error: " + e);
        }

        close();
    }

    private void close(){
        try{
            System.out.println("Closing connection");
            socket.close();
            out.close();
            in.close();
        }catch(Exception e){
            System.out.println("Can't close all resources: " + e);
        }
    }
}
