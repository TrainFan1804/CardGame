/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class DrawPile extends Pile {

    public DrawPile() {

        super(0);
    }
    
    @Override
    protected boolean addCard(Card newCard) {

        throw new UnsupportedOperationException("Unimplemented method 'addCard'");
    }

    public void mischen() {

    }

    public boolean isEmpty() {

        return this.cardPile.isEmpty();
    }
    
}
