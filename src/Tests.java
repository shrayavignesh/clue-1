import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class Tests {
    @Test
    void testDice() {
        final int times = 1000;

        for (int i = 0; i < times; i++) {
            int roll = Dice.roll();

            Assertions.assertTrue(roll >= 1 && roll <= 6);
        }
    }
}
