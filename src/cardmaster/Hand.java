package cardmaster;

// custom import
import cardmaster.cards.Card;

/**
 * Diese Klasse stellt die Hand des Spielers zur verfügung, auf dem bis zu 4 Karten gehalten werden können.
 * 
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class Hand {
    
    private Card[] handCards;

    /**
     * Erstellt ein Hand Objekt
     */
    public Hand() {

        this.handCards = new Card[4];
    }

    /**
     * Fügt eine Karte in die Hand hinzu
     * 
     * @param card Die neue Karte, die zur Hand hinzugefügt wird
     */
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

    /**
     *  Bestimmt, ob die Hand <b>nicht</b> voll ist
     * 
     * @return {@code} true, wenn die Hand <b>nicht</b> voll ist, also wenn auf ihr 0 - 3 Karten sind, wenn 4 Karten auf der Hand sind, wird {@code false} zurückgegben
     */
    public boolean isNotFull() {

        for (int i = 0; i < this.handCards.length; i++) {

            if (this.handCards[i] == null) {

                return true;
            }
        }

        return false;
    }

    /**
     * Liefert eine Karte an einem bestimmten Index zurück
     * 
     * @param handCardIndex Der Index, der angesehen wird
     * @return Die Karte an dem gegeben {@code handCardIndex}
     */
    public Card getHandCard(int handCardIndex) {

        return this.handCards[handCardIndex];
    }

    /**
     * Entfernt die gegebene Karte, falls vorhanden, aus der Hand
     * 
     * @param card Die Karte, die entfernt wird
     */
    public void removeCard(Card card) {

        for (int i = 0; i < handCards.length; i++) {

            if (card == this.handCards[i]) {
                
                this.handCards[i] = null;
            } else {
                //throw new IllegalArgumentException();
            }
        }
    }

    /**
     * "Sortiert" die Hand. Es wird nur überprüft, geguckt, ob keine {@code null} zwischen zwei Karten sind
     */
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

    /**
     * Liefert die Anzahl der Karten, die auf der Hand sind, zurück
     *  
     * @return Anzahl der Karten auf der Hand als {@code int}
     */
    public int getHandCardsCount() {

        int o = 0;
        for (int i = 0; i < this.handCards.length; i++) {

            if (this.handCards[i] != null) {
                
                o++;
            }
        }

        return o;
    }

    /**
     * Liefert die {@link Shape} von der ersten Karte zurück
     * 
     * @return Die Shape der ersten Karte
     */
    public Shape getTopShape() {

        return this.handCards[0].getShape();
    }

}
