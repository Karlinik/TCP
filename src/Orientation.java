/**
 * Created by Nikola Karlikova on 23.03.2018.
 */
public enum Orientation {
    UP("^"),
    RIGHT(">"),
    DOWN("v"),
    LEFT("<");

    private String sign;

    Orientation(String sign){
        this.sign = sign;
    }

    public String getSign(){ return sign; }
    public Orientation next(){
        return this.next();
    }
}
