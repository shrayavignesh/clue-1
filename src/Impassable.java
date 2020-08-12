/**
 * An Impassable Card for use on the map for the back of rooms and such
 */
public class Impassable extends Card {
    final boolean showPassable;

    public Impassable() {
        this.showPassable = false;
    }

    public Impassable(boolean showPassable) {
        this.showPassable = showPassable;
    }


    // TODO - Maybe find a better way to handle this
    @Override
    String getDescription() {
        return null;
    }

    @Override
    char getCharRep() {
        if (showPassable) {
            return 'E';
        }
        return 'X'; // ∅
    }
}
