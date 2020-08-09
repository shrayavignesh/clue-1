import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class Clue implements KeyListener{
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;
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
    /**
     * Locations
     */
    private static final Map<Room, Pair<Integer, Integer>> roomLocations = new HashMap<>();
    private static final Map<ClueCharacter, Pair<Integer, Integer>> charLocations = new HashMap<>();
    private static final ArrayList<Pair<Integer, Integer>> entranceLocations = new ArrayList<>();
    private static final Random randomize = new Random(); // For shuffling purposes
    private static final Scanner INPUT = new Scanner(System.in); // Input stream
    private static Card[][] board = new Card[24][25];
    private static ArrayList<ClueCharacter> allCharacters = new ArrayList<>();
    private static Queue<ClueCharacter> characterOrder = new ArrayDeque<>();
    private static Queue<Player> playOrder = new ArrayDeque<>();
    public static Player currentTurn;
    public Suggestion gameSolution; // Final solution

    /**
     * TODO - Main Clue event loop
     * 1. Create Circumstance to be used as solution <character, weapon, room> ()
     * 2. Ask how many players (and their names?) ()
     * 3. Share out the remaining Cards ()
     * <p>
     * Loop through:
     * 1. Roll 2 dice and loop until all moves are over or they enter a room.
     * // TODO - Have to make sure player doesn't move into an occupied room or space or impassable space.
     * 2. If player enters room, can break out of move loop
     * 3. Player can make an suggestion.
     * 4. Loop again through all players:
     * 1. Show the suggestion and the players cards.
     * 2. Allow player to decide if they want to refute or not
     * 5. Give player opportunity to accuse or not.
     * <p>
     * <p>
     * That loop continues until all the players are gone or someone guesses correctly
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

        placeCards();

        INPUT.close();
    }

    /**
     * Uses user input to get how many players and player names
     **/
    public static void getPlayerInfo() {
        // Find out how many players
        int numPlayers = -1;
        while (numPlayers < MIN_PLAYERS || numPlayers > MAX_PLAYERS) {
            if (numPlayers != -1) {
                System.out.printf("Game only supports %d-%d players.\n\n", MIN_PLAYERS, MAX_PLAYERS);
            }

            System.out.printf("How many players? (%d-%d): ", MIN_PLAYERS, MAX_PLAYERS);
            numPlayers = INPUT.nextInt();
        }

        // Get player names and assign them a character
        for (int i = 0; i < numPlayers; i++) {
            String playerName = "";
            Integer number = null;

            while (playerName.equals("")) {
                System.out.print("Enter name for Player " + (i + 1) + ": ");
                playerName = INPUT.nextLine();
            }
            System.out.print("Which character do you want to play? Enter the number:\n");
            for (int j = 0; j < characters.size(); j++) {
                if (characters.get(j).getPlayer() == null) {
                    System.out.print("(" + (j + 1) + ") : " + characters.get(j).name + " ");
                }
                if (j == characters.size() - 1) System.out.print("\nNumber: ");
            }
            number = INPUT.nextInt() - 1;
            while (characters.get(number).getPlayer() != null) {
                System.out.print("Character not available, pick another number: ");
                number = INPUT.nextInt() - 1;
            }

            players.add(new Player(playerName, characters.get(number), (i + 1)));
            characters.get(number).addPlayer(players.get(players.size() - 1));
            System.out.printf("Player %d (%s) is %s\n\n", (i + 1), playerName, characters.get(number).name);
        }
        placeCards();
        printBoard();
    }

    /**
     * Queue ordering of players through sorting out the playing and unused characters
     */
    public static void setPlayOrder() {
        //Collections that separates playing and unused characters
        Queue<ClueCharacter> playingCharacters = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();

        while (!characterOrder.isEmpty()) {
            if (characterOrder.peek().getPlayer() == null) characterOrder.poll();
            else {
                ClueCharacter c = characterOrder.poll();
                playingCharacters.offer(c);
                order.add(c.getOrder());
            }
        }

        clearScreen();

        System.out.print("Characters playing are :\n");
        for (Player p : players) {
            System.out.printf("\tPlayer %d (%s) %s\n", p.getPlayerNumber(), p.getName(), p.getClueCharacter().name);
        }

        System.out.print("First player to start is..\nRoll dice..\n");
        int start = order.get(new Random().nextInt(order.size()));  //Random number
        Player player = allCharacters.get(start).player;
        System.out.printf("~~Player %d (%s): %s~~\n", player.getPlayerNumber(), player.getName(), player.getClueCharacter().name);

        //Sorts out the character order
        while (playingCharacters.peek().getOrder() != start) {
            ClueCharacter c = playingCharacters.poll();
            playingCharacters.offer(c);
        }

        //Sorts out the playing order
        while (!playingCharacters.isEmpty()) playOrder.offer(playingCharacters.poll().player);
    }

    /**
     * Adds all remaining cards into one deck, shuffles and distributes to each player
     */
    public static void distributeCards(List<Card> cards) {
        System.out.print("Shuffling cards..\nDistributing cards to players\n");
        int count = 0;
        for (int i = 0; i < cards.size(); i++) {
            players.get(count).addCard(cards.get(i));
            count++;
            if (count >= players.size()) count = 0;
        }
    }
    
    /**
     * **INCOMPLETE**
     * Moves player in specified direction on the board
     * 
     *  @author Brannon Haines
     */
    public static void movePlayer(String direction) {
	ClueCharacter character = currentTurn.getClueCharacter();
    	Pair<Integer, Integer> location = character.getLocation();
    	int row = location.getOne();
   	int col = location.getTwo();
    	if (direction.equals("Up")) {
    		board[row-1][col] = character;
    		board[row][col] = null;
    		character.setLocation(new Pair<Integer, Integer>(row-1, col));
    	}
    	else if (direction.equals("Down")) {
    		board[row+1][col] = character;
    		board[row][col] = null;
    		character.setLocation(new Pair<Integer, Integer>(row+1, col));
    	}
    	else if (direction.equals("Left")) {
    		board[row][col-1] = character;
    		board[row][col] = null;
    		character.setLocation(new Pair<Integer, Integer>(row, col-1));
    	}
    	else if (direction.equals("Right")) {
    		board[row][col+1] = character;
    		board[row][col] = null;
    		character.setLocation(new Pair<Integer, Integer>(row, col+1));
    	}
    	
    }
    

    /**
     * Loads room name and dimensions into map
     */
    public static void loadRooms() {
        rooms.add(new Room("Kitchen", new Pair<>(0, 0)));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(7, 7));

        rooms.add(new Room("Ball Room", new Pair<>(0, 9)));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(8, 8));

        rooms.add(new Room("Conservatory", new Pair<>(0, 19)));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(5, 6));

        rooms.add(new Room("Billard Room", new Pair<>(8, 19)));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(5, 6));

        rooms.add(new Room("Library", new Pair<>(14, 19)));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(5, 6));

        rooms.add(new Room("Study", new Pair<>(21, 18)));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(3, 7));

        rooms.add(new Room("Hall", new Pair<>(18, 10)));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(6, 6));

        rooms.add(new Room("Lounge", new Pair<>(19, 0)));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(5, 8));

        rooms.add(new Room("Dining Room", new Pair<>(10, 0)));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(6, 9));

        rooms.add(new Room("Middle Room", new Pair<>(10, 11)));
        roomLocations.put(rooms.get(rooms.size() - 1), new Pair<>(6, 5));
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
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(0, 8));

        characters.add(new ClueCharacter("Mr Green", 3));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<Integer, Integer>(0, 17));

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
     * Places room and character cards on the board, also sets entrances to rooms
     * 
     * @author Brannon Haines
     */
    public static void placeCards() {
        placeRooms();
        placeCharacters();
        setEntrances();
    }

    /**
     * Places room on the board
     * 
     * @author Brannon Haines
     */
    public static void placeRooms() {
        Iterator iterator = roomLocations.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Room, Pair<Integer, Integer>> roomLocation = (Map.Entry) iterator.next();
            Room room = (Room) roomLocation.getKey();
            Pair<Integer, Integer> dimension = (Pair<Integer, Integer>) roomLocation.getValue();
            int height = (int) dimension.getOne();
            int width = (int) dimension.getTwo();
            int nextRow = room.TLSquare.getOne();  // starting row
            int nextCol = room.TLSquare.getTwo();  // starting column
            if (room.name.equals("Middle Room")) {  // Middle room can't be accessed
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        board[nextRow][nextCol] = new Impassable(false);
                        nextCol++;
                    }
                    nextRow++;
                    nextCol = room.TLSquare.getTwo();  // need to set back to starting column
                }
            } else {  // is a room that can be accessed
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        board[nextRow][nextCol] = room;
                        nextCol++;
                    }
                    nextRow++;
                    nextCol = room.TLSquare.getTwo();  // need to set back to starting column
                }
            }
        }
    }

    /**
     * Places character on board
     *
     *@author Brannon Haines
     */
    public static void placeCharacters() {
        for (Player player : players) {
        	ClueCharacter character = player.getClueCharacter();
        	for (Map.Entry<ClueCharacter, Pair<Integer, Integer>> entry : charLocations.entrySet()) {
        		if (character.name.equals(entry.getKey().name)) {
        			int row = entry.getValue().getOne();
        			int col = entry.getValue().getTwo();
        			board[row][col] = character;
        			character.setLocation(entry.getValue());
        		}
        	}
        }
    }

    /**
     * Loads the entrances to each room on the board
     * 
     * @author Brannon Haines
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
     * 
     * @author Brannon Haines
     */
    public static void setEntrances() {
        for (Pair<Integer, Integer> location : entranceLocations) {
            int row = location.getOne();
            int col = location.getTwo();
            board[row][col] = new Impassable(true);
        }

    }

    /**
     * Validity check upon users input
     * If input is invalid, keeps asking for a valid input
     *
     * @param i, user input of number of players
     */
    static int validInputCheck(int i) {
        while (true) {
            if (i >= MIN_PLAYERS && i <= MAX_PLAYERS) {
                return i;
            } else {
                System.out.println("Number of players must be between 2 and 6...");
                return validInputCheck(Integer.parseInt(INPUT.nextLine()));
            }
        }

    }
    
    /**
     * Prints the current state of the board
     * 
     * @author Brannon Haines
     */
    public static void printBoard() {
    	clearScreen();
        String output = "";
        for (int row = 0; row < 24; row++) {
            output += "|";
            for (int col = 0; col < 25; col++) {
                Card cell = board[row][col];
                if (cell != null) {
                    output += cell.getCharRep() + "|";
                } else {
                    output += "_|";
                }
            }
            output += "\n";
        }
        System.out.println(output);
    }
    
    /**
     * Clears the console screen by printing 20 empty lines
     * 
     * @author Brannon Haines
     */
    public static void clearScreen() {
    	for (int i = 0; i < 20; i++) {
    		System.out.println();
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

        gameSolution = new Suggestion(w, c, r);
    }

    /**
     * Deals remaining cards (not including solution cards) to players hands
     * NOTE: this method must be called after makeSolution()
     */
    public void dealCards() {
        ArrayList<Card> toDeal = new ArrayList<>();

        //add all cards but solution cards to new deck
        for (Weapon w : weapons) toDeal.add(w);
        for (ClueCharacter c : characters) toDeal.add(c);
        for (Room r : rooms) toDeal.add(r);

        //shuffle said deck
        Collections.shuffle(toDeal);

        //deal between players
        while (!toDeal.isEmpty()) {
            for (Player p : players) {
                p.addCard(toDeal.get(toDeal.size()));
                toDeal.remove(toDeal.size());
            }
        }

    }

    /**
     * This loops over the players apart from the one that instantiated the suggestion
     * @param player
     * @param other
     * @param s
     */
    public void makeSuggestion(Player player, Player other, Suggestion s) {
        //Move other player to suggested room
        other.setCurrentRoom(s.getRoom());

        //Move weapon to suggested room
        s.getWeapon().setRoom(s.getRoom());


        for (Player p : playOrder) {
            if (!p.equals(player)) {
                ArrayList<Card> matchingCards = new ArrayList<>();

                for (Card c : p.hand) {
                    if (c == s.getCharacter() || c == s.getRoom() || c == s.getWeapon()) {
                        matchingCards.add(c);
                    }
                }

                getPlayerToScreen(p);

                if (!matchingCards.isEmpty()) {
                    System.out.println("You can refute with the following cards: ");
                    System.out.println("(0) - None");
                    for (int i = 0; i < matchingCards.size(); i++) {
                        System.out.printf("(%d) - %s\n", i+1, matchingCards.get(i));
                    }

                    System.out.println("\nChoose a card to refute with:");
                    int refIndex = INPUT.nextInt();

                    if (refIndex != 0) p.refuteCard = matchingCards.get(refIndex-1);
                } else {
                    System.out.println("You have no cards to refute with");
                }
            }
        }
        
        // Now relay the refute cards
        for(Player p : playOrder) {
            if (p.refuteCard != null) {
                System.out.printf("%s has refuted with card \"%s\"\n", p.getName(), p.refuteCard);
            }

            p.refuteCard = null;
        }

        //Player can choose to make an accusation
        System.out.print("Enter 'Y' if you would like to make an accusation that " + s.getCharacter().toString()
                + " commited a murder using " + s.getWeapon().toString() + " in " + s.getRoom().toString());

        if (INPUT.nextLine().equals("Y")) {
            //Make accusation
            makeAccusation(s);
        }

        //Game resumes
    }

    //Players accusation is incorrect and they get kicked out of the game

    public void makeAccusation(Suggestion s) {
        if (s == gameSolution) {
            //PLAYER WINS
        }
    }

    public void getPlayerToScreen(Player p) {
        System.out.println("\n\n\n\n");
        System.out.println("Player " + p.getName() + "'s turn.\n (Click to continue)");

        INPUT.next();
    }

	@Override
	/**
	 * Checks which key was pressed for movement
	 * 
	 * @author Brannon Haines
	 */
	public void keyPressed(KeyEvent keyEvent) {
		int key = keyEvent.getKeyCode();
		if (key == keyEvent.VK_UP){
			movePlayer("Up");
		}
		else if (key == keyEvent.VK_DOWN) {
			movePlayer("Down");
		}
		else if (key == keyEvent.VK_LEFT) {
			movePlayer("Left");
		}
		else if (key == keyEvent.VK_RIGHT) {
			movePlayer("Right");
		}
		
	}

	@Override
	public void keyReleased(KeyEvent key) {
	}

	@Override
	public void keyTyped(KeyEvent key) {
	}
}

