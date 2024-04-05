package cardmaster;

import java.util.ArrayList;

// custom import
import cardmaster.cards.Card;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public abstract class Pile {

    protected ArrayList<Card> cardPile;
    
    protected Pile() {
        
        this.cardPile = new ArrayList<>();
    }

    public void addCard(Card newCard) {

        this.cardPile.add(newCard);
    }

    public Card getTopCard() {
        
        if (this.cardPile.size() == 0) {

            return null;
        }
            
        return this.cardPile.get(this.cardPile.size() - 1);
    }

    public boolean isEmpty() {
        
        return this.cardPile.isEmpty();
    }
    
    public int size() {

        return this.cardPile.size();
    }

    public Card removeLast() {

        return this.cardPile.remove(this.size() - 1);
    }

}
