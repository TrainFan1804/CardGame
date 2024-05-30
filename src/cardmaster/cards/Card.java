package cardmaster.cards;

import java.util.Objects;
// java import
import java.util.Random;
import cardmaster.DiscardPile;
import cardmaster.Shape;
import cardmaster.interfaces.Item;

/**
 * <h1>Punkte</h1> 0.5 Punkte für jeden nicht leeren Ablagestapel und 0.5 Punkte
 * für jede einzigartige Form.
 * <p>
 * Beispiele:
 * 
 * <pre>
 *   -
 *   - Stern < gelegt
 *   -
 * </pre>
 * 
 * 0.5 für nicht leere Stapel + 0.5 für die Form Stern
 * 
 * <pre>
 * -Kreis - Stern < gelegt - Kreis
 * </pre>
 * 
 * 1.5 für nicht leere Stapel + 1.0 für die Formen Kreis und Stern.
 */
public abstract class Card implements Item {

	Shape shape;
	String name;
	int price;

	public Card(Shape shape, String name) {

		this.shape = shape;
		this.name = name;
	}

	/**
	 * Berechnet credits beim legen von Karten und diese werden dann bei dem Spieler
	 * hinzuaddiert
	 * 
	 * @param discardPiles
	 * @return credits
	 */
	public abstract double calcCredits(DiscardPile[] discardPiles);

	/**
	 * @return Gibt die Form der Karte zurück.
	 */
	public Shape getShape() {

		return this.shape;
	}

	/**
	 * @return Gibt den Namen der Karte zurück. z.B.: Chance
	 */
	public String getName() {

		return this.name;
	}

	@Override
	public int getPrice() {

		return this.price;
	}

	@Override
	public String toString() {

		return this.shape.toString() + " " + this.name;
	}

	@Override
	public void calcPrice(double credits) {

		Random random = new Random();

		double basePrice = credits / 4;

		double variation = (random.nextDouble(0.8, 1.2));

		basePrice *= variation;
		basePrice += ((double) random.nextInt(3) - 1);

		int finalPrice = (int) Math.round(basePrice);

		if (finalPrice < 1) {

			this.price = 1;
			return;
		}

		this.price = finalPrice;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		Card card = (Card) obj;
		return shape == card.shape && name.equals(card.name);
	}

	@Override
	public int hashCode() {

		return Objects.hash(name, shape);
	}
}
