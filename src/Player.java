import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Player
 */
public class Player {
    protected final String name;
    private final ClueCharacter clueCharacter;
    private final Integer playerNumber;
    protected List<Card> hand = new ArrayList<>();
    protected Card refuteCard;
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
     *
     * @param card to be added
     */
    public void addCard(Card card) {
        hand.add(card);
    }

    // Getters
    public ClueCharacter getClueCharacter() {
        return clueCharacter;
    }

    public List<Card> getHand() {
        return hand;
    }

    public Integer getPlayerNumber() {
        return playerNumber;
    }

    public String getName() {
        return name;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    // Setters
    public void setCurrentRoom(Room r) {
        currentRoom = r;
    }

    public Room getPreviousRoom() {
        return previousRoom;
    }

    public void setPreviousRoom(Room r) {
        previousRoom = r;
    }

    public boolean canStillPlay() {
        return canPlay;
    }

    public void setPlayStatus(boolean b) {
        canPlay = b;
    }

    public Card getRefuteCard() {
        return this.refuteCard;
    }

    public void setRefuteCard(Card card) {
        this.refuteCard = card;
    }
}
