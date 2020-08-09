import java.util.ArrayList;
import java.util.List;

public class Suggestion {
    // Current Player
    private final Weapon weapon;
    private final ClueCharacter character;
    private final Room room;

    //Neighbouring Player
    private Player playerWithCard;
    private List<Card> presentCards;

    public Suggestion(Weapon weapon, ClueCharacter character, Room room) {
        this.weapon = weapon;
        this.character = character;
        this.room = room;

    }

    /**
      * A boolean method that check if the neighbouring player has the suggested cards. If true, iteration
      * going to the next players stop.
      * During this method, if the player has a suggested card, the specific card will be added to the presentCards
      * ArrayList.. this is to assume that the player has either 1 to 3 suggested cards in their hand
      * 
      * @Author:Laurence_Malata
     */
    public boolean checkHand(List<Card> hand, Player nextPlayer) {
        boolean present = false;
        for (Card card : hand) {
            if (card.equals(character) || card.equals(room) || card.equals(weapon)) {
                present = true;
                playerWithCard = nextPlayer;
                presentCards.add(card);
            }
        }
        return present;
    }

    /**
      * Returns a neighbouring player with a suggested card
      * 
      * @Author:Laurence_Malata
     */
    public Player getPlayerWithCard() {
        return playerWithCard;
    }

        Returns the present suggested cards of the neighbouring player
       @Author:Laurence_Malata
     */
    public String printCards(){
        String cards = "Pick a card to refute: Type the number: \n";
        int count = 0;
        for(Card c : presentCards){ cards += count++ + " [" + c.name + "]   "; }
        return cards;
    }

    public List<Card> getPresentCards() { return presentCards; }
    public ClueCharacter getCharacter() {return character;}
    public Room getRoom() {return room;}
    public Weapon getWeapon() {return weapon;}
}
