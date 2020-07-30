import java.util.ArrayList;
import java.util.Queue;

public class Clue {
    public static void main(String[] a) {
        final ArrayList<Weapon> weapons;
        final ArrayList<Room> rooms;
        final ArrayList<ClueCharacter> characters;
        final Suggestion suggestion;
        ArrayList<Player> players;
        Queue<Player> playOrder;
        Card[][] board = new Card[24][25];

        /**
         * TODO - Main Clue event loop
         * 1. Create Circumstance to be used as solution <character, weapon, room>
         * 2. Ask how many players (and their names?)
         * 3. Share out the remaining Cards
         * Loop through:
         *  1. Roll 2 dice and loop until all moves are over or they enter a room.
         *  // TODO - Have to make sure player doesn't move into an occupied room or space or impassable space.
         *  2. If player enters room, can break out of move loop
         *  3. Player can make an suggestion.
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
