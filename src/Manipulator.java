
/**
 * Created by gc2karl on 3/23/2018.
 */
public class Manipulator {
    private final Integer serverKey = 54621;
    private final Integer clientKey = 45328;
    private Integer serverHashCode;
    private String userName;
    private String delimiter;
    /*Time constants*/
    private final Integer TIMEOUT = 1;
    private final Integer TIMEOUT_RECHARGING = 5;

    private Robot robot;
    private String buffer;
    private State state;


    public Manipulator(){
        delimiter = Character.toString((char)7) + Character.toString((char)8);
        this.robot = new Robot();
        this.buffer = "";
        this.state = State.AUTENTIZATION;
    }

    public Integer getTimeout(){
        return 1000 * (robot.isCharging() ? TIMEOUT_RECHARGING : TIMEOUT);
    }

    public Response preValidate(String input){
        System.out.println("prevalidating");
        this.buffer = this.buffer.concat(input);
        if(!checkLenght(buffer))
            return new Response(Commands.SERVER_SYNTAX_ERROR.getValue());
        return new Response("");
    }

    public Response validateResponse(String input){
        if(input.contains("\7\b"))
            return getNextResponse(input);
        else
            return preValidate(input);
    }

    public Response getNextResponse(String input){
        System.out.println("get next response");
        //two message separation
        /*for(int i=0;i<input.length();i++){
            buffer+=(input.charAt(i));
            if(input.charAt(i) == '\b'){
                input = buffer;
                buffer="";
                buffer.concat(input.substring(i+1));
                break;
            }
        }*/
        input = buffer.concat(input);
        buffer = "";
        System.out.println("message: " + input);

        if(robot.isCharging()){
            return charging(input);
        }

        if(input.equals(Commands.CLIENT_RECHARGING.getValue())){
            robot.setCharging(true);
            System.out.println("command charging: ");
            return new Response("");
        }

        switch (state){
            case AUTENTIZATION:
                return autentization(input);
            case CLIENT_CONFIRMATION:
                return checkClientConfirmation(input);
            case FIRST_MOVE:
                return firstMove(input);
            case SECOND_MOVE:
                return secondMove(input);
            case CORNER_NAVIGATION:
                return cornerNavigation(input);
            case NAVIGATION:
                return navigating(input);
            case DESTINATION:
                return retrieveMessage(input);

        }
        return new Response("");
    }

    private Response charging(String input){
        if(!input.equals(Commands.CLIENT_FULL_POWER.getValue())){
            System.out.println("charging: " + Commands.SERVER_LOGIC_ERROR.getValue());
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue());
        }
        robot.setCharging(false);

        System.out.println("charging: ");
        return new Response("");
    }

    private Integer countHashCode(String input){
        Integer hash = 0;
        char[] letters = input.toCharArray();
        for (char i : letters){
            if(i == ' ' || i == '\t' || i == '\r' || i == '\n')
                continue;
            hash += (int)i;
        }
        return (hash*1000) % 65536;
    }

    private Response autentization(String input){
        if(!checkLenght(input)){
            System.out.println("autentization: " + Commands.SERVER_SYNTAX_ERROR.getValue());
            return new Response(Commands.SERVER_SYNTAX_ERROR.getValue());
        }
        this.userName = input;
        this.serverHashCode = (countHashCode(userName) + serverKey) % 65536;
        state = State.CLIENT_CONFIRMATION;

        System.out.println("autentization: " + serverHashCode.toString());
        return new Response(serverHashCode.toString());
    }

    private Response checkClientConfirmation(String input){
        Integer hash = (countHashCode(userName) + clientKey) % 65536;
        System.out.println("client hash code: " + hash);
        if(!input.equals(hash.toString())){
            System.out.println("checkClientConfirmation: " + Commands.SERVER_LOGIN_FAILED.getValue());
            return new Response(true, Commands.SERVER_LOGIN_FAILED.getValue());
        }
        this.state = State.FIRST_MOVE;

        System.out.println("checkClientConfirmation: " + Commands.SERVER_OK.getValue() + Commands.SERVER_MOVE.getValue());
        return new Response(Commands.SERVER_OK.getValue() + delimiter + Commands.SERVER_MOVE.getValue());
    }

    /*
    * FIRST MOVE
    * */
    private Response firstMove(String input){
        Coordinates coordinates = new Coordinates(input);
        if(coordinates == null){
            System.out.println("firstMove: " + Commands.SERVER_LOGIC_ERROR.getValue());
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue());
        }

        if(coordinates.finishReached()){
            this.state = State.DESTINATION;
            System.out.println("firstMove: pickup: " + Commands.SERVER_PICK_UP.getValue());
            return new Response(Commands.SERVER_PICK_UP.getValue());
        }

        robot.setCoordinates(coordinates);
        this.state = State.SECOND_MOVE;

        System.out.println("firstMove: move: " + Commands.SERVER_MOVE.getValue());
        return new Response(Commands.SERVER_MOVE.getValue());
    }

    /*
    * SECOND MOVE
    * */
    private Response secondMove(String input){
        Coordinates coordinates = new Coordinates(input);
        if(coordinates == null){
            System.out.println("secondMove: " + Commands.SERVER_LOGIC_ERROR.getValue());
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue());
        }

        if(coordinates.finishReached()){
            this.state = State.DESTINATION;
            System.out.println("secondMove: pickup: " + Commands.SERVER_PICK_UP.getValue());
            return new Response(Commands.SERVER_PICK_UP.getValue());
        }

        if(!robot.canTellOrientation(coordinates)){
            System.out.println("secondMove: move: " + Commands.SERVER_MOVE.getValue());
            return new Response(Commands.SERVER_MOVE.getValue());
        }

        robot.setCoordinates(coordinates);
        this.state = State.CORNER_NAVIGATION;

        if(!robot.getNextMove()){
            robot.rotate();
            System.out.println("secondMove: turn: " + Commands.SERVER_TURN_RIGHT.getValue());
            return new Response(Commands.SERVER_TURN_RIGHT.getValue());
        }

        System.out.println("secondMove: move: " + Commands.SERVER_MOVE.getValue());
        return new Response(Commands.SERVER_MOVE.getValue());
    }

    /*
    * CORNER NAVIGATION
    * */
    private Response cornerNavigation(String input){
        Coordinates coordinates = new Coordinates(input);
        if(coordinates == null){
            System.out.println("cornerNavigation: " + Commands.SERVER_LOGIC_ERROR.getValue());
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue());
        }

        if(coordinates.finishReached()){
            this.state = State.DESTINATION;
            System.out.println("cornerNavigation: pickup: " + Commands.SERVER_PICK_UP.getValue());
            return new Response(Commands.SERVER_PICK_UP.getValue());
        }

        robot.setCoordinates(coordinates);
        if(!robot.getNextMove()){
            robot.rotate();
            System.out.println("cornerNavigation: turn: " + Commands.SERVER_TURN_RIGHT.getValue());
            return new Response(Commands.SERVER_TURN_RIGHT.getValue());
        }

        System.out.println("cornerNavigation: move: " + Commands.SERVER_MOVE.getValue());
        return new Response(Commands.SERVER_MOVE.getValue());
    }

    /*
    * NAVIGATION
    * */
    private Response navigating(String input){
        Coordinates coordinates = new Coordinates(input);
        if(coordinates == null){
            System.out.println("navigating: " + Commands.SERVER_LOGIC_ERROR.getValue());
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue());
        }

        robot.setCoordinates(coordinates);
        this.state = State.DESTINATION;
        System.out.println("navigating: pickup: " + Commands.SERVER_PICK_UP.getValue());
        return new Response(Commands.SERVER_PICK_UP.getValue());
    }

    /*
    * RETRIEVE MESSAGE
    * */
    private Response retrieveMessage(String input){
        if (input.equals("")){
            this.state = State.NAVIGATION;
            System.out.println("retrieveMessage: nextdest: " + this.robot.getNextMoveDestination().getValue());
            return new Response(this.robot.getNextMoveDestination().getValue());
        }
        if(!checkLenght(input)){
            System.out.println("retrieveMessage: " + Commands.SERVER_SYNTAX_ERROR.getValue());
            return new Response(true, Commands.SERVER_SYNTAX_ERROR.getValue());
        }

        System.out.println("retrieveMessage: logout: " + Commands.SERVER_LOGOUT.getValue());
        return new Response(true, Commands.SERVER_LOGOUT.getValue());
    }

    private boolean checkLenght(String input){
        switch (state){
            case AUTENTIZATION:
                return input.length() <= 12;
            case CLIENT_CONFIRMATION:
                return input.length() <=7;
            case FIRST_MOVE:
                return input.length() <= 12;
            case SECOND_MOVE:
                return input.length() <= 12;
            case CORNER_NAVIGATION:
                return input.length() <= 12;
            case NAVIGATION:
                return input.length() <= 12;
            case DESTINATION:
                return input.length() <=100;
        }
        return true;
    }
}
