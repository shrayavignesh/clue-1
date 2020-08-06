public class ClueCharacter extends Card {
    protected Player player;
    private Integer order;

    public ClueCharacter(String name, Integer number) {
        this.name = name;
        order = number;
    }

    public ClueCharacter(String name) {
        this.name = name;
    }

    public Integer getOrder() { return order; }

    public void addPlayer(Player p) {
        player = p;
    }

    public Player getPlayer() { return player;}

    @Override
    String getDescription() {
        return "Hi. I am " + name + ".";
    }
}
