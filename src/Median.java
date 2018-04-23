import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by gc2karl on 3/23/2018.
 */
public class Median implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Manipulator manipulator;
    private String delimiter;
    private State state;

    Median(Socket socket){
        try{
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream());
        } catch(IOException e){
            System.err.println("IO error while getting Input or Output Stream");
            return;
        }
        this.socket = socket;
        manipulator = new Manipulator();
        delimiter = Character.toString((char)7) + Character.toString((char)8);
        state = State.AUTENTIZATION;
    }

    public void run(){
        try {
            out.flush();
            while(socket.isConnected()){
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);

                Response response = null;

                String input = "";
                int ch;
                socket.setSoTimeout(manipulator.getTimeout());

                while((ch = in.read()) != -1){
                    System.out.print((char)ch);
                    input+=(char)ch;
                    if(input.endsWith("\7\b"))
                        break;

                    if(input.length() >= state.getLenght()){
                        if(!input.contains("RECHAR") && !input.contains("FULL")){
                            out.print(Commands.SERVER_SYNTAX_ERROR.getValue() + delimiter);
                            out.flush();
                            input = "";
                            break;
                        }
                    }
                }

                String message = input.substring(0,input.length()-2);

                if(message.equals("") && !state.equals(State.DESTINATION))
                    break;

                response = manipulator.getNextResponse(message);

                this.state = response.getState();
                if (!response.getResponse().equals("")) {
                    out.print(response.getResponse() + delimiter);
                    out.flush();
                }
                if (response.endSession()) {
                    System.out.println("closed by server side");
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