public class Room extends Card {
    ClueCharacter character = null;
    ClueCharacter other = null;
    Weapon weapon;
    Weapon otherWeapon;
    
    public Room(String roomName){
        this.name = roomName;
    }
    
    /**
    * Adds weapon to room when game is made, may not have a weapon
    */
    public void addWeapon(Weapon weapon){
        this.weapon = weapon;
    }
    
    /**
    * Adds character to the room
    */
    public void addCharacter(ClueCharacter character){
        this.character = character;
    }
    
    /**
    * Only used if a character has been named in a suggestion and they are not in this room
    */
    public void bringCharacterToRoom(ClueCharacter other){
        this.other = other;
    }
    
    /**
    * Only used if a weapon has been named in a suggestion and they are not in this room
    */
    public void bringWeaponToRoom(Weapon otherWeapon){
        this.otherWeapon = otherWeapon;
    }
    
    public void setEntrance() {
    	entrance = true;
    }
    
    @Override
    public String cardDescription() {
        if(weapon == null) {
            return "You've entered the " + roomName + " room, there's nothing in here.\n";
        }
        else{
            return "You've entered the " + roomName + " room, you see a " + weapon.cardDescription() + ".\n";
        }
    }
}