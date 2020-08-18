import javax.swing.*;
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
    private static final Queue<ClueCharacter> characterOrder = new ArrayDeque<>();
    private static final Queue<Player> playOrder = new ArrayDeque<>();
    static Card[][] board = new Card[24][25];
    private static GUI ux;
    private static ArrayList<ClueCharacter> allCharacters = new ArrayList<>();
    private static Suggestion gameSolution;
    public Player currentTurn;

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
        ux = new GUI();

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

        placeCards();
    }

    /**
     * Uses user input to get how many players and player names
     **/
    public static void getPlayerInfo() {
        // Find out how many players
        int numPlayers;

        String[] choices = new String[(MAX_PLAYERS - MIN_PLAYERS) + 1];
        for (int i = MIN_PLAYERS; i <= MAX_PLAYERS; i++) choices[i - 2] = String.valueOf(i);

        numPlayers = Integer.parseInt(
                (String) JOptionPane.showInputDialog(ux,
                        "",
                        "Choose how many players",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        choices,
                        choices[0]
                )
        );

        ArrayList<ClueCharacter> selectablePlayers = new ArrayList<>();
        selectablePlayers.addAll(characters);

        // Get player names and assign them a character
        for (int i = 0; i < numPlayers; i++) {
            String playerName = "";
            ClueCharacter chosenCharacter;

            while (playerName.equals("")) {
                playerName = JOptionPane.showInputDialog("Enter name for player " + (i + 1) + ":");
            }

            chosenCharacter =
                    (ClueCharacter) JOptionPane.showInputDialog(
                            ux,
                            "Available:",
                            "Choose a Character",
                            0,
                            null,
                            selectablePlayers.toArray(),
                            selectablePlayers.toArray()[0]
                    );

            selectablePlayers.remove(chosenCharacter);
            players.add(new Player(playerName, chosenCharacter, (i + 1)));
            chosenCharacter.addPlayer(players.get(players.size() - 1));
        }
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

        // Clear Screen 20 times
        for (int i = 0; i < 20; i++) System.out.println();

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

        rooms.add(new Room("Billiard Room", new Pair<>(8, 19)));
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
        charLocations.put(characters.get(characters.size() - 1), new Pair<>(24, 8));

        characters.add(new ClueCharacter("Col Mustard", 1));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<>(17, 1));

        characters.add(new ClueCharacter("Mrs White", 2));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<>(0, 10));

        characters.add(new ClueCharacter("Mr Green", 3));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<>(0, 15));

        characters.add(new ClueCharacter("Mrs Peacock", 4));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<>(6, 24));

        characters.add(new ClueCharacter("Prof Plum", 5));
        characterOrder.offer(characters.get(characters.size() - 1));
        charLocations.put(characters.get(characters.size() - 1), new Pair<>(19, 24));
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
        placeCharacters();
    }

    /**
     * Places room on the board
     */
    public static void placeRooms() {
        while (roomLocations.entrySet().iterator().hasNext()) {
            var roomLocation = (Map.Entry<Room, Pair<Integer, Integer>>) roomLocations.entrySet().iterator().next();
            Room room = roomLocation.getKey();
            Pair<Integer, Integer> dimension = roomLocation.getValue();
            int height = dimension.getOne();
            int width = dimension.getTwo();
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
     */
    public static void placeCharacters() {
        for (ClueCharacter c : allCharacters) {
            int x = c.getLocation().getOne();
            int y = c.getLocation().getTwo();

            board[x][y] = c;
        }
    }

    /**
     * Loads the entrances to each room on the board
     */
    public static void loadEntrances() {
        entranceLocations.add(new Pair<>(6, 5));
        entranceLocations.add(new Pair<>(12, 8));
        entranceLocations.add(new Pair<>(15, 7));
        entranceLocations.add(new Pair<>(19, 7));
        entranceLocations.add(new Pair<>(18, 12));
        entranceLocations.add(new Pair<>(18, 13));
        entranceLocations.add(new Pair<>(20, 15));
        entranceLocations.add(new Pair<>(21, 18));
        entranceLocations.add(new Pair<>(16, 19));
        entranceLocations.add(new Pair<>(14, 21));
        entranceLocations.add(new Pair<>(9, 19));
        entranceLocations.add(new Pair<>(12, 23));
        entranceLocations.add(new Pair<>(4, 19));
        entranceLocations.add(new Pair<>(5, 16));
        entranceLocations.add(new Pair<>(7, 15));
        entranceLocations.add(new Pair<>(7, 10));
        entranceLocations.add(new Pair<>(5, 9));
    }

    /**
     * Sets the entrance as active on the board, creates a room as well
     */
    public static void setEntrances() {
        for (Pair<Integer, Integer> location : entranceLocations) {
            int row = location.getOne();
            int col = location.getTwo();
            board[row][col] = new Impassable();
        }

    }

    public static String printBoard() {
        StringBuilder output = new StringBuilder();
        for (int row = 0; row < 24; row++) {
            output.append("|");
            for (int col = 0; col < 25; col++) {
                Card cell = board[row][col];
                if (cell != null) {
                    output.append(cell.getCharRep()).append("|");
                } else {
                    output.append("_|");
                }
            }
            output.append("\n");
        }
        return output.toString();
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
        toDeal.addAll(weapons);
        toDeal.addAll(characters);
        toDeal.addAll(rooms);

        //shuffle said deck
        Collections.shuffle(toDeal);

        //deal between players
        while (!toDeal.isEmpty()) {
            for (Player p : players) {
                p.addCard(toDeal.get(toDeal.size() - 1));
                toDeal.remove(toDeal.size() - 1);
            }
        }

    }

    /**
     * This loops over the players apart from the one that instantiated the suggestion
     *
     * @param player player making suggestion
     * @param other  player involved in suggestion
     * @param s      suggestion envelope
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
                    if (c == s.character || c == s.room || c == s.weapon) {
                        matchingCards.add(c);
                    }
                }

                getPlayerToScreen(p);

                if (!matchingCards.isEmpty()) {
                    System.out.println("You can refute with the following cards: ");
                    System.out.println("(0) - None");
                    for (int i = 0; i < matchingCards.size(); i++) {
                        System.out.printf("(%d) - %s\n", i + 1, matchingCards.get(i));
                    }

                    System.out.println("\nChoose a card to refute with:");
                    int refIndex = 0;

                    if (refIndex != 0) p.refuteCard = matchingCards.get(refIndex - 1);
                } else {
                    System.out.println("You have no cards to refute with");
                }
            }
        }

        // Now relay the refute cards
        for (Player p : playOrder) {
            if (p.refuteCard != null) {
                System.out.printf("%s has refuted with card \"%s\"\n", p.name, p.refuteCard);
            }

            p.refuteCard = null;
        }

        //Player can choose to make an accusation
        System.out.print("Enter 'Y' if you would like to make an accusation that " + s.getCharacter().toString()
                + " committed a murder using " + s.getWeapon().toString() + " in " + s.getRoom().toString());

        if (true) {
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
        System.out.println("Player " + p.name + "'s turn.\n (Click to continue)");
    }
}
