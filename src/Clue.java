import java.util.*;

public class Clue {
    static Card[][] board = new Card[24][25];

    private static final int MIN_PLAYERS = 2; //2 Players
    private static final int MAX_PLAYERS = 6; //6 Players

    /**
     * Cards
     */
    private static final ArrayList<Weapon> weapons = new ArrayList<>();         //Contains all weapons
    private static final ArrayList<Room> rooms = new ArrayList<>();             //Contains all rooms
    private static final ArrayList<ClueCharacter> characters = new ArrayList<>();   //Temporary collection
    private static ArrayList<ClueCharacter> allCharacters = new ArrayList<>();  //Contains all characters

    /**
     * PlayerInfo
     */
    private static final ArrayList<Player> players = new ArrayList<>();
    private static Queue<ClueCharacter> characterOrder = new ArrayDeque<>();
    private static Queue<Player> playOrder = new ArrayDeque<>();

    /**
     * Locations
     */
    private static final Map<Room, Pair<Integer, Integer>> roomLocations = new HashMap<>();
    private static final Map<ClueCharacter, Pair<Integer, Integer>> charLocations = new HashMap<>();
    private static final ArrayList<Pair<Integer, Integer>> entranceLocations = new ArrayList<>();

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

        // Create temporary collections for Weapon, Room and Character cards
        allCharacters = new ArrayList<>(characters);
        ArrayList<Weapon> weaponCards = new ArrayList<>(weapons);
        ArrayList<Room> roomCards = new ArrayList<>(rooms);

        // 1. Create Circumstance to be used as solution <character, weapon, room>
        Collections.shuffle(characters);
        Collections.shuffle(weaponCards);
        Collections.shuffle(roomCards);

        // Deal out weapons to rooms
        for (int i = 0; i < weaponCards.size(); i++) {
            roomCards.get(i).addWeapon(weaponCards.get(i));
        }

        // 2. Ask how many players (and their names?)
        getPlayerInfo();

        Suggestion mainAccusation = new Suggestion(
                weaponCards.remove(0),
                characters.remove(0),
                roomCards.remove(0)
        );

        //3. Orders the players' playOrder
        setPlayOrder();

        // 4. Share out the remaining Cards ()
        ArrayList<Card> deck = new ArrayList<>();
        deck.addAll(weaponCards);
        deck.addAll(characters);
        deck.addAll(roomCards);
        Collections.shuffle(deck);
        distributeCards(deck);
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
                Integer number = null;

                while (playerName.equals("")) {
                    System.out.print("Enter name for Player " + (i + 1) + ": ");
                    playerName = s.nextLine();
                }

                System.out.print("Which character do you want to play? Enter the number:\n");
                for(int j = 0; j < characters.size(); j++){
                    if(characters.get(j).getPlayer() == null){
                        System.out.print("(" + (j+1) + ") : " + characters.get(j).name + " ");
                    }
                    if(j == characters.size()-1) System.out.print("\nNumber: ");
                }
                number = s.nextInt() - 1;
                while(characters.get(number).getPlayer() != null){
                    System.out.print("Character not available, pick another number: ");
                    number = s.nextInt() - 1;
                }

                players.add(new Player(playerName, characters.get(number), (i+1)));
                characters.get(number).addPlayer(players.get(players.size()-1));
                System.out.printf("Player %d (%s) is %s\n\n", (i + 1), playerName, characters.get(number).name);
            }
        }
    }

    /**
     * Queue ordering of players through sorting out the playing and unused characters
     */
    public static void setPlayOrder(){
        //Collections that separates playing and unused characters
        Queue<ClueCharacter> playingCharacters = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();

        while(!characterOrder.isEmpty()){
            if(characterOrder.peek().getPlayer() == null) characterOrder.poll();
            else {
                ClueCharacter c = characterOrder.poll();
                playingCharacters.offer(c);
                order.add(c.getOrder());
            }
        }

        //Clear Screen 20 times
        for(int i = 0; i<20;i++) System.out.println(new String(new char[50]).replace("\0", "\r\n"));

        System.out.print("Characters playing are :\n");
        for(Player p : players){
            System.out.printf("\tPlayer %d (%s) %s\n", p.playerNumber, p.name, p.clueCharacter.name);
        }

        System.out.print("First player to start is..\nRoll dice..\n");
        int start = order.get(new Random().nextInt(order.size()));  //Random number
        Player player = allCharacters.get(start).player;
        System.out.printf("~~Player %d (%s): %s~~\n", player.playerNumber, player.name, player.clueCharacter.name);

        //Sorts out the character order
        while(playingCharacters.peek().getOrder() != start){
            ClueCharacter c = playingCharacters.poll();
            playingCharacters.offer(c);
        }

        //Sorts out the playing order
        while(!playingCharacters.isEmpty()) playOrder.offer(playingCharacters.poll().player);
    }

    /**
     * Adds all remaining cards into one deck, shuffles and distributes to each player
     */
    public static void distributeCards(List<Card> cards){
        System.out.print("Shuffling cards..\nDistributing cards to players\n");
        int count = 0;
        for(int i = 0; i< cards.size(); i++ ){
            players.get(count).addCard(cards.get(i));
            count++;
            if(count >= players.size()) count = 0;
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
        characters.add(new ClueCharacter("Miss Scarlett", 0));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(24, 8));

        characters.add(new ClueCharacter("Col Mustard", 1));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(17, 1));

        characters.add(new ClueCharacter("Mrs White", 2));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(0, 10));

        characters.add(new ClueCharacter("Mr Green", 3));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(0, 15));

        characters.add(new ClueCharacter("Mrs Peacock", 4));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(6, 24));

        characters.add(new ClueCharacter("Prof Plum", 5));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(19, 24));
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
     * Sets the entrance as active on the board, creates a room as well
     */
    public static void setEntrances() {
        for (Pair<Integer, Integer> location : entranceLocations) {
            int row = location.getOne();
            int col = location.getTwo();
            Room room = (Room) board[row][col];
        }
    }
}
