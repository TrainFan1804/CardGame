package cardmaster;

//java import
import cardmaster.collections.AlgoArrayList;
import cardmaster.interfaces.Item;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
@SuppressWarnings("rawtypes")
public class Shop {
    
    private static final int DEFAULT_SIZE = 8;
    private int maxShopSize;

    // Kaufzählungen für jede Art von Upgrade


    private AlgoArrayList shopItems;

    public Shop() {

        this.shopItems = new AlgoArrayList(DEFAULT_SIZE);
        this.maxShopSize = DEFAULT_SIZE;
    }

    /**
     * Fügt ein Item in dem Shop hinzu
     * 
     * @param item
     * @param calculationValue für die Methode calcPrice
     */
    public void addItem(Item item, double calculationValue) {

        item.calcPrice(calculationValue);
        this.shopItems.add(item);
    }

    /**
     * Kauft ein Item aus dem Shop und gibt es auch zurück
     * 
     * @param shopItemIndex
     * @return gekauftes Item
     */
    public Item buy(int shopItemIndex) {

        if (shopItemIndex < this.shopItems.size() && 
            this.shopItems.getItemAtIndex(shopItemIndex) != null) {

            Object temp = this.shopItems.getItemAtIndex(shopItemIndex);


            
            this.shopItems.delete(shopItemIndex);
            return (Item)temp;
        }

        return null;
    }

    /**
     * Prüft ob der Shop leer ist.
     * 
     * @return boolean
     */
    public boolean isEmpty() {

        return this.shopItems.size() == 0;
    }

    /**
     * 
     * @return die Menge an Items im Shop
     */
    public int getItemCount() {
        int o = 0;

        for (int i = 0; i < shopItems.size(); i++) {

            if (this.shopItems.getItemAtIndex(i) != null) o++;
        }
        
        return o;
    }

    /**
     * Generiert eine Karten beschreibung von der Karte am gegebenen Index
     * 
     * @param shopItemIndex Der Index von der Karte
     * @return Die Beschreibung der Karte
     */
    public String shopItemDescription(int shopItemIndex) {

        if (shopItemIndex >= 0 && shopItemIndex < this.shopItems.size()) {

            return this.itemPrice(shopItemIndex) + " " + this.shopItems.getItemAtIndex(shopItemIndex).toString(); 
        }

        throw new IndexOutOfBoundsException();
    }

    /**
     * 
     * @param shopItemIndex
     * @return Preis des Items an dem Index
     */
    public int itemPrice(int shopItemIndex) {

        
        Item item = (Item)this.shopItems.getItemAtIndex(shopItemIndex);
        return item.getPrice();
    }

    /**
     * Cleart die Items im Shop
     */
    public void clearShopItems() {

        for (int i = this.getItemCount(); i > 0; i--) {

            this.shopItems.delete(i);
        }
    }

    /** 
     * @return maximale Größe des Shops
     */
    public int getSize() {

        return this.maxShopSize;
    }

    /**
     * Erhöht die Größe des Shops um 1
     */
    public void sizeUpgrade() {
        
        this.maxShopSize++;
    }

    /**
	 * Liefert alle Items aus dem Shop zurück
	 * 
	 * @return Alle Items aus dem Shop als Array
	 */
    public Object[] getAllItems() {

        return this.shopItems.toArray();
    }
}
