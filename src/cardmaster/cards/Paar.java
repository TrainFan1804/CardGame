package cardmaster.cards;

import cardmaster.DiscardPile;
import cardmaster.Shape;

public class Paar extends Card {

    /**
     * Generiert eine Paar Karte mit einer bestimmten Shape
     * 
     * @param shape Die Shape der neuen Karte
     */
    public Paar(Shape shape) {

        super(shape, "Paar");
    }

    @Override
    public double calcCredits(DiscardPile[] discardPiles) {
        
        int countSameShape = 0;

        for (DiscardPile pile : discardPiles) {
            if (!pile.isEmpty() && pile.getTopCard().getShape() == this.getShape()) {
                countSameShape++;
            }
        }
        return (countSameShape / 2) * 2; // Für jedes Paar 2 Punkte
    }
    
}
