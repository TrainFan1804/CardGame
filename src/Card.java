/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class Card {

    private Shape shape;
    private double point;

    public Card(Shape shape, double point) {

        this.shape = shape;
        this.point = point;

    }

    public Shape getShape() {

        return this.shape;
    }

    public void setShape(Shape shape) {

        this.shape = shape;
    }

    public double getPoint() {

        return this.point;
    }

    public void setPoint(double point) {

        this.point = point;
    }

    public String getName() {
        
        return null;
    }
    
}
