/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public abstract class Pile {

    protected Card[] cardPile;

    protected Pile(int size) {

        this.cardPile = new Card[size];
    }

    protected abstract boolean addCard(Card newCard);
}
