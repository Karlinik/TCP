/**
 * Created by Nikola Karlikova on 25.03.2018.
 */
public enum Commands {
    /*Server messages*/
    SERVER_MOVE ("102 MOVE \7\b"),
    SERVER_TURN_LEFT ("103 TURN LEFT\7\b"),
    SERVER_TURN_RIGHT ("104 TURN RIGHT\7\b"),
    SERVER_PICK_UP ("105 GET MESSAGE\7\b"),
    SERVER_LOGOUT ("106 LOGOUT\7\b"),
    SERVER_OK ("200 OK\7\b"),
    SERVER_LOGIN_FAILED ("300 LOGIN FAILED\7\b"),
    SERVER_SYNTAX_ERROR ("301 SYNTAX ERROR\7\b"),
    SERVER_LOGIC_ERROR ("302 LOGIC ERROR\7\b"),

    /*Client messages*/
    CLIENT_RECHARGING ("RECHARGING\7\b"),
    CLIENT_FULL_POWER ("FULL POWER\7\b");

    Commands(String value){ this.value = value; }

    private String value;

    public String getValue(){
        return this.value;
    }
}
