/**
 * Abstract container class for manipulation on all types of Card
 */
abstract class Card {
    protected String name;

    /**
     * Name of card entity
     * @return String name
     */
    private String getName() {
        return name;
    }

    /**
     * @return a human-readable description of the Card
     */
    abstract String getDescription();

    /**
     * TODO - Fix names with same first letter
     * @return a char version of the card for use inside the map
     */
    char getCharRep() {
        return name.charAt(0);
    }
}
