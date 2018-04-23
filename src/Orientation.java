/**
 * Created by Nikola Karlikova on 23.03.2018.
 */
public enum Orientation {
    UP("^"){
        @Override
        public Orientation next(){
            return Orientation.RIGHT;
        }

        @Override
        public Orientation previous(){
            return Orientation.LEFT;
        }
    },
    RIGHT(">"){
        @Override
        public Orientation next(){
            return Orientation.DOWN;
        }

        @Override
        public Orientation previous(){
            return Orientation.UP;
        }
    },
    DOWN("v"){
        @Override
        public Orientation next(){
            return Orientation.LEFT;
        }

        @Override
        public Orientation previous(){
            return Orientation.RIGHT;
        }
    },
    LEFT("<");

    public Orientation next(){
        return Orientation.UP;
    }

    public Orientation previous(){
        return Orientation.DOWN;
    }

    private String sign;

    Orientation(String sign){
        this.sign = sign;
    }

    public String getSign(){ return sign; }
}
