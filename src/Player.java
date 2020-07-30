import java.util.ArrayList;

public class Player {
    final ClueCharacter clueCharacter;
    private ArrayList<Card> hand = new ArrayList<>();
    private Room currentRoom = null;
    private Room previousRoom = null;
    private boolean canPlay = true;

    public Player(ClueCharacter character) {
        clueCharacter = character;
    }
}
