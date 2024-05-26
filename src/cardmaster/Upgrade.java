package cardmaster;

import cardmaster.interfaces.Item;

public class Upgrade implements Item {

    private UpgradeDescriptions description;
    private double price;

    @Override
    public int getPrice() {
        return (int)price;
    }

    public Upgrade(UpgradeDescriptions upgradeName) {

        this.description = upgradeName;
    }

    
    public UpgradeDescriptions getDescription() {

        return description;
    }

    @Override
    public String toString() {

        return "(" + this.price + ") " + description.getDescription();
    }

    @Override
    public void calcPrice(double timesPurchased) {
        
        if (timesPurchased == 0) {
            this.price = 1;
        } else {
            this.price += (timesPurchased * timesPurchased) + 1;
        }
    }

    /**
     * ENUMS für die Upgrades im Shop
     * <p>
     * Ein getter für die Beschreibung des Upgrades vorhanden
     */
    public enum UpgradeDescriptions {
    
        ADD_SHOP("Additional card in shop"), 
        ADD_STACK("Additional play stack"),
        ADD_HANDCARD("Additional hand card");

        private String description;

        UpgradeDescriptions(String description) {

            this.description = description;
        }

        /**
         * Getter für die Upgradebeschreibung
         * @return Beschreibung des Upgrades
         */
        public String getDescription() {

            return this.description;
        }
    }
}
