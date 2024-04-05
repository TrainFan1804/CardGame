package cardmaster;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class Ablagestapel extends Pile {

    public Ablagestapel() {

        super();
    }
    
    public void clear() {

        this.cardPile.clear();
    }

    public Shape getTopShape() {
        
        return this.getTopCard().getShape();
    }
    
}
