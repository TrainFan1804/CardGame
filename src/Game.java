/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */

 // Ich finde hier fehlen noch ein paar commits
 
public class Game {

    private Mode currentMode;
    
    public Game(int maxRounds) {
        
    }

    public double getCredits() {

        return 1.0;
    }

    public Mode getMode() {

        return null;
    }

    public int getStackCound() {

        return 0;
    }

    public boolean isShopEmpty() {

        return false;
    }

    public int getShopItemCount() {

        return 0;
    }

    public String getShopItemDescription(int shopItemIndex) {

        return null;
    }

    public int getShopItemPrice(int shopItemIndex) {

        return 0;
    }

    public boolean buy(int shopItemIndex) {

        return false;
    }

    public boolean isDrawPileEmpty() {

        return false;
    }

    public void endShopping() {


    }

    public int getHandCardsCound() {

        return 0;
    }

    public Card getHandCard(int handCardIndex) {

        return null;
    }

    public void play(Card card, int stackIndex) {

        
    }

    public Shape[] getTopShapes() {

        return null;
    }

}
