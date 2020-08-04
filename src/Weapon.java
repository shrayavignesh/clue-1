=======
public class Weapon extends Card {
    public Weapon(String name) {
        this.name = name;
    }

    @Override
    String getDescription() {
        return "A " + name;
    }
}