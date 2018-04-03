/**
 * Created by gc2karl on 3/23/2018.
 */
public class Robot {
    private boolean isCharging;
    private Orientation orientation;
    private Coordinates coordinates;
    private int movesCount;
    private final int cornerX = -2;
    private final int cornerY =  2;

    Robot(){
        this.movesCount = 0;
    }

    public boolean isCharging() { return isCharging; }

    public void setCharging(boolean option){ this.isCharging=option; }

    public void setCoordinates(Coordinates coordinates){
        this.coordinates = coordinates;
    }

    public boolean canTellOrientation(Coordinates coordinates){
        if(this.coordinates.x()==coordinates.x()){
            if(this.coordinates.y() < coordinates.y()){
                this.orientation = Orientation.UP;
                return true;
            }
            else if(this.coordinates.y() > coordinates.y()){
                this.orientation = Orientation.DOWN;
                return true;
            }
        } else if (this.coordinates.y()==coordinates.y()){
            if(this.coordinates.x() < coordinates.x()){
                this.orientation = Orientation.RIGHT;
                return true;
            }
            else if(this.coordinates.x() > coordinates.x()){
                this.orientation = Orientation.LEFT;
                return true;
            }
        }
        return false;
    }

    public Commands getNextMoveDestination(){
        if(this.coordinates.finishReached()){
            if(this.orientation.equals(Orientation.UP) || this.orientation.equals(Orientation.LEFT)){
                this.rotate();
                return Commands.SERVER_TURN_RIGHT;
            }
            else if(this.orientation.equals(Orientation.DOWN)){
                this.antiRotate();
                return Commands.SERVER_TURN_LEFT;
            }
            else return Commands.SERVER_MOVE;
        } else{
            if(this.coordinates.x() == -2) {
                this.antiRotate();
                return Commands.SERVER_TURN_LEFT;
            }
            else if(this.coordinates.x() == 2){
                this.rotate();
                return Commands.SERVER_TURN_RIGHT;
            }
            else
                return Commands.SERVER_MOVE;
        }
    }

    public boolean getNextMove(){
        if(this.coordinates.x() != this.cornerX){
            if(this.coordinates.x() > this.cornerX)
                return this.orientation.getSign().equals("<");
            else return this.orientation.getSign().equals(">");
        } else if(this.coordinates.y() > this.cornerY)
            return this.orientation.getSign().equals("v");
        else return this.orientation.getSign().equals("^");
    }

    public void rotate(){
        this.orientation = this.orientation.next();
    }

    public void antiRotate(){
        int i=0;
        while(i<3)
            this.orientation = this.orientation.next();
    }
}
