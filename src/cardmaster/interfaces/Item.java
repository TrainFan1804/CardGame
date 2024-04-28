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

    void calcPrice(double credits);
    
    int getPrice();
}
