import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by gc2karl on 3/23/2018.
 */
public class Median implements Runnable{
    private Socket socket;
    private DataOutputStream out;
    private Scanner in;
    private Manipulator manipulator;

    Median(Socket socket) throws Exception {
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new Scanner(new InputStreamReader(socket.getInputStream()));
        manipulator = new Manipulator();

    }

    public void run(){
        try {
            //out.writeBytes(manipulator.getNextResponse("").getResponse());
            //out.flush();
            int i=0;
            while(true){
                System.out.println("starting while loop");

                socket.setSoTimeout(manipulator.getTimeout());
                //socket.setSoTimeout(1000);
                if(i>0){
                    System.out.println("i: " + i);
                    System.out.println("input: " + in.next());
                }
                Response response = null;

                if(in.hasNext() && !in.hasNext(".*\\b")){
                    //recieved whole message -> next tick of the statemachine
                    response = manipulator.getNextResponse(in.next());
                }else{
                    //recieved only part of the message -> give it to the statemachine to check if it is already too long, if not, we'll wait for the rest
                    response = manipulator.preValidate(in.next());
                }

                System.out.println(response);
                if (!response.getResponse().equals("")) {
                    out.writeBytes(response.getResponse());
                    out.flush();
                }
                if (response.endSession()) {
                    System.out.println("closed by the stateMachine");
                    break;
                }
                i++;
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
