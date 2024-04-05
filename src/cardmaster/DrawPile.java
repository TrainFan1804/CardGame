package cardmaster;

import java.util.Collections;

/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */


public class DrawPile extends Pile {

    public DrawPile() {

        super();
    }

    public void mischen() {

        Collections.shuffle(cardPile);
    }

}
