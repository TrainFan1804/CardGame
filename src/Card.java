/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class Card {

    private Shape Shape;
    private double point;

    public Card(Shape shape, double point) {

        this.Shape = shape;
        this.point = point;

    }

    public Shape getShape() {
        return Shape;
    }

    public void setShape(Shape shape) {
        Shape = shape;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public String getName() {
        return null;
    }
    
}
