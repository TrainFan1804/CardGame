package cardmaster;

// java import
import java.util.Random;

/**
 * Enum Klasse für die Shapes für eine {@link Card}
 * 
 * @author g.ary, o.le
 * @since 05.04.2024
 */
public enum Shape {

    CIRCLE,
    STAR,
    SQUARE;

    /**
     * Liefert eine zufällige {@code Shape} zurück
     * 
     * @return Eine zufällige Shape
     */
    public static Shape getRandomShape() {

        Random random = new Random();

        Shape[] shapes = Shape.values();
        return shapes[random.nextInt(shapes.length)];
    }

}

