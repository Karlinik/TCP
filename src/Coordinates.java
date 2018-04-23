/**
 * Created by Nikola Karlikova on 23.03.2018.
 */
public class Coordinates {
    public Integer x;
    public Integer y;

    Coordinates(Integer x, Integer y){
        this.x = x;
        this.y = y;
    }

    public Integer x(){return x;}
    public Integer y(){return y;}


    public boolean finishReached(){
        if(x == -2 && y == 2)
            return true;
        return false;
    }

    public boolean outOfDestination(){
        if(x == 2 && y == -3)
            return true;
        return false;
    }

    public boolean same(Coordinates coordinates){
        if(this.x == coordinates.x() && this.y == coordinates.y())
            return true;
        return false;
    }
}
