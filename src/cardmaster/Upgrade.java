package cardmaster;

import cardmaster.interfaces.Item;

public class Upgrade implements Item {

    private UpgradeDescriptions description;
    private double price;

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
        return description.getDescription();
    }

    public void calcPrice(double timesPurchased) {
        
        if (timesPurchased == 0) {
            this.price = 1;
        } else {
            this.price += (timesPurchased * timesPurchased) + 1;
        }
    }

    public enum UpgradeDescriptions {
    
        ADD_SHOP("Additional card in shop"), 
        ADD_STACK("Additional play stack"),
        ADD_HANDCARD("Additional hand card");

        private String description;

        UpgradeDescriptions(String description) {

            this.description = description;
        }

        public String getDescription() {

            return this.description;
        }
    }
}
