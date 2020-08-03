public class Room extends Card {
    ClueCharacter character = null;
    ClueCharacter other = null;
    Weapon weapon;

    public Room(String name) {
        this.name = name;
    }

    @Override
    String getDescription() {
        return name;
    }
}
