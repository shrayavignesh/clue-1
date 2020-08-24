import javax.swing.*;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class Board {
    public Card[][] board;

    //BOARD AND TILE DIMENSIONS
    private int height = 25;
    private int width = 24;
    private int tileSize = 30;


    public Board(Card[][] c) {
        //card objects have been placed using PlaceRooms() method in clue class
        //board will be a graphical representation of this 2d array of cards
        board = c;
    }

    /**goes through 2d array of cards specified in Clue.java**/
    public void drawBoard(Graphics g){
        System.out.println("Drawing Board...");

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                Card c = board[x][y];

                if(c.type().equals("Room")){
                    drawTile(g, x, y,"Room");
                }
                else if (c.type().equals("Impassable")){
                    drawTile(g, x, y,"Impassable");
                }
                else {
                    drawTile(g, x, y,"Hallway");
                }
            }
        }
    }

    public void drawCharacters(Card[] currentPlayers){

    }

    /**draws tile colour depending on location type**/
    protected void drawTile(Graphics g, int x, int y, String s) {

        if(s.equals("Room")){
            g.setColor(Color.RED);
        }
        else if(s.equals("Impassable")){
            g.setColor(Color.BLACK);
        }
        else if(s.equals("Hallway")){
            g.setColor(Color.YELLOW);
        }
        else{
            return;
        }

        g.fillRect(x*tileSize, y*tileSize, tileSize, tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(x*tileSize, y*tileSize, tileSize, tileSize);
    }



    public Card onClick(int x, int y){
        int xPos = x % width;
        int yPos = y % width;

        //if card is null - it is a hallway
        return this.board[xPos][yPos];

    }
}
