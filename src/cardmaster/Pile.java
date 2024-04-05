package cardmaster;

// java import
import java.util.ArrayList;
// custom import
import cardmaster.cards.Card;

/**
 * Diese Abstrakte Klasse stellt eine Vorlage für {@link DrawPile} und {@link Ablagestapel} zur verfügung.
 * 
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public abstract class Pile {

    protected ArrayList<Card> cardPile;
    
    /**
     * Erstellt einen Stapel
     */
    protected Pile() {
        
        this.cardPile = new ArrayList<>();
    }

    /**
     * Fügt eine Karte zu dem Stapel hinzu
     * 
     * @param newCard Die neue Karte vom Typ {@link Card}
     */
    public void addCard(Card newCard) {

        this.cardPile.add(newCard);
    }

    /**
     * Liefert die oberste Karte auf dem Stapel zurück
     * 
     * @return Die oberste Karte vom Typ {@link Card}. Wenn der Stapel leer ist, wird {@code null} zurückgegeben
     */
    public Card getTopCard() {
        
        if (this.cardPile.size() == 0) {

            return null;
        }
            
        return this.cardPile.get(this.cardPile.size() - 1);
    }

    /**
     * Bestimmt, ob der Stapel leer ist oder nicht
     * 
     * @return Liefert {@code true} zurück, wenn der Stapel leer ist, sonst {@code false}
     */
    public boolean isEmpty() {
        
        return this.cardPile.isEmpty();
    }
    
    /**
     * Bestimmt die Größe des Stapel
     * 
     * @return Die Größe des Stapel als {@code int}
     */
    public int size() {

        return this.cardPile.size();
    }

    /**
     * Entfernt die {@link Card} mit dem höchsten Index. Die Karte mit dem höchsten Index stellt die oberste Karte dar.
     * 
     * @return Die oberste Karte vom Typ {@link Card}
     */
    public Card removeLast() {

        return this.cardPile.remove(this.size() - 1);
    }

}
