/**
 * Created by Nikola Karlikova on 25.03.2018.
 */
public enum Commands {
    /*Server messages*/
    SERVER_MOVE ("102 MOVE"),
    SERVER_TURN_LEFT ("103 TURN LEFT"),
    SERVER_TURN_RIGHT ("104 TURN RIGHT"),
    SERVER_PICK_UP ("105 GET MESSAGE"),
    SERVER_LOGOUT ("106 LOGOUT"),
    SERVER_OK ("200 OK"),
    SERVER_LOGIN_FAILED ("300 LOGIN FAILED"),
    SERVER_SYNTAX_ERROR ("301 SYNTAX ERROR"),
    SERVER_LOGIC_ERROR ("302 LOGIC ERROR"),

    /*Client messages*/
    CLIENT_RECHARGING ("RECHARGING"),
    CLIENT_FULL_POWER ("FULL POWER");

    Commands(String value){ this.value = value; }

    private String value;

    public String getValue(){
        return this.value;
    }
}
