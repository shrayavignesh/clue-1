public class Weapon extends Card {
    public Room room;

    public Weapon(String name) {
        super(name);
    }

    public void setRoom(Room r) {
        room = r;
    }

    @Override
    public String getDescription() {
        return "A " + getName();
    }
}
