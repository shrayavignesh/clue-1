import java.util.Random;

public class Dice {
    protected final static Random RANDOM = new Random();

    static int roll() {
        return RANDOM.nextInt(6) + 1;
    }


}
