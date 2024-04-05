package cardmaster;

// java import
import java.util.Collections;

/**
 * Diese Klasse stellt einen Ziehstapel zur verfügung, auf dem die Karten, die man vom {@link Shop} gekauft hat.
 * 
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class DrawPile extends Pile {

    /**
     * Erstellt einen Ziehstapel
     */
    public DrawPile() {

        super();
    }

    /**
     * Den Ziehstapel mischen
     */
    public void mischen() {

        Collections.shuffle(cardPile);
    }

}
