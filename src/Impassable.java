public class Impassable extends Card {
    final boolean showPassable;

    public Impassable() { this.showPassable = false; }
    public Impassable(boolean showPassable) {
        this.showPassable = showPassable;
    }
}
