package Cluedo;

import Cluedo.Card;

public class Weapon extends Card {
  
    private String weaponName;
  
    public Weapon(String weaponName){
      this.weaponName = weaponName;
    }
  
    @Override
    public String cardDescription(){
      return weaponName;
    }
}