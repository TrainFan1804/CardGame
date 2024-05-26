package cardmaster.cards;

import cardmaster.DiscardPile;
import cardmaster.Shape;

/**
 * Eine Chance Karte
 */
public class Chance extends Card {

    /**
     * Generiert eine Chance Karte mit einer bestimmten Shape
     * 
     * @param shape Die Shape der neuen Karte
     */
    public Chance(Shape shape) {
        
        super(shape, "Chance");
    }

    @Override
    public double calcCredits(DiscardPile[] discardPiles) {

        boolean hasCircle = false;
        boolean hasStar = false;
        boolean hasSquare = false;
    
        int nonEmptyPiles = 0;
    
        for (DiscardPile pile : discardPiles) {

            if (!pile.isEmpty()) {
            
                nonEmptyPiles++; // Zähle nicht leere Stapel
                Card topCard = pile.getTopCard();
                Shape shape = topCard.getShape();
    
                // Prüfe die Form der Karte und setze entsprechende Booleans
                if (shape == Shape.CIRCLE) {
            
                    hasCircle = true;
                } else if (shape == Shape.STAR) {
            
                    hasStar = true;
                } else if (shape == Shape.SQUARE) {
            
                    hasSquare = true;
                }
            }
        }
    
        // Zähle, wie viele unterschiedliche Formen gefunden wurden
        int uniqueShapes = 0;
        if (hasCircle) uniqueShapes++;
        if (hasStar) uniqueShapes++;
        if (hasSquare) uniqueShapes++;
    
        double points = (0.5 * nonEmptyPiles) + (0.5 * uniqueShapes);
        return points;
    }
}
