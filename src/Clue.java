import java.util.ArrayList;
import java.util.Queue;

public class Clue {
    private String[] weapon_Names = {"Candlestick", "Dagger", "Lead Pipe", "Revolver", "Rope", "Spanner"};
    private String[] room_Names = {"Lounge", "Dining Room", "Kitchen", "Ballroom", "Conservatory",
            "Billiard Room", "Library", "Study Room", "Hall"};
    private String[] character_Names = {"Miss Scarlett", "Colonel Mustard", "Mrs White", "Mr Green",
            "Mrs Peacock", "Professor Plum"};

    private ArrayList<Weapon> weapons;
    private ArrayList<Room> rooms;
    private ArrayList<ClueCharacter> characters;
    private Suggestion suggestion;    //Cluedo.Suggestion changes every player so not final
    ArrayList<Player> players;
    Queue<Player> playOrder;
    Card[][] board = new Card[24][25];

    public Clue() {
        loadGame();
    }

    public void loadGame() {
        for (String weapon : weapon_Names) weapons.add(new Weapon(weapon));
        for (String room : room_Names) rooms.add(new Room(room));
        for (String character : character_Names) characters.add(new ClueCharacter(character));
    }

    public static void main(String[] a) {


        /**
         * TODO - Main Clue event loop
         * 1. Create Circumstance to be used as solution <character, weapon, room>
         * 2. Ask how many players (and their names?)
         * 3. Share out the remaining Cards
         * Loop through:
         *  1. Roll 2 dice and loop until all moves are over or they enter a room.
         *  // TODO - Have to make sure player doesn't move into an occupied room or space or impassable space.
         *  2. If player enters room, can break out of move loop
         *  3. Cluedo.Player can make an suggestion.
         *  4. Loop again through all players:
         *          1. Show the suggestion and the players cards.
         *          2. Allow player to decide if they want to refute or not
         *  5. Give player opportunity to accuse or not.
         *
         *
         *  That loop continues until all the players are gone or someone guesses correctly
         */
    }
}
