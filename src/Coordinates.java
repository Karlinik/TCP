/**
 * Created by Nikola Karlikova on 23.03.2018.
 */
public class Coordinates {
    private int x;
    private int y;

    Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    Coordinates(String input){
        this.parse(input);
    }

    public int x(){return x;}
    public int y(){return y;}


    public boolean finishReached(){
        //TBD
        return true;
    }

    private Coordinates parse(String input){
        //TBD
        return new Coordinates(0,0);
    }
}
