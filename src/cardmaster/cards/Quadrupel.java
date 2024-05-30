package cardmaster.cards;

import cardmaster.DiscardPile;
import cardmaster.Shape;

public class Quadrupel extends Card{

    /**
     * Generiert eine Quadrupel Karte mit einer bestimmten Shape
     * 
     * @param shape Die Shape der neuen Karte
     */
    public Quadrupel(Shape shape) {

        super(shape, "Quadrupel");
    }

    @Override
    public double calcCredits(DiscardPile[] discardPiles) {
        
        int countSameShape = 0;

        for (DiscardPile pile : discardPiles) {
            if (!pile.isEmpty() && pile.getTopCard().getShape() == this.getShape()) {
                countSameShape++;
            }
        }
        return (countSameShape / 4) * 10; // Für jedes Paar 2 Punkte
    }
    
}
