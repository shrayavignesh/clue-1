import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
	
	final ClueCharacter clueCharacter;
    private List<Card> hand = new ArrayList<>();
    private Room currentRoom = null;
    private Room previousRoom = null;
    private boolean canPlay = true;

    public Player(ClueCharacter character) {
        clueCharacter = character;
    }
    
    /**
     * Adds card to hand
     * @param card to be added
     */
    public void addCard(Card card) {
    	hand.add(card);
    }
    
    // Getters
    public List<Card> getHand(){return Collections.unmodifiableList(hand);}
    public Room getCurrentRoom(){return currentRoom;}
    public Room getPreviousRoom(){return previousRoom;}
    public boolean canStillPlay(){return canPlay;}
    
    // Setters
    public void setCurrentRoom(Room r){currentRoom = r;}
    public void setPreviousRoom(Room r){previousRoom = r;}
    public void setPlayStatus(boolean b){canPlay = b;}
	
}
