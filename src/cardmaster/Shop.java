package cardmaster;

//java Import
import java.util.Random;
// custom import
import cardmaster.cards.Card;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class Shop {
    
    private Card[] shopCards;
    private Random random;

    public Shop() {
        
        this.random = new Random();
        
        // Init mit random Karten und immer 5
        this.shopCards = new Card[5];
    }

    /**
     * Laesst den Spieler eine Karte aus dem Shop kaufen
     * 
     * @param shopItemIndex index der Karte die gekauft wird
     * @return gekaufte Karte oder null, falls index ungueltig 
     */
    public Card buy(int shopItemIndex) {

        if (shopItemIndex >= 0 && shopItemIndex < shopCards.length && this.shopCards[shopItemIndex] != null) {

            Card boughtCard = this.shopCards[shopItemIndex];
            
            this.shopCards[shopItemIndex] = null;
            // Aufrücken der Karte, falls Karte in der Mitte gekauft wird (Damit keine Lücken entstehen)
            for (int i = shopItemIndex; i < this.shopCards.length - 1; i++) {

                this.shopCards[i] = this.shopCards[i + 1];
                this.shopCards[i + 1] = null;
            }

            return boughtCard;
        }

        return null;
    }

    /**
     * Berechnet den Preis für den Kauf einer Karte
     * Die Logik dahinter basiert auf dem aktuellen Punktestand des Spielers
     * PREIS P = credits / 4
     * Variation +- 20% des Preises auf die Karte und zufälliger wert von +- 1
     * MinPrice: 1 credit
     * 
     * @param credits Die Punkte des Spielers
     * @return Preis der Karte
     */
    public int calcCardPrice(double credits) {

        double basePrice = credits / 4;

        double variation = (random.nextDouble(0.8, 1.2));

        basePrice *= variation;
        basePrice += ((double) random.nextInt(3) - 1);

        int finalPrice = (int) Math.round(basePrice);
        
        if (finalPrice < 1) {

            return 1;
        }

        return finalPrice;
    }

    /**
     * Generiert neue Karten
     */
    public void generateNewCards(int currentCredits) {

        for (int i = 0; i < 5; i++) {

            this.shopCards[i] = new Card(Shape.getRandomShape());

            shopCards[i].setPrice(calcCardPrice(currentCredits));
            System.out.println(this.shopCards[i].toString() + " kostet: " + shopCards[i].getPrice());
        }
    }

    /**
     * Löscht alle Karten aus dem Shop
     */
    public void clearShop() {

        for (int i = 0; i < 5; i++) {

            this.shopCards[i] = null;
        }
    }

    /**
     * Prüfen, ob der Shop keine Karten mehr hat
     * 
     * @return {@code true} wenn leer, sonst {@code false}
     */
    public boolean isShopEmpty() {

        return this.getShopItemCount() == 0;
    }

    /**
     * Gibt die anzahl der Karten im Shop zurück
     * 
     * @return
     */
    public int getShopItemCount() {

        int count = 0;
        
        for (int i = 0; i < this.shopCards.length; i++) {

            if (this.shopCards[i] != null) {
                count++;
            }
        }
        
        return count;
    }

    /**
     * Generiert eine Karten beschreibung von der Karte am gegebenen Index
     * 
     * @param shopItemIndex Der Index von der Karte
     * @return Die Beschreibung der Karte
     */
    public String getCardDescription(int shopItemIndex) {

        if (shopItemIndex >= 0 && shopItemIndex < this.shopCards.length) {

            return this.shopCards[shopItemIndex].toString(); 
        }

        return null;
    }

    public int getCardPrice(int shopItemIndex) {

        return this.shopCards[shopItemIndex].getPrice();
    }

}
