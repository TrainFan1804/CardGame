package cardmaster;

// java Import
import cardmaster.cards.Card;
import cardmaster.collections.AlgoArrayList;
import cardmaster.collections.Shuffle;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class DrawPile {

    private static final int DEFAULT_SIZE = 10;
    
    private AlgoArrayList cardPile;    

    public DrawPile() {

        this.cardPile = new AlgoArrayList(DEFAULT_SIZE);
    }
    
    public void addCard(Card newCard) {

        this.cardPile.add(newCard);
    }

    public void mischen() {

        Shuffle.shuffle(this.cardPile);
    }

    public boolean isEmpty() {

        return this.cardPile.size() == 0;
    }

	public Card getTopCard() {
		
		Card card = (Card) this.cardPile.getItemAtIndex(this.cardPile.size() - 1);
        this.cardPile.delete(this.cardPile.size() - 1);
        return card;
	}

    public void addAllCards(Card[] cards) {

        for (Card c : cards) {

            this.cardPile.add(c);
        }

        this.mischen();
    }

    public int size() {

        return this.cardPile.size();
    }
    
}
