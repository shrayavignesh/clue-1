import java.util.*;

public class Clue {
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
    static Card[][] board = new Card[24][25];
    private static ArrayList<ClueCharacter> allCharacters = new ArrayList<>();
    private static Queue<ClueCharacter> characterOrder = new ArrayDeque<>();
    private static Queue<Player> playOrder = new ArrayDeque<>();
    public static Player currentTurn;
    public static Suggestion gameSolution; // Final solution

    /**
     * Rounds
     */
    private static boolean gameover = false;

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

        gameSolution = new Suggestion(
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

        //placeCards();
        round();

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
    }

    /**
     * Queue ordering of players through sorting out the playing and unused characters
     *
     * @Author:Laurence_Malata
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

        // Clear Screen 20 times
        for (int i = 0; i < 20; i++) System.out.println("");

        System.out.print("Characters playing are :\n");
        for (Player p : players) {
            System.out.printf("\tPlayer %d (%s) %s\n", p.playerNumber, p.name, p.clueCharacter.name);
        }

        System.out.print("First player to start is..\nRoll dice..\n");
        int start = order.get(new Random().nextInt(order.size()));  //Random number
        Player player = allCharacters.get(start).player;
        System.out.printf("~~Player %d (%s): %s~~\n", player.playerNumber, player.name, player.clueCharacter.name);

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
     *
     * @Author:Laurence_Malata
     */
    public static void distributeCards(List<Card> cards) {
        System.out.print("Shuffling cards..\nDistributing cards to players\n");
        int count = 0;
        for (Card card : cards) {
            players.get(count).addCard(card);
            count++;
            if (count >= players.size()) count = 0;
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

    public static void placeCards() {
        placeRooms();
        setEntrances();
    }

    /**
     * Places room on the board
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
                        board[nextRow][nextCol] = new Impassable(true);
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
            board[row][col] = new Impassable(false);
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

    public static String printBoard() {
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
        return output;
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
     * Makes the Suggestion Script
     * Author:Laurence_Malata
     */
    public static void makeSuggestion(){
        System.out.print("Do you want to check your cards? Type 'Yes' to continue: ");
        String input = INPUT.nextLine();
        if(input.equals("Yes")) currentTurn.getHand();

        int count = 0, index;

        StringBuilder cards = new StringBuilder();
        System.out.print("Which character are you suggesting?\n");
        for (ClueCharacter c : allCharacters) cards.append(count++).append(" [").append(c.name).append("]   ");
        System.out.print("Type the number: ");
        index = INPUT.nextInt() - 1;
        ClueCharacter suggestedCharacter = allCharacters.get(index);

        cards = new StringBuilder();
        count = 0;
        System.out.print("What weapon was used?\n");
        for (Weapon w : weapons) cards.append(count++).append(" [").append(w.name).append("]   ");
        System.out.print("Type the number: ");
        index = INPUT.nextInt() - 1;
        Weapon suggestedWeapon = weapons.get(index);

        Suggestion s = new Suggestion(suggestedWeapon,suggestedCharacter,currentTurn.getCurrentRoom());
        refutation(currentTurn,s);
    }

    /**
     * This loops over the players apart from the one that instantiated the suggestion
     * @param player
     * @param s
     *
     * @Author:Laurence_Malata
     */
    public static void refutation(Player player, Suggestion s) {
        //Move other player to suggested room
        if(s.getCharacter().getPlayer() != null) s.getCharacter().getPlayer().setCurrentRoom(s.getRoom());


        //Move weapon to suggested room
        s.getWeapon().setRoom(s.getRoom());

        for (Player p : playOrder) {
            if (!p.equals(player)) {
                while(s.getPlayerWithCard() == null){
                    boolean hasSuggestedCard = s.checkHand(p.hand,p);
                    if(hasSuggestedCard) {
                        System.out.print("Player " + p.name + " has a card to refute to Player" + player.name +".\n");
                        getPlayerToScreen(p);
                        s.printCards();
                        int refIndex = INPUT.nextInt();
                        player.setRefuteCard(s.getPresentCards().get(refIndex-1));
                    }
                    else {
                        System.out.print("Player " + p.name + " has no cards to refute.\n");
                    }
                }
            }
            if(s.getPlayerWithCard() != null) break;
        }

        //Return back to current player
        getPlayerToScreen(player);
        System.out.print("Player " + s.getPlayerWithCard() + " has refuted card: "+player.getRefuteCard() +"\n");
        player.setPreviousRoom(player.getCurrentRoom());
        //After suggestion, current Player will be in this room until next turn

        //Accusation
        makeAccusation();

        //Game resumes
    }

    /**
     *  Creates the Accusation Script
     *
     * @Author:Laurence_Malata
     */
    public static void makeAccusation(){
        System.out.print("Would you like to make an accusation? You can only do this ONCE!\nType 'Yes'" +
                "to continue or 'No' to resume the game: ");

        if (INPUT.nextLine().equals("Yes")) {
            System.out.print("Which character are you accusing?\n");
            int count = 0,number;
            StringBuilder cards = new StringBuilder();  //Prints the options
            for (ClueCharacter c : allCharacters) cards.append(count++).append(" [").append(c.name).append("]   ");
            System.out.print("Type the number: ");
            number = INPUT.nextInt() - 1;
            ClueCharacter accusedCharacter = allCharacters.get(number);

            System.out.print("What weapon was used?\n");
            cards = new StringBuilder();
            count = 0;
            for (Weapon w : weapons) cards.append(count++).append(" [").append(w.name).append("]   ");
            System.out.print("Type the number: ");
            number = INPUT.nextInt() - 1;
            Weapon accusedWeapon = weapons.get(number);

            System.out.print("Where did it happen?\n");
            cards = new StringBuilder();
            count = 0;
            for (Room r : rooms) cards.append(count++).append(" [").append(r.name).append("]   ");
            System.out.print("Type the number: ");
            number = INPUT.nextInt() - 1;
            Room accusedRoom = rooms.get(number);

            //Make accusation
            finalAccusation(currentTurn ,new Suggestion(accusedWeapon,accusedCharacter,accusedRoom));
        }
    }

    /**
     *  Players make a final accusation and if they win, the game is over. If their accusation is false, they are
     *  kicked out of the game.
     *
     * @param p
     * @param s
     * @Author:Laurence_Malata
     */
    public static void finalAccusation(Player p, Suggestion s) {
        System.out.print("Player " + p.getName() + " is going to make an accusation!\n");
        System.out.print("Player " + p.getName() + " is accusing " + s.getCharacter() + " of murder using " +
                s.getWeapon() + " in " + s.getRoom() +"\n");

        //Wins the game
        if(gameSolution.getCharacter().equals(s.getCharacter()) && gameSolution.getRoom().equals(s.getRoom()) &&
        gameSolution.getWeapon().equals(s.getWeapon())){
            gameover = true;
            System.out.print("Player " + p.getName() + " has solved the murder mystery!\n");
            System.out.print("Player " + p.getName() + " wins the game!\n");
        }
        //Loses the game, player's accusation is incorrect
        else if (!gameSolution.getCharacter().equals(s.getCharacter()) || !gameSolution.getRoom().equals(s.getRoom()) ||
                !gameSolution.getWeapon().equals(s.getWeapon())){
            p.setPlayStatus(false);
            System.out.print("Player " + p.getName() + " is out of the game!\n");
        }
    }

    public static String getPlayerToScreen(Player p) {
        System.out.println("\n\n\n\n");
        System.out.println("Player " + p.name + "'s turn.\n (Press ENTER to continue)");

        return INPUT.nextLine();
    }

    /**
     * Method that questions the current Player if he/she wants to move rooms or not. This only applies
     * if the current Player wasn't in the current Room during their turn, but during another player's
     * turn (during Suggestion)
     *
     * @return 'Yes' or 'No' input
     * @Author:Laurence_Malata
     */
    public static String optionToMove(){
        String input = "";
        if(currentTurn.getCurrentRoom() != null && currentTurn.getPreviousRoom() != null){
            if(!currentTurn.getCurrentRoom().equals(currentTurn.getPreviousRoom())){
                System.out.printf("You were previously in the %s...Do you wish to stay in the %d?\n Type and enter 'Yes' " +
                        "to stay or 'No' to move: ", currentTurn.getPreviousRoom().name, currentTurn.getCurrentRoom().name);
                input = INPUT.nextLine();
                while(!input.equals("Yes") || !input.equals("No")){
                    System.out.print("Wrong input. Please type either 'Yes' or 'No' and enter: ");
                    input = INPUT.nextLine();
                }
            }
        }
        return input;
    }

    /**
     *  Main loop of the game, not finished yet!!
     *
     */
    public static void round(){
        Dice firstDice = new Dice();
        Dice secondDice = new Dice();

        //Loop until it reaches gameover
        while(!gameover){
            //Loop that checks for players that can't play anymore, to remove them at the front of queue
            //And put them back to the queue
            while(!playOrder.peek().canStillPlay()) {
                currentTurn = playOrder.poll();
                playOrder.offer(currentTurn);
            }
            currentTurn = playOrder.poll(); //Can still play so removed from front of queue to play current turn
            getPlayerToScreen(currentTurn);
            int first = firstDice.roll() + 1;
            int second = secondDice.roll() + 1;
            int total = first + second; //Total moves to use for move method
            System.out.printf("Rolling dice..\nYou rolled a %d and a %d..\n" +
                    "You can move %d squares", first,second,total);

            //Was moved from previous room through suggestion
            String input = optionToMove();

            //Didn't move rooms
            if(input.equals("Yes")){
                makeSuggestion();
            }
            //Wants to move rooms or has to move boards
            else if(input.equals("No") || input.equals("")) {
                currentTurn.setCurrentRoom(null);   //Player moves out of room
                //
                //  Move turn
                //
                //Might need currentTurn.setCurrentRoom() <-- If player enters the room after moving
            }

            playOrder.offer(currentTurn); //Player will be at the back of the playing order
        }
    }
}
