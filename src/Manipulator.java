
/**
 * Created by gc2karl on 3/23/2018.
 */
public class Manipulator {
    private final Integer serverKey = 54621;
    private final Integer clientKey = 45328;
    private Integer serverHashCode;

    /*Time constants*/
    private final Integer TIMEOUT = 1;
    private final Integer TIMEOUT_RECHARGING = 5;

    private Robot robot;
    private String buffer;
    private State state;

    public Manipulator(){
        this.robot = new Robot();
        this.buffer = "";
        this.state = State.AUTENTIZATION;
    }

    public Integer getTimeout(){
        return 1000 * (robot.isCharging() ? TIMEOUT_RECHARGING : TIMEOUT);
    }

    public Response preValidate(String input){
        this.buffer = this.buffer.concat(input);
        if(!checkLenght(buffer))
            return new Response(Commands.SERVER_SYNTAX_ERROR.getValue());
        return new Response("");
    }

    public Response getNextResponse(String input){
        input = buffer.concat(input);
        buffer = "";
        System.out.println("response getted" + input);

        if(robot.isCharging()){
            return charging(input);
        }

        if(input.equals(Commands.CLIENT_RECHARGING.getValue())){
            robot.setCharging(true);
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
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue());
        }
        robot.setCharging(false);
        return new Response("");
    }

    private Integer countHashCode(String input, Integer key){
        Integer hash = 0;
        char[] letters = input.toCharArray();
        for (char i : letters)
            hash += (int)i;
        hash-=15;
        return (hash*1000) % 65536;
    }

    private Response autentization(String input){
        System.out.println("autentization: " + input);
        if(!checkLenght(input))
            return new Response(Commands.SERVER_SYNTAX_ERROR.getValue());
        this.serverHashCode = (countHashCode(input,serverKey) + serverKey) % 65536;
        state = State.CLIENT_CONFIRMATION;
        return new Response(serverHashCode.toString() + "\7\b");
    }

    private Response checkClientConfirmation(String input){
        System.out.println("client confirmation" + input);
        Integer hash = (countHashCode(input,clientKey) + serverKey) % 65536;
        System.out.println("client hash code" + hash);
        if(!serverHashCode.equals(hash))
            return new Response(true, Commands.SERVER_LOGIN_FAILED.getValue());
        this.state = State.FIRST_MOVE;
        return new Response(Commands.SERVER_OK.getValue() + Commands.SERVER_MOVE.getValue());
    }

    private Response firstMove(String input){
        Coordinates coordinates = new Coordinates(input);
        if(coordinates == null)
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue());

        if(coordinates.finishReached()){
            this.state = State.DESTINATION;
            return new Response(Commands.SERVER_PICK_UP.getValue());
        }

        System.out.println("First coordinates picked: " + coordinates);
        robot.setCoordinates(coordinates);
        this.state = State.SECOND_MOVE;
        return new Response(Commands.SERVER_MOVE.getValue());
    }

    private Response secondMove(String input){
        Coordinates coordinates = new Coordinates(input);
        if(coordinates == null)
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue());

        if(coordinates.finishReached()){
            this.state = State.DESTINATION;
            return new Response(Commands.SERVER_PICK_UP.getValue());
        }

        if(!robot.canTellOrientation(coordinates))
            return new Response(Commands.SERVER_MOVE.getValue());

        robot.setCoordinates(coordinates);
        this.state = State.CORNER_NAVIGATION;

        if(!robot.getNextMove()){
            robot.rotate();
            return new Response(Commands.SERVER_TURN_RIGHT.getValue());
        }
        return new Response(Commands.SERVER_MOVE.getValue());
    }

    private Response cornerNavigation(String input){
        Coordinates coordinates = new Coordinates(input);
        if(coordinates == null)
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue());

        if(coordinates.finishReached()){
            this.state = State.DESTINATION;
            return new Response(Commands.SERVER_PICK_UP.getValue());
        }

        robot.setCoordinates(coordinates);
        if(!robot.getNextMove()){
            robot.rotate();
            return new Response(Commands.SERVER_TURN_RIGHT.getValue());
        }
        return new Response(Commands.SERVER_MOVE.getValue());
    }

    private Response navigating(String input){
        Coordinates coordinates = new Coordinates(input);
        if(coordinates == null)
            return new Response(true, Commands.SERVER_LOGIC_ERROR.getValue());

        robot.setCoordinates(coordinates);
        this.state = State.DESTINATION;
        return new Response(Commands.SERVER_PICK_UP.getValue());
    }
    private Response retrieveMessage(String input){
        if (input.equals("")){
            this.state = State.NAVIGATION;
            return new Response(this.robot.getNextMoveDestination().getValue());
        }
        if(!checkLenght(input)){
            return new Response(true, Commands.SERVER_SYNTAX_ERROR.getValue());
        }
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
