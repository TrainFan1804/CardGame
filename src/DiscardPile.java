/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class DiscardPile extends Pile{

    public DiscardPile() {

        super(0);
    }
    
    @Override
    protected void addCard(Card newCard) {

        throw new UnsupportedOperationException("Unimplemented method 'addCard'");
    }

    public void clear() {

        this.cardPile.clear();
    }
    
}
