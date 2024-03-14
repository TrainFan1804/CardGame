// java import
import java.util.ArrayList;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public abstract class Pile {

    protected ArrayList<Card> cardPile;
    
    protected abstract boolean addCard(Card newCard);
    
    protected Pile(int size) {

        this.cardPile = new ArrayList<>();
    }    
        
}
