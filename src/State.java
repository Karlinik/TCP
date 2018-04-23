/**
 * Created by gc2karl on 3/23/2018.
 */
public enum State {
    AUTENTIZATION(12),
    CLIENT_CONFIRMATION(7),
    FIRST_MOVE(12),
    SECOND_MOVE(12),
    CORNER_NAVIGATION(12),
    NAVIGATION(12),
    DESTINATION(100);

    private int lenght;

    State(int lenght){
        this.lenght = lenght;
    }

    public int getLenght(){ return this.lenght; }
}
