public class ClueCharacter extends Card {
    protected Player player;
    final String name;

    public ClueCharacter(String name) {
        this.name = name;
    }

    public void addPlayer(Player p) {
        player = p;
    }
}
