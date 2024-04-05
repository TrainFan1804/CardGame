package cardmaster;

// custom import
import cardmaster.cards.Card;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class Hand {
    
    private Card[] handCards;

    public Hand() {

        this.handCards = new Card[4];
    }

    public void addCard(Card card) {

        for (int i = 0; i < this.handCards.length; i++) {

            if (this.handCards[i] == null) {

                this.handCards[i] = card;
                System.out.println("ZIEHE KARTE");
                return;
            }
            
            System.out.println("Karten auf der Hand: " + this.getHandCardsCount());

        }
    }

    public boolean isNotFull() {

        for (int i = 0; i < this.handCards.length; i++) {

            if (this.handCards[i] == null) {

                return true;
            }
        }

        return false;
    }

    public Card getHandCard(int handCardIndex) {

        return this.handCards[handCardIndex];
    }

    public void removeCard(Card card) {

        for (int i = 0; i < handCards.length; i++) {

            if (card == this.handCards[i]) {
                
                this.handCards[i] = null;
            } else {
                //throw new IllegalArgumentException();
            }
        }
    }

    public void sortCardsOnHand() {

        boolean isSorted = false;
        while (!isSorted) {

            isSorted = true;
            for (int i = 0; i < this.handCards.length - 1; i++) {

                if (this.handCards[i] == null && this.handCards[i + 1] != null) {

                    this.handCards[i] = this.handCards[i+1];
                    this.handCards[i+1] = null;
                    isSorted = false;
                } 
            }
        }   
    }

    public int getHandCardsCount() {

        int o = 0;
        for (int i = 0; i < this.handCards.length; i++) {

            if (this.handCards[i] != null) {
                
                o++;
            }
        }

        return o;
    }

    public Shape getTopShape() {

        return this.handCards[0].getShape();
    }

}
