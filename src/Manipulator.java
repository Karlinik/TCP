import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /* GET TIMEOUT */
    public int getTimeout(){
        return 1000 * (robot.isCharging() ? TIMEOUT_RECHARGING : TIMEOUT);
    }

    /* PARSE TO COORDINATES */
    public ArrayList<Integer> parse(String input){
        ArrayList<Integer> result = new ArrayList<>();
        if (!input.matches("OK -?\\d\\d? -?\\d\\d?"))
            return null;

        Pattern pattern = Pattern.compile("-?\\d+");
        Matcher matcher = pattern.matcher(input);

        while(matcher.find())
            result.add(Integer.parseInt(matcher.group()));

        return result;
    }

    /* SEMAPHORE */
    public Response getNextResponse(String input){
        input = buffer.concat(input);
        buffer = "";
        System.out.println("client: " + input);

        if(robot.isCharging())
            return charging(input);

        if(input.equals(Commands.CLIENT_RECHARGING.getValue())){
            robot.setCharging(true);
            return new Response("", state);
        }

        switch (state){
            case AUTENTIZATION:
                return authentication(input);
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
        return new Response("", state);
    }

    /* CHARGING */
    private Response charging(String input){
        if(!input.equals(Commands.CLIENT_FULL_POWER.getValue())){
            System.out.println("charging: " + Commands.SERVER_LOGIC_ERROR.getValue());
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue(), state);
        }
        robot.setCharging(false);

        return new Response("", state);
    }

    /* HASH CODE */
    private Integer countHashCode(String input){
        Integer hash = 0;
        char[] letters = input.toCharArray();
        for (char i : letters){
            if(i == '\r' || i == '\n')
                continue;
            hash += (int)i;
        }
        return (hash*1000) % 65536;
    }

    /* AUTHENTICATION */
    private Response authentication(String input){
        if(!checkLenght(input+delimiter)){
            return new Response(Commands.SERVER_SYNTAX_ERROR.getValue(), state);
        }
        this.userName = input;
        this.serverHashCode = (countHashCode(userName) + serverKey) % 65536;
        state = State.CLIENT_CONFIRMATION;

        return new Response(serverHashCode.toString(), state);
    }

    /* CHECK CLIENT CONFIRMATION */
    private Response checkClientConfirmation(String input){
        if(!checkLenght(input+delimiter)){
            System.out.println("checkClientConfirmation: " + Commands.SERVER_SYNTAX_ERROR.getValue());
            return new Response(Commands.SERVER_SYNTAX_ERROR.getValue(), state);
        }
        Integer hash = (countHashCode(userName) + clientKey) % 65536;
        if(!input.equals(hash.toString()))
            return new Response(true, Commands.SERVER_LOGIN_FAILED.getValue(), state);

        this.state = State.FIRST_MOVE;

        return new Response(Commands.SERVER_OK.getValue() + delimiter + Commands.SERVER_MOVE.getValue(), state);
    }

    /* FIRST MOVE */
    private Response firstMove(String input){
        if(!checkLenght(input+delimiter)){
            return new Response(Commands.SERVER_SYNTAX_ERROR.getValue(), state);
        }
        ArrayList<Integer> list = parse(input);
        Coordinates coordinates = new Coordinates(list.get(0), list.get(1));

        if(coordinates == null){
            System.out.println("firstMove: " + Commands.SERVER_LOGIC_ERROR.getValue());
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue(), state);
        }

        robot.setCoordinates(coordinates);
        this.state = State.SECOND_MOVE;

        return new Response(Commands.SERVER_MOVE.getValue(), state);
    }

    /* SECOND MOVE */
    private Response secondMove(String input){
        if(!checkLenght(input+delimiter)){
            return new Response(Commands.SERVER_SYNTAX_ERROR.getValue(), state);
        }
        ArrayList<Integer> list = parse(input);
        Coordinates coordinates = new Coordinates(list.get(0), list.get(1));

        if(coordinates == null){
            System.out.println("secondMove: " + Commands.SERVER_LOGIC_ERROR.getValue());
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue(), state);
        }

        if(coordinates.same(robot.getCoordinates()))
            return new Response(Commands.SERVER_MOVE.getValue(), state);

        robot.tellOrientation(coordinates);
        robot.setCoordinates(coordinates);

        if(coordinates.finishReached()){
            this.state = State.DESTINATION;
            return new Response(Commands.SERVER_PICK_UP.getValue(), state);
        }

        this.state = State.CORNER_NAVIGATION;

        Commands command = robot.getNextMove();

        return new Response(command.getValue(), state);
    }

    /* CORNER NAVIGATION */
    private Response cornerNavigation(String input){
        if(!checkLenght(input+delimiter))
            return new Response(Commands.SERVER_SYNTAX_ERROR.getValue(), state);

        ArrayList<Integer> list = parse(input);
        Coordinates coordinates = new Coordinates(list.get(0), list.get(1));

        if(coordinates == null){
            System.out.println("cornerNavigation: " + Commands.SERVER_LOGIC_ERROR.getValue());
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue(), state);
        }

        robot.setCoordinates(coordinates);

        if(coordinates.finishReached()){
            this.state = State.DESTINATION;
            return new Response(Commands.SERVER_PICK_UP.getValue(), state);
        }

        Commands command = robot.getNextMove();

        return new Response(command.getValue(), state);
    }

    /* NAVIGATION */
    private Response navigating(String input){
        System.out.println("navigating");
        if(!checkLenght(input+delimiter))
            return new Response(Commands.SERVER_SYNTAX_ERROR.getValue(), state);

        ArrayList<Integer> list = parse(input);
        Coordinates coordinates = new Coordinates(list.get(0), list.get(1));

        if(coordinates == null){
            System.out.println("navigating: " + Commands.SERVER_LOGIC_ERROR.getValue());
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue(), state);
        }

        if(coordinates.outOfDestination())
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue(), state);

        robot.setCoordinates(coordinates);

        this.state = State.DESTINATION;

        return new Response(Commands.SERVER_PICK_UP.getValue(), state);
    }

    /* RETRIEVE MESSAGE */
    private Response retrieveMessage(String input){
        if (input.equals("") || input.isEmpty()){
            this.state = State.NAVIGATION;
            return new Response(this.robot.getNextMoveDestination().getValue(), state);
        }

        if(!checkLenght(input+delimiter)){
            System.out.println("retrieveMessage: " + Commands.SERVER_SYNTAX_ERROR.getValue());
            return new Response(true, Commands.SERVER_SYNTAX_ERROR.getValue(), state);
        }

        return new Response(true, Commands.SERVER_LOGOUT.getValue(), state);
    }

    /* VALIDATE MESSAGE */
    private boolean checkLenght(String input){
        switch (state){
            case AUTENTIZATION:
                return input.length() <= State.AUTENTIZATION.getLenght();
            case CLIENT_CONFIRMATION:
                return (input.length() <= State.CLIENT_CONFIRMATION.getLenght() && input.matches("\\d{1}\\d?\\d?\\d?\\d?\7\b"));
            case FIRST_MOVE:
                return (input.length() <= State.FIRST_MOVE.getLenght() && input.matches("OK -?\\d\\d? -?\\d\\d?\7\b"));
            case SECOND_MOVE:
                return (input.length() <= State.SECOND_MOVE.getLenght() && input.matches("OK -?\\d\\d? -?\\d\\d?\7\b"));
            case CORNER_NAVIGATION:
                return (input.length() <= State.CORNER_NAVIGATION.getLenght() && input.matches("OK -?\\d\\d? -?\\d\\d?\7\b"));
            case NAVIGATION:
                return (input.length() <= State.NAVIGATION.getLenght() && input.matches("OK -?\\d\\d? -?\\d\\d?\7\b"));
            case DESTINATION:
                return input.length() <= State.DESTINATION.getLenght();
        }
        return true;
    }
}
