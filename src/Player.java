import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Player
 */
public class Player {
    final ClueCharacter clueCharacter;
    final String name;
    final Integer playerNumber;
    private List<Card> hand = new ArrayList<>();
    private Room currentRoom = null;
    private Room previousRoom = null;
    private boolean canPlay = true;

    public Player(String name, ClueCharacter character, Integer number) {
        clueCharacter = character;
        this.name = name;
        playerNumber = number;
    }

    /**
     * Adds card to hand
     * @param card to be added
     */
    public void addCard(Card card) {
        hand.add(card);
    }

    // Getters
    public ClueCharacter getClueCharacter() { return clueCharacter; }
    public String getHand() {
        String cards = "Your cards:\n";
        for(Card c : hand) cards += "[" + c.name + "] ";
        return cards;
    }
    public Integer getPlayerNumber() {return playerNumber;}
    public String getName() {return name;}
    public Room getCurrentRoom() { return currentRoom; }
    public Room getPreviousRoom() { return previousRoom; }
    public boolean canStillPlay() { return canPlay; }

    // Setters
    public void setCurrentRoom(Room r) { currentRoom = r; }
    public void setPreviousRoom(Room r){ previousRoom = r; }
    public void setPlayStatus(boolean b) { canPlay = b; }

}
