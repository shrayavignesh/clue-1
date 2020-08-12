import java.util.Random;

public class Dice {
    protected final static Random _rgen = new Random();

    static int roll() {
        return _rgen.nextInt(6) + 1;
    }


}
