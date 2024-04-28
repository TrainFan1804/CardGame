package cardmaster;

// java Import
import cardmaster.cards.Card;
import cardmaster.collections.AlgoArrayList;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class DiscardPile {

    private static final int DEFAULT_SIZE = 10;

    private AlgoArrayList cardPile;    

    public DiscardPile() {

        this.cardPile = new AlgoArrayList(DEFAULT_SIZE);
    }
    
    public void addCard(Card newCard) {

        this.cardPile.add(newCard);
    }

    public void clear() {

        this.cardPile.clear();
    }

    public boolean isEmpty() {

        return this.cardPile.size() == 0;
    }

	public Card getTopCard() {

        Card card = (Card) this.cardPile.getItemAtIndex(this.cardPile.size() - 1);
        return card;
	}

    public Shape getTopShape() {
		
        if (this.getTopCard() == null) {

            return null;
        }

		return this.getTopCard().getShape();
	}

    public Card[] getAllCards() {

        Object[] items = this.cardPile.toArray();
        Card[] cards = new Card[this.cardPile.size()];

        for (int i = 0; i < this.cardPile.size(); i++) {
            
            if(items[i] instanceof Card) {

                cards[i] = (Card) items[i];
            }
        }
        return cards;
    }
}
