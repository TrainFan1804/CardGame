// java import
import java.util.ArrayList;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class Hand {
    
    private ArrayList<Card> handCards;

    public Hand() {

        this.handCards = new ArrayList<Card>();
    }

    public Card getHandCard(int handCardIndex) {

        return this.handCards.get(handCardIndex);
    }

    public int getHandCardsCount() {

        return this.handCards.size();
    }

    public Shape getTopShape() {

        return null;
    }

    public void play(Card card, int stackIndex) {
        
        this.handCards.remove(card);
    }

}
