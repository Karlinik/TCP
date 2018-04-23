import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by gc2karl on 3/23/2018.
 */
public class Median2 implements Runnable{
    private String delimiter;
    private Socket socket;
    private DataOutputStream out;
    private Scanner in;
    private Manipulator manipulator;
    private Response response;

    Median2(Socket socket) throws Exception {
        delimiter = Character.toString((char)7) + Character.toString((char)8);
        this.socket = socket;
        try{
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new Scanner(new InputStreamReader(socket.getInputStream())).useDelimiter(delimiter);
        } catch(IOException e){
            System.err.println("IO error while getting Input or Output Stream");
            return;
        }
        manipulator = new Manipulator();
        response = new Response("", State.AUTENTIZATION);
    }

    public void run(){
        try {
            out.flush();
            while(true){

                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(manipulator.getTimeout());
                Thread.sleep(manipulator.getTimeout());

                Response response = null;

                if(in.hasNext() && !in.hasNext(".*\\z")){
                    System.out.println("has next");
                    response = manipulator.getNextResponse(in.next());
                }
                else{
                    response = manipulator.getNextResponse("");
                }
                /*if(in.hasNext() && !in.hasNext(".*\\z")){
                    response = manipulator.getNextResponse(in.next());
                }else{
                    response = manipulator.preValidate(in.next());
                }*/

                System.out.println("response: " + response.getResponse());

                this.response = response;

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
