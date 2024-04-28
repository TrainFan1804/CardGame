package cardmaster;

import java.util.ArrayList;
import java.util.List;

//java import
import cardmaster.collections.AlgoArrayList;
import cardmaster.interfaces.Item;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class Shop {
    
    private static final int DEFAULT_SIZE = 8;
    private int maxShopSize;

    // Kaufzählungen für jede Art von Upgrade


    private AlgoArrayList shopItems;

    public Shop() {

        this.shopItems = new AlgoArrayList(DEFAULT_SIZE);
        this.maxShopSize = DEFAULT_SIZE;
    }

    public void addItem(Item item, double calculationValue) {

        item.calcPrice(calculationValue);
        this.shopItems.add(item);
    }

    public Item buy(int shopItemIndex) {

        if (shopItemIndex < this.shopItems.size() && 
            this.shopItems.getItemAtIndex(shopItemIndex) != null) {

            Item temp = this.shopItems.getItemAtIndex(shopItemIndex);


            
            this.shopItems.delete(shopItemIndex);
            return temp;
        }

        return null;
    }

    public boolean depositDrawPile() {

        return false;
    }

    public boolean isEmpty() {

        return this.shopItems.size() == 0;
    }

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

            return this.shopItems.getItemAtIndex(shopItemIndex).toString(); 
        }

        return null;
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < shopItems.size(); i++) {
            if (shopItems.getItemAtIndex(i) != null) {
                items.add(shopItems.getItemAtIndex(i));
            }
        }
        return items;
    }

    // public Item getItems() {

    //     Item[] items = new Item[this.shopItems.size()];

    //     for (Item item : this.shopItems)
    // }
    public int itemPrice(int shopItemIndex) {

        Item item = this.shopItems.getItemAtIndex(shopItemIndex);
        return item.getPrice();
    }

    public void clearShopItems() {

        for (int i = this.getItemCount(); i > 0; i--) {

            this.shopItems.delete(i);
        }
    }

    public int getSize() {

        return this.maxShopSize;
    }

    public void sizeUpgrade() {
        
        this.maxShopSize++;
    }
}
