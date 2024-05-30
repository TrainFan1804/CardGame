package cardmaster;

// java Import
import cardmaster.cards.Card;
import cardmaster.collections.AlgoArrayList;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */

@SuppressWarnings("rawtypes")
public class DiscardPile {

    private static final int DEFAULT_SIZE = 10;

    private AlgoArrayList cardPile;

    public DiscardPile() {

        this.cardPile = new AlgoArrayList(DEFAULT_SIZE);
    }
    
    /**
     * Fügt eine Karte zum Ablagestapel hinzu
     * 
     * @param newCard
     */
    public void addCard(Card newCard) {

        this.cardPile.add(newCard);
    }

    /**
     * Leert den Ablagestapel
     */
    public void clear() {

        this.cardPile.clear();
    }

    /**
     * Schaut ob der jeweilige Ablagestapel leer ist.
     * 
     * @return boolean
     */
    public boolean isEmpty() {

        return this.cardPile.size() == 0;
    }

    /**
     * 
     * @return Karte, welche ganz oben auf dem Ablagestapel liegt
     */
	public Card getTopCard() {

        Card card = (Card) this.cardPile.getItemAtIndex(this.cardPile.size() - 1);
        return card;
	}

    /**
     * 
     * @return NULL, wenn keine Karte vorhanden ist
     * @return Shape der Karte, welche ganz Oben auf dem Ablagestapel liegt
     */
    public Shape getTopShape() {
		
        if (this.getTopCard() == null) {

            return null;
        }

		return this.getTopCard().getShape();
	}

    /**
     * 
     * @return Karten-Array mit allen Karten in dem jeweiligen ABlagestapel
     */
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

    @Override
	public String toString() {

        if (this.cardPile.size() == 0) {
            
            return "Ablagestapel ist leer";
        } 

        return this.cardPile.getItemAtIndex(this.cardPile.size() - 1).toString();
	}
}
