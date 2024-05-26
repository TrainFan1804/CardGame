package cardmaster;

import static cardmaster.TestUtils.CHANCE;
import static cardmaster.TestUtils.SHAPE_PREDICATES;
import static cardmaster.TestUtils.ThrowsExpected.thr;
import static cardmaster.TestUtils.assertThrows;
import static cardmaster.TestUtils.buyAllCards;
import static cardmaster.TestUtils.createGameWhereAllCardsCanBeBought;
import static cardmaster.TestUtils.createGameWithCardsMatching;
import static cardmaster.TestUtils.createGameWithChanceCard;
import static cardmaster.TestUtils.ignoreExceptions;
import static cardmaster.TestUtils.simplePlay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cardmaster.Game.Mode;
import cardmaster.cards.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestGame {

	@Rule
	public Timeout globalTimeout = Timeout.millis(500);

	@Test
	public void constructorAssertsArgument() {
		// Der Konstruktor von Game soll sein Argument überprüfen (assert). Später
		// lernen Sie einen
		// besseren Mechanismus kennen. Der Test muss auch dann noch funktionieren.

		// 0 ist nicht erlaubt
		assertThrows(() -> new Game(0), thr(AssertionError.class),
				thr(IllegalArgumentException.class, e -> assertConstructorErrorMessages(0, e)));
		// Auch werte kleiner als 0 nicht.
		assertThrows(() -> new Game(-1), thr(AssertionError.class),
				thr(IllegalArgumentException.class, e -> assertConstructorErrorMessages(-1, e)));
	}

	private static void assertConstructorErrorMessages(int maxRounds, IllegalArgumentException e) {
		final var message = e.getMessage();
		assertTrue("Name of invalid argument must appear in the error message.", message.contains("maxRounds"));
		assertTrue("Message must include the actual argument.", message.contains(Integer.toString(maxRounds)));
	}

	@Test
	public void gameStartsWithThreeStacks() {
		assertEquals(3, new Game(1).getStacksCount());
	}

	@Test
	public void gameStartsInShoppingMode() {
		assertEquals(Mode.SHOPPING, new Game(1).getMode());
	}

	@Test
	public void handIsRefilledAutomatically() {
		final var game = createGameWhereAllCardsCanBeBought(1);
		buyAllCards(game);
		game.endShopping();
		assertEquals(Mode.PLAYING, game.getMode());
		assertEquals(4, game.getHandCardsCount());
		assertEquals(Mode.PLAYING, game.getMode());
		game.play(game.getHandCard(0), 0);
		assertEquals(Mode.PLAYING, game.getMode());
		assertEquals(4, game.getHandCardsCount()); // nachgezogen
		assertEquals(Mode.PLAYING, game.getMode());
		game.play(game.getHandCard(0), 0);
		assertEquals(Mode.PLAYING, game.getMode());
		assertEquals(3, game.getHandCardsCount()); // keine Karten mehr da zum Nachziehen
		assertEquals(Mode.PLAYING, game.getMode());
		game.play(game.getHandCard(0), 0);
		assertEquals(Mode.PLAYING, game.getMode());
		assertEquals(2, game.getHandCardsCount());
		assertEquals(Mode.PLAYING, game.getMode());
		game.play(game.getHandCard(0), 0);
		assertEquals(Mode.PLAYING, game.getMode());
		assertEquals(1, game.getHandCardsCount());
		assertEquals(Mode.PLAYING, game.getMode());
		game.play(game.getHandCard(0), 0);
	}

	@Test
	public void handKeepsCards() {
		final var game = createGameWhereAllCardsCanBeBought(1);
		buyAllCards(game);
		game.endShopping();
		var cards = handCards(game);
		game.play(cards.remove(3), 0);
		// Die drei anderen Karten müssen noch in der Hand sein. cards sind die alten
		// Handkarten ohne
		// die, die eben abgelegt wurde.
		assertHandCardsChange(cards, handCards(game), true);

		cards = handCards(game);
		game.play(cards.remove(2), 0);
		// Die drei anderen Karten müssen noch in der Hand sein, aber es wurde keine
		// neue gezogen
		assertHandCardsChange(cards, handCards(game), false);

		cards = handCards(game);
		game.play(cards.remove(1), 0);
		assertHandCardsChange(cards, handCards(game), false);

		cards = handCards(game);
		game.play(cards.remove(0), 0);
	}

	@Test
	public void modeChangesAutomaticallyWhenLastCardIsPlayer() {
		final var game = createGameWithChanceCard(2, true);
		assertEquals(Mode.SHOPPING, game.getMode());
		game.endShopping();
		assertEquals(Mode.PLAYING, game.getMode());
		game.play(game.getHandCard(0), 0);
		// Automatischer wechsel. Da es nicht die letzte Runde war, wir nun wieder
		// geshoppt
		assertEquals(Mode.SHOPPING, game.getMode());
		game.endShopping();
		assertEquals(Mode.PLAYING, game.getMode());
		game.play(game.getHandCard(0), 0);
		// Automatischer wechsel. Es war die letzte Runde
		assertEquals(Mode.END, game.getMode());
	}

	@Test
	public void endShopHasNoEffectIfNoCardWasBoughtEver() {
		final var game = new Game(1);
		game.endShopping(); // Es ist mindestens eine Karte notwendig, um zu spielen.
		assertEquals(Mode.SHOPPING, game.getMode());
	}

	@Test
	public void playAssertsCardIsInHand() {
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

		assertThrows(() -> game.play(donorCard, 0), thr(AssertionError.class),
				thr(IllegalArgumentException.class, e -> {
					assertTrue("Message must contain the card, that is not in this hand.",
							e.getMessage().contains(donorCard.toString()));
					assertTrue("Message must mentioned that the error is related to the hand..",
							e.getMessage().toLowerCase(Locale.ROOT).contains("hand"));
				}));

		assertEquals(Mode.PLAYING, game.getMode());
		assertEquals(1, game.getHandCardsCount());
		assertEquals(oldCredits, game.getCredits(), 1E-12);
	}

	@Test
	public void playAssertsStackIndex() {
		final var game = createGameWithChanceCard(1, true);
		game.endShopping();
		assertEquals(Mode.PLAYING, game.getMode());
		// negative index are invalid
		assertThrows(() -> game.play(game.getHandCard(0), -1), thr(AssertionError.class),
				thr(IndexOutOfBoundsException.class, e -> assertPlayStackIndexError(-1, e)));
		assertEquals(Mode.PLAYING, game.getMode());
		// indexes >= stack count are invalid
		assertThrows(() -> game.play(game.getHandCard(0), game.getStacksCount()), thr(AssertionError.class),
				thr(IndexOutOfBoundsException.class, e -> assertPlayStackIndexError(game.getStacksCount(), e)));
		assertEquals(Mode.PLAYING, game.getMode());
		assertThrows(() -> game.play(game.getHandCard(0), game.getStacksCount() + 2), thr(AssertionError.class),
				thr(IndexOutOfBoundsException.class, e -> assertPlayStackIndexError(game.getStacksCount() + 2, e)));
	}

	private static void assertPlayStackIndexError(int index, IndexOutOfBoundsException e) {
		final var message = e.getMessage();
		assertTrue("Name of invalid argument must appear in the error message.", message.contains("stackIndex"));
		assertTrue("Message must indicate an upper bound of valid values.",
				message.contains("2") || message.contains("3"));
		assertTrue("Message must include the actual argument.", message.contains(Integer.toString(index)));
	}

	@Test
	public void playCardRepeatedlyKeepsGameUnchanged() {
		final var game = createGameWithCardsMatching("A start chance and other cards", 1,
				TestUtils::allCardsCanBeBought, CHANCE.and(SHAPE_PREDICATES.get(Shape.STAR)),
				CHANCE.and(SHAPE_PREDICATES.get(Shape.CIRCLE)));
		game.endShopping();
		assertEquals(Mode.PLAYING, game.getMode());
		Card card = null;
		for (int i = 0; i < game.getHandCardsCount(); i++) {
			assertEquals(Mode.PLAYING, game.getMode());
			final var handCard = game.getHandCard(i);
			if (handCard.getShape() == Shape.STAR && handCard.getName().equals("Chance")) {
				card = handCard;
				break;
			}
		}
		assertNotNull("Star chance card was not in hand but there where only two cards.", card);
		assertEquals(Mode.PLAYING, game.getMode());
		assertEquals(2, game.getHandCardsCount());
		game.play(card, 0);
		assertEquals(Mode.PLAYING, game.getMode());
		assertEquals(1, game.getHandCardsCount());
		final var oldCredits = game.getCredits();
		final var finalCard = card;
		ignoreExceptions(() -> game.play(finalCard, 0), AssertionError.class, IllegalArgumentException.class);
		assertEquals(Mode.PLAYING, game.getMode());
		assertEquals(1, game.getHandCardsCount());
		assertEquals(oldCredits, game.getCredits(), 1E-12);
	}

	@Test
	public void deckIsShuffled() {
		final var game = createGameWithCardsMatching(
				"Contains Chance cards of shape circle and star and all cards can be bought.", 1000,
				TestUtils::allCardsCanBeBought, CHANCE.and(SHAPE_PREDICATES.get(Shape.STAR)),
				CHANCE.and(SHAPE_PREDICATES.get(Shape.CIRCLE)));
		buyAllCards(game);

		// Spieler hat fünf Karten und zwei sind verschieden. Jede kann nach dem Mischen
		// die unterste
		// Karte auf dem Nachziehstapel sein. Dies impliziert, dass es mindestens zwei
		// Möglichkeiten
		// gibt, die ersten vier Karten zu ziehen. In den 1000 Runden ist es sehr
		// wahrscheinlich, dass
		// mindestens zwei dieser Möglichkeiten auftreten.

		Map<NameShapePair, Integer> firstHand = null;
		do {
			game.endShopping();
			if (firstHand == null) {
				firstHand = createHistogramOfHand(game);
			} else {
				if (!firstHand.equals(createHistogramOfHand(game))) {
					return; // Saw another hand.
				}
			}
			simplePlay(game);
		} while (game.getMode() != Mode.END);
		fail("This is a probabilistic test! The draw pile is shuffled at the beginn of every playing "
				+ "phase. The test expects there to be a different composition of hands cards at some "
				+ "point that is different from the initial hand. This is expected because there is a "
				+ "start chance and a circle chance. At some point, each is the fifty card that is not "
				+ "drawn initially.");
	}

	private static Map<NameShapePair, Integer> createHistogramOfHand(Game game) {
		assertEquals(Mode.PLAYING, game.getMode());
		final var res = new HashMap<NameShapePair, Integer>();
		for (int i = 0; i < game.getHandCardsCount(); i++) {
			assertEquals(Mode.PLAYING, game.getMode());
			res.compute(new NameShapePair(game.getHandCard(i)), (k, c) -> c == null ? 1 : c + 1);
		}
		return res;
	}

	record NameShapePair(String name, Shape shape) {

		public NameShapePair(Card card) {
			this(card.getName(), card.getShape());
		}
	}

	/**
	 * Überprüft das alle Karten in <code>expected</code> auch in
	 * <code>current</code> sind. Bei <code>hasDrawn == true</code> muss noch genau
	 * eine weitere Karte in <code>current</code> sein. Andernfalls müssen die
	 * Karten identisch sein.
	 */
	private static void assertHandCardsChange(List<Card> expected, List<Card> current, boolean hasDrawn) {
		expected = new ArrayList<>(expected); // copy to ensure passed lists are kept intact
		current = new ArrayList<>(current);
		for (Card c : expected) {
			assertTrue(current.remove(c));
		}
		assertEquals(hasDrawn ? 1 : 0, current.size());
	}

	/**
	 * Return a list of all the hand cards.
	 */
	public static List<Card> handCards(Game game) {
		assertEquals(Mode.PLAYING, game.getMode());
		final var res = new ArrayList<Card>(game.getHandCardsCount());
		for (int i = 0; i < game.getHandCardsCount(); i++) {
			assertEquals(Mode.PLAYING, game.getMode());
			res.add(game.getHandCard(i));
		}
		return res;
	}
}
