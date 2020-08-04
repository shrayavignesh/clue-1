import java.util.*;

public class Clue {
    static Card[][] board = new Card[24][25];

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 5;

    /**
     * Cards
     */
    private static final ArrayList<Weapon> weapons = new ArrayList<>();
    private static final ArrayList<Room> rooms = new ArrayList<>();
    private static final ArrayList<ClueCharacter> characters = new ArrayList<>();

    /**
     * PlayerInfo
     */
    private static final ArrayList<Player> players = new ArrayList<>();
    private static Queue<Player> playOrder;

    /**
     * Locations
     */
    private static final Map<Room, Pair<Integer, Integer>> roomLocations = new HashMap<>();
    private static final Map<ClueCharacter, Pair<Integer, Integer>> charLocations = new HashMap<>();
    private static final ArrayList<Pair<Integer, Integer>> entranceLocations = new ArrayList<>();


    Suggestion gameSolution; // Final solution
    private static final Random randomize = new Random(); // For shuffling purposes
    private static final Scanner INPUT = new Scanner(System.in); // Input stream


    /**
     * TODO - Main Clue event loop
     * 1. Create Circumstance to be used as solution <character, weapon, room> ()
     * 2. Ask how many players (and their names?) ()
     * 3. Share out the remaining Cards ()
     *
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
    public static void main(String[] a) {
        // 0. Load up Arrays
        loadCharacters();
        loadWeapons();
        loadRooms();
        loadEntrances();

        // 1. Create Circumstance to be used as solution <character, weapon, room>
        Collections.shuffle(characters);
        Collections.shuffle(weapons);
        Collections.shuffle(rooms);

        // Deal out weapons to rooms
        for (int i = 0; i < weapons.size(); i++) {
            rooms.get(i).addWeapon(weapons.get(i));
        }

        // 2. Ask how many players (and their names?)
        getPlayerInfo();

        Suggestion mainAccusation = new Suggestion(
                weapons.remove(0),
                characters.remove(0),
                rooms.remove(0),
                null);

        // 3. Share out the remaining Cards ()
        ArrayList<Card> deck = new ArrayList<>();
        deck.addAll(weapons);
        deck.addAll(characters);
        deck.addAll(rooms);
    }

    /**
     * Uses user input to get how many players and player names
     **/
    public static void getPlayerInfo() {
        try (Scanner s = new Scanner(System.in)) {
            // Find out how many players
            int numPlayers = -1;
            while (numPlayers < MIN_PLAYERS || numPlayers > MAX_PLAYERS) {
                if (numPlayers != -1) {
                    System.out.printf("Game only supports %d-%d players.\n\n", MIN_PLAYERS, MAX_PLAYERS);
                }

                System.out.printf("How many players? (%d-%d): ", MIN_PLAYERS, MAX_PLAYERS);
                numPlayers = s.nextInt();
            }

            // Get player names and assign them a character
            for (int i = 0; i < numPlayers; i++) {
                String playerName = "";

                while (playerName.equals("")) {
                    System.out.print("Enter name for Player " + (i + 1) + ": ");
                    playerName = s.nextLine();
                }


                players.add(new Player(playerName, characters.get(i)));
                System.out.printf("Player %d (%s) is: %s\n", (i + 1), playerName, characters.get(i).name);
            }
        }
    }

    /**
     * Loads room name and dimensions into map
     */
    public static void loadRooms() {
        rooms.add(new Room("Kitchen"));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(6, 6));

        rooms.add(new Room("Ball Room"));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(8, 6));

        rooms.add(new Room("Conservatory"));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(6, 4));

        rooms.add(new Room("Billard Room"));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(6, 5));

        rooms.add(new Room("Library"));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(5, 5));

        rooms.add(new Room("Study"));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(7, 3));

        rooms.add(new Room("Hall"));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(6, 7));

        rooms.add(new Room("Lounge"));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(7, 5));

        rooms.add(new Room("Dining Room"));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(8, 6));

        rooms.add(new Room("Middle Room"));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(5, 7));
    }

    /**
     * Loads Clue Character names and their starting positions into map
     */
    public static void loadCharacters() {
        characters.add(new ClueCharacter("Mrs White"));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(0, 10));

        characters.add(new ClueCharacter("Mr Green"));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(0, 15));

        characters.add(new ClueCharacter("Mrs Peacock"));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(6, 24));

        characters.add(new ClueCharacter("Prof Plum"));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(19, 24));

        characters.add(new ClueCharacter("Miss Scarlett"));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(24, 8));

        characters.add(new ClueCharacter("Col Mustard"));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(17, 1));
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
     *
     * @param roomName,  Name of room
     * @param dimension, dimension of room
     */
    public static void placeRooms(String roomName, Pair<Integer, Integer> dimension) {
        int rows = dimension.getOne();
        int cols = dimension.getTwo();
        if (roomName.equals("Middle Room")) {  // Middle room can't be accessed
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
     *
     * @param charName, name of character
     * @param location, starting location of character
     */
    public static void placeCharacters(String charName, Pair<Integer, Integer> location) {
        int row = location.getOne();
        int col = location.getTwo();
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
            int row = location.getOne();
            int col = location.getTwo();
            Room room = (Room) board[row][col];
        }

    }

    /**
     * Validity check upon users input
     * If input is invalid, keeps asking for a valid input
     *
     * @param i, user input of number of players
     */
    static int validInputCheck(int i) {
        while(true){
            if(i >= MIN_PLAYERS && i <= MAX_PLAYERS){
                return i;
            }
            else {
                System.out.println("Number of players must be between 2 and 6...");
                return validInputCheck( Integer.parseInt(INPUT.nextLine()) );
            }
        }

    }

    /**
     * Creates game solution by randomly selecting a weapon, room and murderer
     */
    public void makeSolution() {
        // randomly choosing a murder weapon
        Weapon w = weapons.remove(randomize.nextInt(weapons.size()));

        // randomly choosing a murder room
        Room r = rooms.remove(randomize.nextInt(rooms.size()));

        // randomly choosing a murderer
        ClueCharacter c = characters.remove(randomize.nextInt(characters.size()));

        gameSolution = new Suggestion(w, c, r, null);
    }


    /**
     * Deals remaining cards (not including solution cards) to players hands
     * NOTE: this method must be called after makeSolution()
     */
    public void dealCards() {
        ArrayList <Card> toDeal = new ArrayList<>();

        //add all cards but solution cards to new deck
        for(Weapon w : weapons) toDeal.add(w);
        for(ClueCharacter c : characters) toDeal.add(c);
        for(Room r : rooms) toDeal.add(r);

        //shuffle said deck
        Collections.shuffle(toDeal);

        //deal between players
        while(!toDeal.isEmpty()) {
            for (Player p : players) {
                p.addCard(toDeal.get(toDeal.size()));
                toDeal.remove(toDeal.size());
            }
        }

    }
}