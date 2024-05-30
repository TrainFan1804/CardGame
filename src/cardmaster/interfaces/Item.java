package cardmaster.interfaces;

/**
 * Dies ist ein Markerinterface, welches dazu dienst, alle Typen zu makieren, welche im {@link cardmaster.Shop} kaufbar sind.
 * 
 * @see cardmaster.cards.Card
 * @see cardmaster.Upgrade
 * 
 * @author o.le
 * @since 20.04.2024
 */
public interface Item { 

    /**
     * Berechnet den Preis für das Item im Shop, anhand der Credits des Players
     * 
     * @param credits
     */
    void calcPrice(double credits);
    
    /**
     * Getter für Item-Preise
     * @return Preis des Items
     */
    int getPrice();
}
