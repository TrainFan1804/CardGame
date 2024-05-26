package cardmaster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cardmaster.TestGame.NameShapePair;
import cardmaster.cards.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestCardFactory {

	@Rule
	public Timeout globalTimeout = Timeout.millis(100);
	public static final Set<String> CARD_NAMES;

	static {
		final var cardNames = new ArrayList<>(List.of("Chance", "Paar", "Tripel"));
		// Für die nächste Serie
		try {
			Class.forName("cardmaster.TestQuadrupelCard");
			cardNames.add("Quadrupel");
			cardNames.add("Kombi");
		} catch (ClassNotFoundException ignore) {
		}
		CARD_NAMES = Set.copyOf(cardNames);
	}

	@Test
	public void testCreateAllNamesKnownAndMatchCardName() {
		final var factory = CardFactory.getDefaultFactory();
		for (String name : CARD_NAMES) {
			for (Shape shape : Shape.values()) {
				final var card = factory.create(name, shape);
				assertNotNull(card);
				assertEquals(name, card.getName());
				assertEquals(shape, card.getShape());
			}
		}
	}

	@Test
	public void defaultFactoryCreatesAllCards() {
		Map<String, Set<Shape>> seen = new HashMap<>();
		final var factory = CardFactory.getDefaultFactory();
		for (int i = 0; i < 1000; i++) {
			final var card = factory.createRandom();
			seen.computeIfAbsent(card.getName(), ignore -> new HashSet<>()).add(card.getShape());
		}
		if (seen.size() < CARD_NAMES.size() || !seen.values().stream().allMatch(s -> s.size() == 3)) {
			fail("This is a probabilistic test! When generating 1000 cards, all combinations of name " + CARD_NAMES
					+ " with shape should occur but got: " + seen);
		}
	}

	@Test
	public void defaultFactorySeemsToBeRandomSequence() {
		final var factory = CardFactory.getDefaultFactory();
		final var sample1 = takeSample(factory);
		for (int i = 0; i < 10; i++) {
			if (!sample1.equals(takeSample(factory))) {
				return;
			}
		}
		fail("This is a probabilistic test! When generating a sequence of 10 cards repeatedly, the "
				+ "sequence must be different in at least one of the 10 next sequences");
	}

	@Test
	public void defaultFactorySeemsToProduceEachCardEquallyLikely() {
		// Dieser Test führt einen statistischen Test (Chi-Quadrat-Anpassungstest)
		// durch, um zu testen,
		// ob die Standardfabrik jede Kombination aus Kartenname und Form gleichhäufig
		// gleich häufig
		// erzeugt. In ca. 0,5 % der Aufrufe dieser Funktion, wird fälschlicherweise
		// behauptet, die
		// Implementierung der Standardfabrik sei falsch.
		final var factory = CardFactory.getDefaultFactory();
		// alpha = 0.005
		final double[] lookupTable = { 0.0, 10.597, 16.750, 21.955, 26.757, 31.319, 35.718, 39.997, 44.181, 48.290,
				52.336 // Bis zu 10 verschiedene Kartenarten werden vom Test unterstützt.
		};
		assertEquals(3, Shape.values().length); // Annahme für die Werte in lookupTable

		final var seenNames = new HashSet<String>();
		final var absoluteProbabilities = new HashMap<NameShapePair, Integer>();
		final var sampleSize = 1000;

		for (int i = 0; i < sampleSize; i++) {
			final var card = factory.createRandom();
			seenNames.add(card.getName());
			absoluteProbabilities.compute(new NameShapePair(card), (k, c) -> c == null ? 1 : c + 1);
		}
		assertEquals("Every card type should be generated", CARD_NAMES, seenNames);

		final var expectedAmountPerGroup = (double) sampleSize / (seenNames.size() * Shape.values().length);

		var chiSquare = 0.0;
		for (String name : seenNames) {
			for (Shape shape : Shape.values()) {
				var n = (double) absoluteProbabilities.getOrDefault(new NameShapePair(name, shape), 0);
				n -= expectedAmountPerGroup;
				chiSquare += n * n;
			}
		}
		chiSquare /= expectedAmountPerGroup;
		assertTrue("This is a probabilistic test! The probability of an error is 0.5%",
				chiSquare <= lookupTable[seenNames.size()]);
	}

	@Test
	public void gameUsesFactory() {
		int[] usageCount = new int[1];
		final var game = new Game(1, new CardFactory() {
			@Override
			public Card createRandom() {
				++usageCount[0];
				return CardFactory.getDefaultFactory().createRandom();
			}

			@Override
			public Card create(String name, Shape shape) {
				fail("Game must not use create. Use createRandom() instead.");
				return CardFactory.getDefaultFactory().create(name, shape);
			}
		});
		for (int i = 0; i < game.getShopItemCount(); i++) {
			game.getShopItemDescription(i); // Ensure Cards must be created
		}
		// Repeat to potentially cause more createRandom() calls
		for (int i = 0; i < game.getShopItemCount(); i++) {
			game.getShopItemDescription(i);
		}
		// now the factory must be used five times.
		assertEquals("Exactly five cards must be created.", 5, usageCount[0]);
	}

	private static List<Card> takeSample(CardFactory cardFactory) {
		return Stream.generate(cardFactory::createRandom).limit(10).toList();
	}
}
