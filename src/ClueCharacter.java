public class ClueCharacter extends Card {
    protected Player player;
    private Integer order;
    private Pair<Integer, Integer> location;

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
    
    public void setLocation(Pair<Integer, Integer> location) {
    	this.location = location;
    }
    
    public Pair<Integer, Integer> getLocation(){
    	return location;
    }

    @Override
    String getDescription() {
        return "Hi. I am " + name + ".";
    }

    public boolean playerNotNull() {
        return player == null;
    }
}
