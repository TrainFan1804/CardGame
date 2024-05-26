package cardmaster;

import static cardmaster.TestUtils.CHANCE;
import static cardmaster.TestUtils.SHAPE_PREDICATES;
import static cardmaster.TestUtils.createGameMatching;
import static cardmaster.TestUtils.createGameWithCardsMatching;
import static cardmaster.TestUtils.createGameWithChanceCard;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import cardmaster.Game.Mode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestChanceCard {

	@Rule
	public Timeout globalTimeout = Timeout.millis(10000);

	@Test
	public void singleChanceCard() {
		final var game = createGameWithChanceCard(1000, true);
		while (game.getMode() != Mode.END) {
			// Runden werden gespielt
			var credits = game.getCredits();
			game.endShopping();
			assertEquals(Mode.PLAYING, game.getMode());
			game.play(game.getHandCard(0), 0);
			credits += 0.5 + 0.5; // eine Karte + eine Form
			assertEquals(credits, game.getCredits(), 1E-12);
		}
	}

	@Test
	public void multipleChanceCardSameShape() {
		final var shapeToBuy = new Shape[1];
		// Erzeugt ein Spiel mit zwei Chance-Karten gleicher Form.
		final var game = createGameMatching("Contains two Chance cards of same shape.", 1000, g -> {
			final var shapeCounts = new boolean[Shape.values().length];
			for (int i = 0; i < g.getShopItemCount(); i++) {
				final var description = g.getShopItemDescription(i);
				if (CHANCE.test(description)) {
					for (Shape shape : Shape.values()) {
						if (SHAPE_PREDICATES.get(shape).test(description)) {
							if (shapeCounts[shape.ordinal()]) {
								shapeToBuy[0] = shape;
								return true; // a second Chance card with the shape
							}
							shapeCounts[shape.ordinal()] = true;
							break;
						}
					}
				}
			}
			return false;
		});
		// buy the two chance cards
		int remaining = 2;
		for (int i = game.getShopItemCount() - 1; i >= 0; i--) {
			final var description = game.getShopItemDescription(i);
			if (CHANCE.test(description) && SHAPE_PREDICATES.get(shapeToBuy[0]).test(description)) {
				--remaining;
				game.buy(i);
				if (remaining == 0) {
					break;
				}
			}
		}
		assert remaining == 0;
		while (game.getMode() != Mode.END) {
			// Es wird mit den zwei Karten bis zum Ende gespielt
			var credits = game.getCredits();
			game.endShopping();
			assertEquals(Mode.PLAYING, game.getMode());
			game.play(game.getHandCard(0), 0);
			credits += 0.5 + 0.5; // eine Karte + eine Form
			assertEquals(credits, game.getCredits(), 1E-12);
			assertEquals(Mode.PLAYING, game.getMode());
			game.play(game.getHandCard(0), 1);
			credits += 2 * 0.5 + 0.5; // zwei Karten + eine Form
			assertEquals(credits, game.getCredits(), 1E-12);
		}
	}

	@Test
	public void multipleChanceCardDifferentShapes() {
		final var game = createGameWithCardsMatching("Contains a Chance card of every shape.", 1000,
				CHANCE.and(SHAPE_PREDICATES.get(Shape.CIRCLE)), CHANCE.and(SHAPE_PREDICATES.get(Shape.STAR)),
				CHANCE.and(SHAPE_PREDICATES.get(Shape.SQUARE)));

		while (game.getMode() != Mode.END) {
			var credits = game.getCredits();
			game.endShopping();
			assertEquals(Mode.PLAYING, game.getMode());
			game.play(game.getHandCard(0), 0);
			credits += 0.5 + 0.5; // eine Karte + eine Form
			assertEquals(credits, game.getCredits(), 1E-12);
			assertEquals(Mode.PLAYING, game.getMode());
			game.play(game.getHandCard(0), 1);
			credits += 2 * 0.5 + 2 * 0.5; // zwei Karten + zwei Formen
			assertEquals(credits, game.getCredits(), 1E-12);
			assertEquals(Mode.PLAYING, game.getMode());
			game.play(game.getHandCard(0), 2);
			credits += 3 * 0.5 + 3 * 0.5; // drei Karten + drei Formen
			assertNotEquals(Mode.PLAYING, game.getMode());
			assertEquals(credits, game.getCredits(), 1E-12);
		}
	}
}
