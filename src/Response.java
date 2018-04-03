/**
 * Created by gc2karl on 3/23/2018.
 */
public class Response {
    private String response;
    private boolean endSession;

    Response(boolean closeAfter, String response){
        this.response = response;
        this.endSession = closeAfter;
    }

    Response(String response){
        this(false, response);
    }

    public String getResponse(){
        return this.response;
    }

    public boolean endSession(){
        return endSession;
    }
}
