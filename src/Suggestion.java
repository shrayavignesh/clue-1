import java.util.ArrayList;
import java.util.List;

public class Suggestion {
    // Current Player
    protected final Weapon weapon;
    protected final ClueCharacter character;
    protected final Room room;
    private final List<Card> presentCards = new ArrayList<>();
    //Neighbouring Player
    private Player playerWithCard;

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
     * @author Laurence Malata
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
     * Returns the present suggested cards of the neighbouring player
     *
     * @author Laurence Malata
     */
    public Player getPlayerWithCard() {
        return playerWithCard;
    }

    public void printCards() {
        StringBuilder cards = new StringBuilder("Pick a card to refute: Type the number: \n");
        int count = 0;

        for (Card c : presentCards) {
            cards.append(count++).append(" [").append(c.getName()).append("]   ");
        }

        System.out.println(cards);
    }

    public List<Card> getPresentCards() {
        return presentCards;
    }

    public ClueCharacter getCharacter() {
        return character;
    }

    public Room getRoom() {
        return room;
    }

    public Weapon getWeapon() {
        return weapon;
    }
}
