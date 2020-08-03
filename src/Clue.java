
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import javafx.util.Pair;

public class Clue {
	
    private static ArrayList<Weapon> weapons;
    private ArrayList<Room> rooms;
    private static ArrayList<ClueCharacter> characters;
    private Suggestion suggestion;
    private ArrayList<Player> players;
    private Queue<Player> playOrder;
    static Card[][] board = new Card[24][25];
    private static Map<String, Pair> roomLocations = new HashMap<String, Pair>();
    private static Map<String, Pair> charLocations = new HashMap<String, Pair>();
    private static ArrayList<Pair<Integer, Integer>> entranceLocations = new ArrayList<>();
	
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
	
	/**
	 * Sets up the game
	 */
	 public static void setupGame(int playerCount){
	 }
	 
	 /**
	  * Loads room name and dimensions into map
	  */
	 public static void loadRooms() {
		 roomLocations.put("Kitchen", new Pair<Integer, Integer>(6, 6));
		 roomLocations.put("Ball Room", new Pair<Integer, Integer>(8, 6));
		 roomLocations.put("Conservatory", new Pair<Integer, Integer>(6, 4));
		 roomLocations.put("Billard Room", new Pair<Integer, Integer>(6, 5));
		 roomLocations.put("Library", new Pair<Integer, Integer>(5, 5));
		 roomLocations.put("Study", new Pair<Integer, Integer>(7, 3));
		 roomLocations.put("Hall", new Pair<Integer, Integer>(6, 7));
		 roomLocations.put("Lounge", new Pair<Integer, Integer>(7, 5));
		 roomLocations.put("Dining Room", new Pair<Integer, Integer>(8, 6));
		 roomLocations.put("Middle Room", new Pair<Integer, Integer>(5, 7));
	 }
	 
	 /**
	  * Loads Clue Character names and their starting positions into map
	  */
	 public static void loadCharacters() {
		 charLocations.put("Mrs White", new Pair<Integer, Integer>(0, 10));
		 charLocations.put("Mr Green", new Pair<Integer, Integer>(0, 15));
		 charLocations.put("Mrs Peacock", new Pair<Integer, Integer>(6, 24));
		 charLocations.put("Prof Plum", new Pair<Integer, Integer>(19, 24));
		 charLocations.put("Miss Scarlett", new Pair<Integer, Integer>(24, 8));
		 charLocations.put("Col Mustard", new Pair<Integer, Integer>(17, 1));
		 
	 }
	 
	 /**
	  * Loads weapons into list
	  */
	 public static void loadWeapons() {
		 weapons.add(new Weapon("Candlestick"));
		 weapons.add(new Weapon("Dagger"));
		 weapons.add(new Weapon("Lead Pipe"));
		 weapons.add(new Weapon("Revolver"));
		 weapons.add(new Weapon("Rope"));
		 weapons.add(new Weapon("Spanner"));
	 }
	 
	 /**
	  * Places room on the board
	  * @param roomName, Name of room
	  * @param dimension, dimension of room
	  */
	 public static void placeRooms(String roomName, Pair<Integer, Integer> dimension) {
		 int rows = dimension.getKey();
		 int cols = dimension.getValue();
		 if (roomName.equals("Middle Room")){  // Middle room can't be accessed
			 for (int row = 0; row < rows; row++) {
				 for (int col = 0; col < cols; col++) {
					 board[row][col] = new Impassable(true);
				 }
			 }
		 }
		 for (int row = 0; row < rows; row++) {
			 for (int col = 0; col < cols; col++) {
				 board[row][col] = new Room(roomName);
			 }
		 }
	 }
	 
	 /**
	  * Places character on board
	  * @param charName, name of character
	  * @param location, starting location of character
	  */
	 public static void placeCharacters(String charName, Pair<Integer, Integer> location) {
		 int row = location.getKey();
		 int col = location.getValue();
		 ClueCharacter character = new ClueCharacter(charName);
		 board[row][col] = character;
		 characters.add(character);
	 }
	 
	 /**
	  * Loads the entrances to each room on the board
	  */
	 public static void loadEntrances() {
		 entranceLocations.add(new Pair<Integer, Integer>(6, 5));
		 entranceLocations.add(new Pair<Integer, Integer>(12, 8));
		 entranceLocations.add(new Pair<Integer, Integer>(15, 7));
		 entranceLocations.add(new Pair<Integer, Integer>(19, 7));
		 entranceLocations.add(new Pair<Integer, Integer>(18, 12));
		 entranceLocations.add(new Pair<Integer, Integer>(18, 13));
		 entranceLocations.add(new Pair<Integer, Integer>(20, 15));
		 entranceLocations.add(new Pair<Integer, Integer>(21, 18));
		 entranceLocations.add(new Pair<Integer, Integer>(16, 19));
		 entranceLocations.add(new Pair<Integer, Integer>(14, 21));
		 entranceLocations.add(new Pair<Integer, Integer>(9, 19));
		 entranceLocations.add(new Pair<Integer, Integer>(12, 23));
		 entranceLocations.add(new Pair<Integer, Integer>(4, 19));
		 entranceLocations.add(new Pair<Integer, Integer>(5, 16));
		 entranceLocations.add(new Pair<Integer, Integer>(7, 15));
		 entranceLocations.add(new Pair<Integer, Integer>(7, 10));
		 entranceLocations.add(new Pair<Integer, Integer>(5, 9));
	 }
	 
	 /**
	  * Sets the entrance as active on the board
	  */
	 public static void setEntrances() {
		 for (Pair<Integer, Integer> location : entranceLocations) {
			 int row = location.getKey();
			 int col = location.getValue();
			 Room room = (Room) board[row][col];
			 room.setEntrance();
		 }
		 
	 }
}
