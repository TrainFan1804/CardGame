package cardmaster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestCardEqualsAndHashcode1 {

	@Rule
	public Timeout globalTimeout = Timeout.millis(100);

	private static final List<String> CARD_NAMES;

	static {
		final var cardNames = new ArrayList<>(List.of("Chance", "Paar", "Tripel"));
		// Für die nächste Serie
		try {
			Class.forName("cardmaster.TestQuadrupelCard");
			cardNames.add("Quadrupel");
		} catch (ClassNotFoundException ignore) {
		}
		CARD_NAMES = cardNames;
	}

	@Test
	public void cardsAreEqualIfFactoryIsCalledWithSameArguments() {
		final var defaultFactory = CardFactory.getDefaultFactory();
		for (String cardName : CARD_NAMES) {
			for (Shape shape : Shape.values()) {
				for (int i = 0; i < 100; i++) {
					assertEquals(defaultFactory.create(cardName, shape), defaultFactory.create(cardName, shape));
				}
			}
		}
	}

	@Test
	public void cardsAreNotEqualIfFactoryIsCalledWithDifferentArguments() {
		final var defaultFactory = CardFactory.getDefaultFactory();
		for (String cardName0 : CARD_NAMES) {
			for (Shape shape0 : Shape.values()) {
				for (String cardName1 : CARD_NAMES) {
					for (Shape shape1 : Shape.values()) {
						if (cardName0.equals(cardName1) && shape0.equals(shape1)) {
							continue;
						}
						for (int i = 0; i < 100; i++) {
							assertNotEquals(defaultFactory.create(cardName0, shape0),
									defaultFactory.create(cardName1, shape1));
						}
					}
				}
			}
		}
	}

	@Test
	public void equalCardsHaveSameHashCode() {
		final var defaultFactory = CardFactory.getDefaultFactory();
		for (String cardName : CARD_NAMES) {
			for (Shape shape : Shape.values()) {
				for (int i = 0; i < 100; i++) {
					final var card0 = defaultFactory.create(cardName, shape);
					final var card1 = defaultFactory.create(cardName, shape);
					if (card0.equals(card1)) {
						assertEquals(card0.hashCode(), card1.hashCode());
					}
				}
			}
		}
	}
}
