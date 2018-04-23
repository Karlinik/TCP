/**
 * Created by gc2karl on 3/23/2018.
 */
public class Response {
    private String response;
    private boolean endSession;
    private State state;

    Response(boolean closeAfter, String response, State state){
        this.endSession = closeAfter;
        this.response = response;
        this.state = state;
    }

    Response(String response, State state){
        this(false, response, state);
    }

    public String getResponse(){
        return this.response;
    }

    public boolean endSession(){
        return endSession;
    }

    public State getState(){ return this.state; }
}
