import static java.lang.Math.abs;

/**
 * Created by gc2karl on 3/23/2018.
 */
public class Robot {
    private boolean isCharging;
    private Orientation orientation;
    private Coordinates coordinates;
    private int movesCount;
    private final Integer cornerX = -2;
    private final Integer cornerY =  2;

    Robot(){
        this.movesCount = 0;
    }

    public Coordinates getCoordinates(){return this.coordinates;}

    public boolean isCharging() { return isCharging; }

    public void setCharging(boolean option){ this.isCharging=option; }

    public void setCoordinates(Coordinates coordinates){
        this.coordinates = coordinates;
    }

    public void tellOrientation(Coordinates coordinates){
        if(this.coordinates.x()==coordinates.x()){
            if(this.coordinates.y() < coordinates.y())
                this.orientation = Orientation.UP;
            else if(this.coordinates.y() > coordinates.y())
                this.orientation = Orientation.DOWN;

        } else if (this.coordinates.y()==coordinates.y()){
            if(this.coordinates.x() < coordinates.x())
                this.orientation = Orientation.RIGHT;
            else if(this.coordinates.x() > coordinates.x())
                this.orientation = Orientation.LEFT;
        }
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
            if(this.coordinates.x() == cornerX) {
                if(this.orientation.equals(Orientation.LEFT) || (this.orientation.equals(Orientation.DOWN) && abs(coordinates.y)%2 == 0)){
                    this.antiRotate();
                    return Commands.SERVER_TURN_LEFT;
                } else
                    return Commands.SERVER_MOVE;
            }
            else if(this.coordinates.x() == 2){
                if(this.orientation.equals(Orientation.RIGHT) || (this.orientation.equals(Orientation.DOWN) && abs(coordinates.y)%2 == 1)){
                    this.rotate();
                    return Commands.SERVER_TURN_RIGHT;
                } else
                    return Commands.SERVER_MOVE;
            }
            else
                return Commands.SERVER_MOVE;
        }
    }

    public Commands getNextMove(){
        if(this.coordinates.x() != this.cornerX){
            //need to reach same x coordinate
            if(this.coordinates.x() > this.cornerX) {
                if (this.orientation.getSign().equals("<"))
                    return Commands.SERVER_MOVE;
                else if(this.orientation.getSign().equals("^")){
                    this.antiRotate();
                    return Commands.SERVER_TURN_LEFT;
                }
                else{
                    this.rotate();
                    return Commands.SERVER_TURN_RIGHT;
                }
            }
            else {
                if(this.orientation.getSign().equals(">"))
                    return Commands.SERVER_MOVE;
                else if(this.orientation.getSign().equals("v")){
                    this.antiRotate();
                    return Commands.SERVER_TURN_LEFT;
                }
                else{
                    this.rotate();
                    return Commands.SERVER_TURN_RIGHT;
                }
            }
        } else {
            //same x coordinate, needs to reach same y coordinate
            if(this.coordinates.y() > this.cornerY){
                if(this.orientation.getSign().equals("v"))
                    return Commands.SERVER_MOVE;
                else if(this.orientation.getSign().equals("<")){
                    this.antiRotate();
                    return Commands.SERVER_TURN_LEFT;
                }
                else{
                    this.rotate();
                    return Commands.SERVER_TURN_RIGHT;
                }
            } else{
                if(this.orientation.getSign().equals("^"))
                    return Commands.SERVER_MOVE;
                else if(this.orientation.getSign().equals(">")){
                    this.antiRotate();
                    return Commands.SERVER_TURN_LEFT;
                }
                else{
                    this.rotate();
                    return Commands.SERVER_TURN_RIGHT;
                }
            }
        }
    }

    public void rotate(){
        this.orientation = this.orientation.next();
    }

    public void antiRotate(){ this.orientation = this.orientation.previous(); }
}
