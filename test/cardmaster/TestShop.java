package cardmaster;

import static cardmaster.TestUtils.ANY_CARD;
import static cardmaster.TestUtils.CHANCE;
import static cardmaster.TestUtils.SHAPE_PREDICATES;
import static cardmaster.TestUtils.assertInClosedIntervall;
import static cardmaster.TestUtils.buyAllCardsExceptOne;
import static cardmaster.TestUtils.createGameMatching;
import static cardmaster.TestUtils.createGameWhereAllItemsCanBeBought;
import static cardmaster.TestUtils.createGameWithChanceCard;
import static cardmaster.TestUtils.simplePlay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cardmaster.Game.Mode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestShop {

	@Rule
	public Timeout globalTimeout = Timeout.millis(10000);

	@Test
	public void initiallyShopContainsFiveCards() {
		assertEquals(5, getCardInShopCount(new Game(1)));
	}

	@Test
	public void initialShopPrices() {
		assertShopPrices(new Game(1));
	}

	@Test
	public void buyingItemRemovesItFromShop() {
		final var game = new Game(1);
		final var initialSize = game.getShopItemCount();
		int expectedSize = initialSize;
		for (int i = expectedSize - 1; i >= 0; i--) {
			if (game.getShopItemPrice(i) <= game.getCredits()) {
				assertTrue("Player had enough credits to buy but game.buy(" + i + ") returned false.", game.buy(i));
				--expectedSize;
				assertEquals("Items in should must have decreased by one.", expectedSize, game.getShopItemCount());
			}
		}
		assertTrue("At least one item must be bought by now", game.getShopItemCount() < initialSize);
	}

	@Test
	public void priceIsAtLeastOne() {
		final var game = createGameWithChanceCard(100, true);
		while (game.getMode() != Mode.END) {
			for (int i = game.getShopItemCount() - 1; i >= 0; i--) {
				assertTrue(game.getShopItemPrice(i) >= 1);
				game.buy(i);
			}
			game.endShopping();
			simplePlay(game);
		}
	}

	@Test
	public void buyingItemRemovesPriceFromScore() {
		final var game = new Game(1);
		int expectedSize = game.getShopItemCount();

		for (int i = expectedSize - 1; i >= 0; i--) {
			final var price = game.getShopItemPrice(i);
			var credits = game.getCredits();
			if (price <= credits) {
				credits -= price;
				game.buy(i);
				assertEquals(credits, game.getCredits(), 1E-12);
			}
		}
	}

	@Test
	public void tooExpensiveItemCanNotBeBought() {
		final var game = createGameMatching("has a shop where not all items can be bought.", 1, g -> {
			int sum = 0;
			for (int i = 0; i < g.getShopItemCount(); i++) {
				sum += g.getShopItemPrice(i);
			}
			return sum > g.getCredits();
		});
		for (int i = game.getShopItemCount() - 1; i >= 0; i--) {
			final var price = game.getShopItemPrice(i);
			var credits = game.getCredits();
			game.buy(i);
			if (price > credits) {
				assertEquals(credits, game.getCredits(), 1E-12);
			}
		}
		assertTrue(
				"The sum of all prices exceeded the score. When trying to buy all items, at least one could not be bought.",
				game.getShopItemCount() > 0);
	}

	@Test
	public void descriptionContainsShape() {
		// Wenn die Beschreibung die Zeichenfolge "Chance" enthält (Eine Chance-Karte
		// kaufen), dann muss
		// die Form auch in der Beschreibung stehen. Bitte wählen Sie dafür aus "Kreis",
		// "Circle", "●",
		// "Stern", "Star", "★", "Quadrat", "Square", "■" aus.
		final var predicate = CHANCE.negate().or(SHAPE_PREDICATES.get(Shape.CIRCLE) // Kreis und nicht Stern und nicht
																					// Quadrat
				.and(SHAPE_PREDICATES.get(Shape.STAR).negate()).and(SHAPE_PREDICATES.get(Shape.SQUARE).negate()))
				.or(SHAPE_PREDICATES.get(Shape.STAR) // Stern und nicht Kreis und nicht Quadrat
						.and(SHAPE_PREDICATES.get(Shape.CIRCLE).negate())
						.and(SHAPE_PREDICATES.get(Shape.SQUARE).negate()))
				.or(SHAPE_PREDICATES.get(Shape.SQUARE) // Quadrat und nicht Kreis und nicht Stern
						.and(SHAPE_PREDICATES.get(Shape.CIRCLE).negate())
						.and(SHAPE_PREDICATES.get(Shape.STAR).negate()));
		for (int i = 0; i < 10; i++) {
			final var game = new Game(1);
			for (int iShop = 0; iShop < game.getShopItemCount(); iShop++) {
				assertTrue(predicate.test(game.getShopItemDescription(iShop)));
			}
		}
	}

	@Test
	public void shopContainsRandomValues() {
		final var game = createGameWithChanceCard(1000, true);
		final Set<Map<String, Integer>> seenOffers = new HashSet<>();

		while (game.getMode() != Mode.END) {
			seenOffers.add(shopDescriptions(game));
			if (seenOffers.size() > 1) {
				return;
			}
			game.endShopping();
			simplePlay(game);
		}
		fail("This is a probabilistic test! Within 1000 rounds, there must be two unique offers in "
				+ "the shop. (Even when buying just a single card in round 1. This may fail when "
				+ "restocking does not replace old items.)");
	}

	@Test
	public void shopIsRestockedWithNewItems() {
		final var game = createGameWithChanceCard(1000, true);
		assertEquals(Mode.SHOPPING, game.getMode());

		while (true) {
			buyAllCardsExceptOne(game);
			final var lastDescriptions = shopDescriptions(game).keySet();
			game.endShopping();
			assertEquals(Mode.PLAYING, game.getMode());
			simplePlay(game);
			if (game.getMode() == Mode.END) {
				break;
			}
			assertEquals(Mode.SHOPPING, game.getMode());
			assertTrue(game.getShopItemCount() >= 5);

			lastDescriptions.removeAll(shopDescriptions(game).keySet());
			if (!lastDescriptions.isEmpty()) {
				// Some item was available last round but is not available in this round. Shop
				// must be
				// restocked entirely.
				return;
			}
		}
		fail("This is a probabilistic test! Shop items are refreshed every round not just restocked. "
				+ "Within 1000 rounds there should be some item not available in the next round");
	}

	@Test
	public void shopPricesDependsOnCurrentScore() {
		final var game = createGameWithChanceCard(1000, true);
		assertEquals(Mode.SHOPPING, game.getMode());
		for (int round = 1; round < 1000; round++) {
			game.endShopping();
			assertEquals(Mode.PLAYING, game.getMode());
			// gain 1 credit
			game.play(game.getHandCard(0), 0);
			assertEquals(Mode.SHOPPING, game.getMode());
			assertShopPrices(game);
		}
	}

	@Test
	public void testIsShopEmpty() {
		final var game = createGameWhereAllItemsCanBeBought(1);
		for (int i = game.getShopItemCount() - 1; i >= 0; i--) {
			assertFalse(game.isShopEmpty());
			game.buy(i);
		}
		assertTrue(game.isShopEmpty());
	}

	private static Map<String, Integer> shopDescriptions(Game game) {
		assert game.getMode() == Mode.SHOPPING;
		final Map<String, Integer> res = new HashMap<>();
		for (int i = 0; i < game.getShopItemCount(); i++) {
			res.compute(game.getShopItemDescription(i), (d, count) -> count == null ? 1 : count + 1);
		}
		return res;
	}

	/**
	 * Überprüft die Preise der Gegenstände. Es ist egal, ob Preise auf- oder
	 * abgerundet werden.
	 */
	public static void assertShopPrices(Game game) {
		assert game.getMode() == Mode.SHOPPING;
		final var minPrice = Math.max(1, (int) (game.getCredits() / 4.0 * 0.8) - 1);
		final var maxPrice = Math.max(1, (int) Math.ceil((game.getCredits() / 4.0 * 1.2)) + 1);
		for (int iShop = 0; iShop < game.getShopItemCount(); iShop++) {
			if (ANY_CARD.test(game.getShopItemDescription(iShop))) {
				assertInClosedIntervall(minPrice, maxPrice, game.getShopItemPrice(iShop));
			} else {
				// Für die erste Serie ignorieren
				assertEquals(1, game.getShopItemPrice(iShop));
			}
		}
	}

	/**
	 * Bestimmt die Anzahl an Karten im Shop.
	 */
	private static int getCardInShopCount(Game game) {
		var res = 0;
		for (int iShop = 0; iShop < game.getShopItemCount(); iShop++) {
			if (ANY_CARD.test(game.getShopItemDescription(iShop))) {
				++res;
			}
		}
		return res;
	}
}
