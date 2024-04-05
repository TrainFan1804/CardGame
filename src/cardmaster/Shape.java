package cardmaster;

import java.util.Random;

public enum Shape {
    CIRCLE,
    STAR,
    SQUARE;

    public static Shape getRandomShape() {

        Random random = new Random();

        Shape[] shapes = Shape.values();
        return shapes[random.nextInt(shapes.length)];
    }
}

