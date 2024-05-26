package cardmaster;

import static cardmaster.TestUtils.CHANCE;
import static cardmaster.TestUtils.SHAPE_PREDICATES;
import static cardmaster.TestUtils.ThrowsExpected.thr;
import static cardmaster.TestUtils.assertThrows;
import static cardmaster.TestUtils.createGameWithCardsMatching;
import static cardmaster.TestUtils.createGameWithChanceCard;
import static cardmaster.TestUtils.simplePlay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import cardmaster.Game.Mode;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestExceptions {

	@Rule
	public Timeout globalTimeout = Timeout.millis(1000);

	@Test
	public void gameConstructorAssertsArgument() {
		// Ab sofort soll eine IllegalArgumentException geworfen werden.

		// 0 ist nicht erlaubt
		assertThrows(() -> {
			new Game(0);
		}, thr(IllegalArgumentException.class));
		// Auch werte kleiner als 0 nicht.
		assertThrows(() -> {
			new Game(-1);
		}, thr(IllegalArgumentException.class));

		// Auch wenn eine CardFactory angegeben ist.
		assertThrows(() -> {
			new Game(0, CardFactory.getDefaultFactory());
		}, thr(IllegalArgumentException.class));
		assertThrows(() -> {
			new Game(-1, CardFactory.getDefaultFactory());
		}, thr(IllegalArgumentException.class));

		assertThrows(false, NullPointerException.class, () -> new Game(1, null));
	}

	@Test
	public void gamePlayAssertsCardIsInHand() {
		// Get some card
		final var donorGame = createGameWithChanceCard(1, true);
		donorGame.endShopping();
		assertEquals(Mode.PLAYING, donorGame.getMode());
		final var donorCard = donorGame.getHandCard(0);

		// Create a game with a different card
		final var game = createGameWithCardsMatching(
				"Contains a Chance card with a shape different than " + donorCard.getShape(), 1,
				// Eine Chance-Karte mit einer anderen Form
				CHANCE.and(SHAPE_PREDICATES.get(donorCard.getShape()).negate()));
		game.endShopping();
		final var oldCredits = game.getCredits();

		assertThrows(() -> game.play(donorCard, 0), thr(IllegalArgumentException.class, e -> {
			assertTrue("Message must contain the card, that is not in this hand.",
					e.getMessage().contains(donorCard.toString()));
			assertTrue("Message must mentioned that the error is related to the hand..",
					e.getMessage().contains("hand"));
		}));

		assertEquals(Mode.PLAYING, game.getMode());
		assertEquals(1, game.getHandCardsCount());
		assertEquals(oldCredits, game.getCredits(), 1E-12);
	}

	@Test
	public void gamePlayAssertsStackIndex() {
		final var game = createGameWithChanceCard(1, true);
		game.endShopping();
		assertEquals(Mode.PLAYING, game.getMode());
		// negative index are invalid
		assertThrows(() -> game.play(game.getHandCard(0), -1), thr(IndexOutOfBoundsException.class));
		assertEquals(Mode.PLAYING, game.getMode());
		// indexes >= stack count are invalid
		assertThrows(() -> game.play(game.getHandCard(0), game.getStacksCount()), thr(IndexOutOfBoundsException.class));
		assertEquals(Mode.PLAYING, game.getMode());
		assertThrows(() -> game.play(game.getHandCard(0), game.getStacksCount() + 2),
				thr(IndexOutOfBoundsException.class));
	}

	public static Game createGameInShoppingMode() {
		final var game = new Game(1);
		assertEquals(Mode.SHOPPING, game.getMode());
		return game;
	}

	public static Game createGameInPlayingMode() {
		final var game = createGameWithChanceCard(1, true);
		game.endShopping();
		assertEquals(Mode.PLAYING, game.getMode());
		return game;
	}

	public static Game createGameInEndMode() {
		final var game = createGameWithChanceCard(1, true);
		game.endShopping();
		assertEquals(Mode.PLAYING, game.getMode());
		simplePlay(game);
		assertEquals(Mode.END, game.getMode());
		return game;
	}

	public static Consumer<IllegalCallException> createExceptionAssertion(String methodName, Mode allowedMode,
			Mode actuallMode) {
		return e -> {
			final var message = e.getMessage();
			assertTrue(message.contains(methodName));
			assertTrue(message.contains(allowedMode.name()));
			assertTrue(message.contains(actuallMode.name()));
		};
	}

	@Test
	public void gameIsShopEmptyThrowsExceptionInPlayingMode() {
		final var game = createGameInPlayingMode();
		assertThrows(game::isShopEmpty,
				thr(IllegalCallException.class, createExceptionAssertion("isShopEmpty", Mode.SHOPPING, Mode.PLAYING)));
	}

	@Test
	public void gameIsShopEmptyThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(game::isShopEmpty,
				thr(IllegalCallException.class, createExceptionAssertion("isShopEmpty", Mode.SHOPPING, Mode.END)));
	}

	@Test
	public void gameGetShopItemCountThrowsExceptionInPlayingMode() {
		final var game = createGameInPlayingMode();
		assertThrows(game::getShopItemCount, thr(IllegalCallException.class,
				createExceptionAssertion("getShopItemCount", Mode.SHOPPING, Mode.PLAYING)));
	}

	@Test
	public void gameGetShopItemCountThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(game::getShopItemCount,
				thr(IllegalCallException.class, createExceptionAssertion("getShopItemCount", Mode.SHOPPING, Mode.END)));
	}

	@Test
	public void gameGetShopItemDescriptionThrowsExceptionInPlayingMode() {
		final var game = createGameInPlayingMode();
		assertThrows(() -> game.getShopItemDescription(0), thr(IllegalCallException.class,
				createExceptionAssertion("getShopItemDescription", Mode.SHOPPING, Mode.PLAYING)));
	}

	@Test
	public void gameGetShopItemDescriptionThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(() -> game.getShopItemDescription(0), thr(IllegalCallException.class,
				createExceptionAssertion("getShopItemDescription", Mode.SHOPPING, Mode.END)));
	}

	@Test
	public void gameGetShopItemPriceThrowsExceptionInPlayingMode() {
		final var game = createGameInPlayingMode();
		assertThrows(() -> game.getShopItemPrice(0), thr(IllegalCallException.class,
				createExceptionAssertion("getShopItemPrice", Mode.SHOPPING, Mode.PLAYING)));
	}

	@Test
	public void gameGetShopItemPriceThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(() -> game.getShopItemPrice(0),
				thr(IllegalCallException.class, createExceptionAssertion("getShopItemPrice", Mode.SHOPPING, Mode.END)));
	}

	@Test
	public void gameBuyThrowsExceptionInPlayingMode() {
		final var game = createGameInPlayingMode();
		assertThrows(() -> game.buy(0),
				thr(IllegalCallException.class, createExceptionAssertion("buy", Mode.SHOPPING, Mode.PLAYING)));
	}

	@Test
	public void gameBuyThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(() -> game.buy(0),
				thr(IllegalCallException.class, createExceptionAssertion("buy", Mode.SHOPPING, Mode.END)));
	}

	@Test
	public void gameIsDrawPileEmptyThrowsExceptionInPlayingMode() {
		final var game = createGameInPlayingMode();
		assertThrows(game::isDrawPileEmpty, thr(IllegalCallException.class,
				createExceptionAssertion("isDrawPileEmpty", Mode.SHOPPING, Mode.PLAYING)));
	}

	@Test
	public void gameIsDrawPileEmptyThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(game::isDrawPileEmpty,
				thr(IllegalCallException.class, createExceptionAssertion("isDrawPileEmpty", Mode.SHOPPING, Mode.END)));
	}

	@Test
	public void gameEndShoppingThrowsExceptionInPlayingMode() {
		final var game = createGameInPlayingMode();
		assertThrows(game::endShopping,
				thr(IllegalCallException.class, createExceptionAssertion("endShopping", Mode.SHOPPING, Mode.PLAYING)));
	}

	@Test
	public void gameEndShoppingThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(game::endShopping,
				thr(IllegalCallException.class, createExceptionAssertion("endShopping", Mode.SHOPPING, Mode.END)));
	}

	@Test
	public void gameGetHandCardsCountThrowsExceptionInShoppingMode() {
		final var game = createGameInShoppingMode();
		assertThrows(game::getHandCardsCount, thr(IllegalCallException.class,
				createExceptionAssertion("getHandCardsCount", Mode.PLAYING, Mode.SHOPPING)));
	}

	@Test
	public void gameGetHandCardsCountThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(game::getHandCardsCount,
				thr(IllegalCallException.class, createExceptionAssertion("getHandCardsCount", Mode.PLAYING, Mode.END)));
	}

	@Test
	public void gameGetHandCardThrowsExceptionInShoppingMode() {
		final var game = createGameInShoppingMode();
		assertThrows(() -> game.getHandCard(0),
				thr(IllegalCallException.class, createExceptionAssertion("getHandCard", Mode.PLAYING, Mode.SHOPPING)));
	}

	@Test
	public void gameGetHandCardThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(() -> game.getHandCard(0),
				thr(IllegalCallException.class, createExceptionAssertion("getHandCard", Mode.PLAYING, Mode.END)));
	}

	@Test
	public void gamePlayThrowsExceptionInShoppingMode() {
		final var game = createGameInShoppingMode();
		assertThrows(() -> game.play(null, -1),
				thr(IllegalCallException.class, createExceptionAssertion("play", Mode.PLAYING, Mode.SHOPPING)));
	}

	@Test
	public void gamePlayThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(() -> game.play(null, -1),
				thr(IllegalCallException.class, createExceptionAssertion("play", Mode.PLAYING, Mode.END)));
	}

	@Test
	public void gameGetTopShapesThrowsExceptionInShoppingMode() {
		final var game = createGameInShoppingMode();
		assertThrows(game::getTopShapes,
				thr(IllegalCallException.class, createExceptionAssertion("getTopShapes", Mode.PLAYING, Mode.SHOPPING)));
	}

	@Test
	public void gameGetTopShapesThrowsExceptionInEndMode() {
		final var game = createGameInEndMode();
		assertThrows(game::getTopShapes,
				thr(IllegalCallException.class, createExceptionAssertion("getTopShapes", Mode.PLAYING, Mode.END)));
	}

	@Test
	public void gameGetShopItemDescriptionIndexOutOfBounds() {
		final var game = createGameInShoppingMode();
		assertThrows(() -> game.getShopItemDescription(-1), thr(IndexOutOfBoundsException.class));
		assertThrows(() -> game.getShopItemDescription(game.getShopItemCount()), thr(IndexOutOfBoundsException.class));
	}

	@Test
	public void gameGetShopItemPriceIndexOutOfBounds() {
		final var game = createGameInShoppingMode();
		assertThrows(() -> game.getShopItemPrice(-1), thr(IndexOutOfBoundsException.class));
		assertThrows(() -> game.getShopItemPrice(game.getShopItemCount()), thr(IndexOutOfBoundsException.class));
	}

	@Test
	public void gameBuyIndexOutOfBounds() {
		final var game = createGameInShoppingMode();
		assertThrows(() -> game.buy(-1), thr(IndexOutOfBoundsException.class));
		assertThrows(() -> game.buy(game.getShopItemCount()), thr(IndexOutOfBoundsException.class));
	}

	@Test
	public void gameGetHandCardIndexOutOfBounds() {
		final var game = createGameInPlayingMode();
		assertThrows(() -> game.getHandCard(-1), thr(IndexOutOfBoundsException.class));
		assertThrows(() -> game.getHandCard(game.getHandCardsCount()), thr(IndexOutOfBoundsException.class));
	}

	@Test
	public void cardFactoryCreate() {
		final var factory = CardFactory.getDefaultFactory();
		assertThrows(false, NullPointerException.class, () -> factory.create(null, Shape.STAR));
		assertThrows(false, NullPointerException.class, () -> factory.create("Paar", null));
		assertThrows(false, NullPointerException.class, () -> factory.create(null, null));
		assertThrows(() -> factory.create("paar", Shape.CIRCLE),
				thr(IllegalArgumentException.class, e -> assertTrue(e.getMessage().contains("paar"))));
		assertThrows(() -> factory.create("Unknown", Shape.CIRCLE),
				thr(IllegalArgumentException.class, e -> assertTrue(e.getMessage().contains("Unknown"))));
	}
}
