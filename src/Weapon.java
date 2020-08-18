public class Weapon extends Card {
    public Room room;

    public Weapon(String name) {
        this.name = name;
    }

    public void setRoom(Room r) {
        room = r;
    }

    @Override
    String getDescription() {
        return "A " + name;
    }
}