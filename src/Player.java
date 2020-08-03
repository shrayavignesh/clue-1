import java.util.ArrayList;
import java.util.Random;

public class Player {
    final ClueCharacter clueCharacter;
    private ArrayList<Card> hand = new ArrayList<>();
    public Integer moving = null;
    private Room currentRoom = null; //This is changed during their turn
    private Room previousRoom = null; //This is changed after their turn ends
    private boolean canPlay = true;

    public Player(ClueCharacter character) {
        clueCharacter = character;
    }

    public void move() {
        //If a free cell, move the person from 1 position to another in array
        //
    }
  
    public void Suggestion() {

    }

    public void Accusation() {
      
    }
}
