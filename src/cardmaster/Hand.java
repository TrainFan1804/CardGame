package cardmaster;

// java import
import cardmaster.cards.Card;
import cardmaster.collections.AlgoArrayList;

/**
 * Diese Klasse liefert eine Hand fuer das Spiel.
 * 
 * @author o.le
 * @since 13.03.2024
 */
@SuppressWarnings("rawtypes")
public class Hand {
    
    private static final int DEFAULT_MAX_SIZE = 4;
    
    private int currentMaxHandSize;
    private AlgoArrayList handCards;

    /**
     * Generiert eine neue Hand. Benutzt intern die Klasse {@link cardmaster.collections.AlgoArrayList}.
     */
    public Hand() {

        this.handCards = new AlgoArrayList(DEFAULT_MAX_SIZE);
        this.currentMaxHandSize = DEFAULT_MAX_SIZE;
    }

    /**
     * Setzt daS Upgrade für die Handgröße um, indem die HandSize um 1 erhöht wird
     */
    public void sizeUpgrade() {

        this.currentMaxHandSize++;
    }

    /**
     * Gibt die Karte an dem gebebenen Index zurueck.
     * 
     * @param handCardIndex Der Index von der Karte, die gesucht wird.
     * @return Gibt die Karte an dem gegeben Index zurueck. Wenn ein Index uebergeben wurde, der nicht in der Hand liegt, wird {@code null} zurueck gegeben.
     */
    public Card getHandCardAtIndex(int handCardIndex) {

        return (Card) this.handCards.getItemAtIndex(handCardIndex);
    }

    /**
     * Liefert die Anzahl der Karten auf der Hand zurueck.
     * 
     * @return Die Anzahl der Karten aus der Hand.
     */
    public int getHandCardsCount() {

        return this.handCards.size();
    }

    public boolean isNotFull() {

        return this.handCards.size() < this.currentMaxHandSize;
    }

    /**
     * Fügt eine Karte in die Hand hinzu
     * 
     * @param card Die neue Karte, die zur Hand hinzugefügt wird
     */
    public void addCard(Card card) {

        this.handCards.add(card);
    }

    /**
     * Diese Methode spielt eine Hand aus der Karte. Die uebergebene Karte wird also aus der Hand geloescht.
     * 
     * @param card Die Karte die aus der Hand gespielt werden soll.
     * @return Gibt {@code true} zurueck, wenn die Karte erfolgreich geloescht wurde, also wenn sie in der Hand vorhanden war.
     *          Gibt {@code false} zurueck, wenn die Karte nicht geloescht wurde, weil sie nicht in der Hand war.
     */
    public boolean playCard(Card card) {
        
        return this.handCards.delete(card);
    }

    /**
     * Uberprueft, ob die uebergebene Karte auf der Hand ist.
     * 
     * @param card Diese Karte wird ueberprueft, ob sie in der Hand ist.
     * @return Gibt {@code true} zurueck, wenn die Karte auf der Hand ist, und wenn sie nicht vorhanden ist,
     *          wird {@code false} zurueck gegeben.
     */
    public boolean checkIfCardIsInHand(Card card) {

        return this.handCards.getIndexFromItem(card) >= 0;
    }

    public Card[] getAllHandCards() {

        Card[] cardsArray = new Card[this.handCards.size()];
        
        for (int i = 0; i < cardsArray.length; i++) {
            
            cardsArray[i] = (Card) this.handCards.getItemAtIndex(i);
        }
        return cardsArray;
    }

    public int size() {
        
        return handCards.size();
    }

    public int getMaxHandCards() {

        return this.currentMaxHandSize;
    }
}