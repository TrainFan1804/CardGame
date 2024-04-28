package cardmaster.cards;

import cardmaster.DiscardPile;
import cardmaster.Shape;

public class Tripel extends Card {

    public Tripel(Shape shape) {
        
        super(shape, "Tripel");
    }

    @Override
    public double calcCredits(DiscardPile[] discardPiles) {
        
        int countSameShape = 0;

        for (DiscardPile pile : discardPiles) {
            if (!pile.isEmpty() && pile.getTopCard().getShape() == this.getShape()) {
                countSameShape++;
            }
        }
        return (countSameShape / 3) * 5; 
    }
    
}
