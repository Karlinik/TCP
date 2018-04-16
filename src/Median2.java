import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by gc2karl on 3/23/2018.
 */
public class Median2 implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private DataInputStream in;
    private Manipulator manipulator;

    Median2(Socket socket) throws Exception {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        manipulator = new Manipulator();

    }

    public void run(){
        try {
            out.flush();
            while(true){
                System.out.println("starting while loop");
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(manipulator.getTimeout());

                Response response = null;

                StringBuilder input = new StringBuilder();
                int ch;
                while((ch = in.read()) != -1){
                    input.append((char)ch);
                }

                String message = input.toString();

                if(message.contains("\b"))
                    response = manipulator.getNextResponse(message);
                else
                    response = manipulator.preValidate(message);

                System.out.println(response.getResponse());
                if (!response.getResponse().equals("")) {
                    out.print(response.getResponse() + "\7\b");
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
            socket.close();
            out.close();
            in.close();
        }catch(Exception e){
            System.out.println("Can't close all resources: " + e);

        }
    }
}
