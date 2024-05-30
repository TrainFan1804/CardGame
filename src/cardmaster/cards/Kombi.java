package cardmaster.cards;

import cardmaster.CardFactory;
import cardmaster.DiscardPile;
import cardmaster.Shape;

public class Kombi extends Card {

    private Card cardOne;
    private Card cardTwo;

    /**
     * Generiert eine Kombi Karte mit einer bestimmten Shape
     * 
     * @param shape Die Shape der neuen Karte
     */
    public Kombi(Shape shape) {
        super(shape, "Kombi");

        this.cardOne = CardFactory.getDefaultFactory().createRandomByShape(shape);
        this.cardTwo = CardFactory.getDefaultFactory().createRandomByShape(shape);
    }

    /**
     * Generiert eine Kombi Karte, die aus zwei anderen Karten besteht
     * 
     * @param cardOne Die erste Karte
     * @param cardTwo Die zweite Karte
     */
    public Kombi(Card cardOne, Card cardTwo) {
        super(cardOne.getShape(), "Kombi");

        this.cardOne = cardOne;
        this.cardTwo = cardTwo;
    }

    @Override
    public double calcCredits(DiscardPile[] discardPiles) {
        
        double credits = 0;

        credits += cardOne.calcCredits(discardPiles);
        credits += cardTwo.calcCredits(discardPiles);

        return credits;
    }

    @Override
    public String toString() {

        return "Kombi(" + cardOne + " + " + cardTwo + ")";
    }
}
