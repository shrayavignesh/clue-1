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
     * Returns a human-readable description of the Card
     * @return
     */
    abstract String getDescription();

    /**
     * Returns a char version of the card for use inside the map
     * TODO - Fix names with same first letter
     * @return
     */
    char getCharRep() {
        return name.charAt(0);
    }
}
