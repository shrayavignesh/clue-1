public class ClueCharacter extends Card {
    protected Player player;

    public ClueCharacter(String name) {
        this.name = name;
    }

    public void addPlayer(Player p) {
        player = p;
    }
}
