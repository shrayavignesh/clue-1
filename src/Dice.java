import java.util.Random;

public class Dice {
    private static Random r = new Random();

    static int roll() {
        return r.nextInt(6);
    }
}
