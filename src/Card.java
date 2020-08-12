/**
 * Abstract container class for manipulation on all types of Card
 */
abstract class Card {
    private String name;

    protected Card(String name) {
        this.name = name;
    }

    protected Card() {
    }

    /**
     * Name of card entity
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a human-readable description of the Card
     *
     * @return Description of card
     */
    abstract String getDescription();

    /**
     * Returns a char version of the card for use inside the map
     *
     * @return Character Representation of card
     */
    char getCharRep() {
        return name.charAt(0);
    }
}
