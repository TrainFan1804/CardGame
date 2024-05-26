package cardmaster;

import java.util.Random;
import cardmaster.cards.*;

public class CardFactory {

    /**
     * Erstellt eine random Karte, aber immer mit dem Namen: "Chance"
     * 
     * @return Card
     */
    public Card createRandom() {

        Random random = new Random();
        Shape[] allCardShapes = Shape.values();
        Shape shape = allCardShapes[random.nextInt(allCardShapes.length)];
        return this.create("Chance", shape);
    }

    private static CardFactory defaultFactory;

    /**
     * Erschafft eine Instanz von CardFactory, welche in der Lage ist, alle Karten
     * in der createRandom() zu erzeugen.
     * 
     * @return Alle möglichen Karten, welche im Spiel verfügbar sind
     */
    public static CardFactory getDefaultFactory() {

        if (defaultFactory == null) {

            defaultFactory = new CardFactory() {

                @Override
                public Card createRandom() {

                    Random random = new Random();
                    String[] allCardTypes = { "Chance", "Paar", "Tripel", "Quadrupel", "Kombi" };
                    Shape[] allCardShapes = Shape.values();

                    String type = allCardTypes[random.nextInt(allCardTypes.length)];
                    Shape shape = allCardShapes[random.nextInt(allCardShapes.length)];

                    return this.create(type, shape);
                }
            };
        }

        return defaultFactory;
    }

    /**
     * Bestimmt mit Hilfe der Parameter welche Karte erstellt werden soll.
     * 
     * @param type
     * @param shape
     * @return Card
     */
    public Card create(String type, Shape shape) {

        if (shape != null && type != null) {

            switch (type) {
                case "Chance":
                    return new Chance(shape);
                case "Paar":
                    return new Paar(shape);
                case "Tripel":
                    return new Tripel(shape);
                case "Quadrupel":
                    return new Quadrupel(shape);
                case "Kombi":
                    return new Kombi(shape);
                default:
                    throw new IllegalArgumentException("Invalid card type: " + type);
            }
        }

        throw new NullPointerException();
    }

    /**
     * Erstellt eine random Shape und damit eine Karte
     * 
     * @param shape
     * @return Card
     */
    public Card createRandomByShape(Shape shape) {

        Random random = new Random();
        String[] allCardTypes = { "Chance", "Paar", "Tripel", "Quadrupel", "Kombi" };

        String type = allCardTypes[random.nextInt(allCardTypes.length)];
        return this.create(type, shape);
    }

    /**
     * Kombiniert, wenn möglich, zwei Karten zu einer Kombi Karte
     * 
     * @param cardOne
     * @param cardTwo
     * @return Card
     */
    public Card combine(Card cardOne, Card cardTwo) {

        if (cardOne == null || cardTwo == null) {

            throw new NullPointerException("nicht null");
        }
        if (!cardOne.getShape().equals(cardTwo.getShape())) {
            String errorMessage = String.format(
                    "Karten müssen die gleiche Form haben. Aktuelle Formen: %s (%s), %s (%s)",
                    cardOne.getShape().name(), cardOne,
                    cardTwo.getShape().name(), cardTwo);
            throw new IllegalArgumentException(errorMessage);
        }

        return new Kombi(cardOne, cardTwo);
    }
}
