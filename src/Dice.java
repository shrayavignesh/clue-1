import java.util.Random;

public class Dice {
    private static final Random r = new Random();

    static int roll() {
        return r.nextInt(6);
    }
}
