package cardmaster;

import java.util.Random;
import cardmaster.cards.*;

public class CardFactory {
    
    public Card createRandom() {

        Random random = new Random();
        Shape[] allCardShapes = Shape.values();
        Shape shape = allCardShapes[random.nextInt(allCardShapes.length)];
        return this.create("Chance", shape);
    }

    private static CardFactory defaultFactory;

    public static CardFactory getDefaultFactory() {
        
        if (defaultFactory == null) {

            defaultFactory = new CardFactory(){

                @Override
                public Card createRandom() {
                
                    Random random = new Random();
                    String[] allCardTypes = {"Chance", "Paar", "Tripel"};
                    Shape[] allCardShapes = Shape.values();
                
                    String type = allCardTypes[random.nextInt(allCardTypes.length)];
                    Shape shape = allCardShapes[random.nextInt(allCardShapes.length)];
                
                    return this.create(type, shape);
                }
            };
        }
        return defaultFactory;
    }

    public Card create(String type, Shape shape) {
        switch (type) {
            case "Chance":
                return new Chance(shape);
            case "Paar":
                return new Paar(shape);
            case "Tripel":
                return new Tripel(shape);
            default:
                throw new IllegalArgumentException("Invalid card type: " + type);
        }
    }
}

