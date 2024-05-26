package cardmaster;

// java Import
import cardmaster.cards.Card;
import cardmaster.collections.AlgoArrayList;
import cardmaster.collections.Shuffle;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */

@SuppressWarnings("rawtypes")
public class DrawPile {

    private static final int DEFAULT_SIZE = 10;
    
    private AlgoArrayList cardPile;    

    /**
     * Erstellt eine DrawPile
     */
    public DrawPile() {

        this.cardPile = new AlgoArrayList(DEFAULT_SIZE);
    }
    
    /**
     * Fügt eine Karte in die Draw Pile hinzu
     * 
     * @param newCard Die neue Karte
     */
    public void addCard(Card newCard) {

        this.cardPile.add(newCard);
    }

    /**
     * Mischt die Drawpile
     */
    public void mischen() {

        Shuffle.shuffle(this.cardPile);
    }

    /**
     * Checkt ob die DrawPile leer ist
     * 
     * @return {@code true} wenn die leer ist sonst {@code false}
     */
    public boolean isEmpty() {

        return this.cardPile.size() == 0;
    }

    /**
     * Liefert die oberste Karte der DrawPile
     * 
     * @return Die Obereste Karte
     */
	public Card getTopCard() {
		
		Card card = (Card) this.cardPile.getItemAtIndex(this.cardPile.size() - 1);
        this.cardPile.delete(this.cardPile.size() - 1);
        return card;
	}

    /**
     * Fügt alle Karten aus einem Array in die Drawpile hinzu
     *  
     * @param cards Das Array der Karten
     */
    public void addAllCards(Card[] cards) {

        for (Card c : cards) {

            this.cardPile.add(c);
        }

        this.mischen();
    }

    /**
     * Liefert die größe der Drawpile
     * 
     * @return Die anzahl der Karten auf der Drawpile
     */
    public int size() {

        return this.cardPile.size();
    }

    public Card[] getAllDrawCards() {

        Card[] cardsArray = new Card[this.cardPile.size()];
        
        for (int i = 0; i < cardsArray.length; i++) {
            
            cardsArray[i] = (Card) this.cardPile.getItemAtIndex(i);
        }
        return cardsArray;
    }
    
}
