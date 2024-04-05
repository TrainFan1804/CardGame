package cardmaster;

/**
 * Diese Klasse stellt einen Ablagestapel zur verfügung, auf dem die Karten gelegt werden, die der Spieler auf der {@link Hand} hat.
 * 
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class Ablagestapel extends Pile {

    /**
     * Erstellt einen Ablagestapel
     */
    public Ablagestapel() {

        super();
    }
    
    /**
     * Leer den Ablagestapel
     */
    public void clear() {

        this.cardPile.clear();
    }

    /**
     * Liefert die {@link Shape} zurück, die oben auf dem Stapel liegt
     * 
     * @return Die {@code Shape}
     */
    public Shape getTopShape() {
        
        return this.getTopCard().getShape();
    }
    
}
