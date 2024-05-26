package cardmaster;

import static cardmaster.TestUtils.SHAPE_PREDICATES;
import static cardmaster.TestUtils.containsIgnoreCasePredicate;
import static cardmaster.TestUtils.simplePlay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import cardmaster.Game.Mode;
import cardmaster.cards.Card;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class FactoryTestUtils {

	private static final boolean LOG = true;
	private static final int GAME_CREATION_LIMIT = 1000;
	private static final int ADDITIONAL_ROUNDS_FOR_CREATION = 1000;

	/**
	 * Ein Prädikat für den Beschreibungstext um einen weiteren Ablagestapel
	 * hinzuzufügen. Muss "pile", "stack" oder "stapel" beinhalten. Unterschiede
	 * hinsichtlich Groß- und Kleinschreibung werden ignoriert.
	 */
	public static final Predicate<String> ADDITIONAL_STACK = containsIgnoreCasePredicate("pile")
			.or(containsIgnoreCasePredicate("stack")).or(containsIgnoreCasePredicate("stapel"));
	/**
	 * Ein Prädikat für den Beschreibungstext um die Hand zu vergrößern (eine
	 * weitere Handkarte). Muss "hand" beinhalten. Unterschiede hinsichtlich Groß-
	 * und Kleinschreibung werden ignoriert.
	 */
	public static final Predicate<String> ADDITIONAL_HAND_CARD = containsIgnoreCasePredicate("hand");
	/**
	 * Ein Prädikat für den Beschreibungstext um die Kartenauswahl ab der nächsten
	 * Runde im Shop zu vergrößern. Muss "shop" beinhalten. Unterschiede
	 * hinsichtlich Groß- und Kleinschreibung werden ignoriert.
	 */
	public static final Predicate<String> ADDITIONAL_SHOP = containsIgnoreCasePredicate("shop");

	/**
	 * Erzeugt ein Spiel, in dem noch <code>maxRounds</code> Runden gespielt werden
	 * können und in dem Upgrades und Karten wie angegeben gekauft wurden. Es werden
	 * noch Chance-Karten jeder Form gekauft.
	 * <p>
	 * Dazu werden immer wieder Spiele erzeugt. Nach {@value #GAME_CREATION_LIMIT}
	 * Versuchen wird aufgegeben. Dann liegt höchstwahrscheinlich ein Fehler bei der
	 * Spielerzeugung oder der Verwendung der {@link CardFactory} bzw.
	 * Implementierung von {@link CardFactory#getDefaultFactory()} vor.
	 *
	 * @param maxRounds           Anzahl der verbleibenden Runden
	 * @param additionalStacks    Wie viele Ablagestapel zusätzlich vorhanden sein
	 *                            sollen.
	 * @param additionalHandCards Wie viele zusätzliche Handkarten es geben soll.
	 * @param cards               Welche Karten gekauft werden sollen.
	 */
	public static Game createGame(final int maxRounds, final int additionalStacks, int additionalHandCards,
			Card... cards) {
		final List<Card> requiredCards = new ArrayList<>(cards.length + 1);
		// Add some cards that are easy to play.
		// requiredCards.add(CardFactory.getDefaultFactory().create("Chance", Shape.CIRCLE));
//    requiredCards.add(CardFactory.getDefaultFactory().create("Chance", Shape.STAR));
   requiredCards.add(CardFactory.getDefaultFactory().create("Chance", Shape.SQUARE));
		final var additionalAmount = requiredCards.size();
		requiredCards.addAll(Arrays.asList(cards));
		additionalHandCards = Math.max(additionalHandCards, cards.length + additionalAmount - 4);

		game: for (int i = 0; i < GAME_CREATION_LIMIT; i++) {
			int remainingStacksToAdd = additionalStacks;
			int remainingHandUpgrades = additionalHandCards;
			final List<Card> remainingCardsToAdd = new ArrayList<>(requiredCards);
			int[] factoryRemainingCardsIndex = new int[1];
			// state of factory
			boolean[] factoryUsed = new boolean[2]; // 0 = generally used, 1 = used in current round
			int remainingRounds = ADDITIONAL_ROUNDS_FOR_CREATION; // Add ADDITIONAL_ROUNDS_FOR_CREATION extra rounds for
																	// this method to have enough time to buy everything
																	// requested.
			final var game = new Game(maxRounds + remainingRounds, new CardFactory() {
				// Durch eine eigene Fabrik wird sichergestellt, welche Karten gekauft werden
				// können.
				@Override
				public Card createRandom() {
					factoryUsed[0] = true; // Spürt auf, ob die Fabrik überhaupt verwendet wird.
					if (factoryRemainingCardsIndex[0] >= remainingCardsToAdd.size()) {
						return CardFactory.getDefaultFactory().createRandom();
					}
					if (factoryUsed[1]) {
						// Each round, we buy exactly one card so the card and the shop will contain the
						// same
						// card n times. This guarantees that the same instance is bought and therefore
						// the test
						// does not require equals to be implemented.
						return remainingCardsToAdd.get(factoryRemainingCardsIndex[0] - 1);
					}
					factoryUsed[1] = true;
					return remainingCardsToAdd.get(factoryRemainingCardsIndex[0]++);
				}

				@Override
				public Card create(String name, Shape shape) {
					return CardFactory.getDefaultFactory().create(name, shape);
				}
			});

			while (remainingRounds > 0) { // 100 Extrarunden spielen und dabei alles Erforderliche kaufen.
				if (LOG) {
					System.out.println(
							"remainingRounds = " + remainingRounds + " game.getCredits() = " + game.getCredits());
				}
				--remainingRounds;
				// try to buy required cards and then the upgrades. This guarantees a chance
				// card in first round.
				final var cardsIter = remainingCardsToAdd.iterator();
				buyCard: while (cardsIter.hasNext() && game.getShopItemCount() > 0) {
					final var card = cardsIter.next();
					final Predicate<? super String> predicate = cardShopDescriptionPredicate(card);
					// try to buy cards at index iCard
					final var creditsBefore = game.getCredits();
					for (int iShop = 0; iShop < game.getShopItemCount(); iShop++) {
						if (predicate.test(game.getShopItemDescription(iShop)) && game.buy(iShop)) {
							if (LOG) {
								System.out
										.println("bought " + card + " (" + card.getClass().getSimpleName() + "@"
												+ Integer.toHexString(System.identityHashCode(card)).toUpperCase(
														Locale.ROOT)
												+ ") for " + (creditsBefore - game.getCredits()) + " remaining: "
												+ remainingCardsToAdd);
							}
							cardsIter.remove();
							break buyCard;
						}
					}
				}
				if (!factoryUsed[0]) {
					fail("Game does not use custom card factory. Fix this first. You need to call "
							+ "CardFactory#createRandom()");
				}
				if (game.isDrawPileEmpty()) {
					continue game; // Can not play a round without a card
				}

				if (remainingStacksToAdd > 0 && game.getShopItemCount() > 0) {
					final var creditsBefore = game.getCredits();
					boolean stackCountIncreaseOffered = false;
					for (int iShop = 0; iShop < game.getShopItemCount(); iShop++) {
						if (ADDITIONAL_STACK.test(game.getShopItemDescription(iShop))) {
							stackCountIncreaseOffered = true;
							if (game.buy(iShop)) {
								--remainingStacksToAdd;
								if (LOG) {
									System.out.println(
											"bought additional stack for " + (creditsBefore - game.getCredits()) + " "
													+ remainingStacksToAdd + " remaining.");
								}
								break;
							}
						}
					}
					if (!stackCountIncreaseOffered) {
						fail("Shop did not have an item to add an additional stack. This test requires this to be implemented.");
					}
				}
				if (remainingHandUpgrades > 0 && game.getShopItemCount() > 0) {
					final var creditsBefore = game.getCredits();
					boolean handSizeIncreaseOffered = false;
					for (int iShop = 0; iShop < game.getShopItemCount(); iShop++) {
						if (ADDITIONAL_HAND_CARD.test(game.getShopItemDescription(iShop))) {
							handSizeIncreaseOffered = true;
							if (game.buy(iShop)) {
								--remainingHandUpgrades;
								if (LOG) {
									System.out
											.println("bought additional hand for " + (creditsBefore - game.getCredits())
													+ " " + remainingHandUpgrades + " remaining.");
								}
								break;
							}
						}
					}
					if (!handSizeIncreaseOffered) {
						fail("Shop did not have an item to upgrade hand size. This test requires this to be implemented.");
					}
				}

				game.endShopping();
				factoryUsed[1] = false;
				factoryRemainingCardsIndex[0] = 0;

				while (game.getMode() == Mode.PLAYING) {
					final var card = game.getHandCard(0);
					game.play(card, card.getShape().ordinal());
				}
			}
			if (remainingHandUpgrades == 0 && remainingStacksToAdd == 0 && remainingCardsToAdd.isEmpty()) {
				return game;
			}
			if (LOG) {
				System.out.println("remainingHandUpgrades = " + remainingHandUpgrades);
				System.out.println("remainingStacksToAdd = " + remainingStacksToAdd);
				System.out.println("remainingCardsToAdd = " + remainingCardsToAdd);
			}
		}
		fail("There is some error in game creation. Expected that when generating " + GAME_CREATION_LIMIT
				+ " games, at least one game allowed to buy " + cards.length + " specific card"
				+ (cards.length != 1 ? "s." : "."));
		while (true)
			; // Unreachable

	}

	/**
	 * Ein Prädikat für den Beschreibungstext, den die Karte im Shop hätte. Der Name
	 * und die Form der Karte müssen im Beschreibungstext vorkommen.
	 */
	public static Predicate<? super String> cardShopDescriptionPredicate(Card card) {
		final var name = card.getName().toLowerCase(Locale.ROOT);
		return SHAPE_PREDICATES.get(card.getShape()).and(d -> d.toLowerCase(Locale.ROOT).contains(name));
	}

	/**
	 * Hilfsfunktion, die so lange spielt, bis sich die Punktzahl um eine bestimme
	 * Größe geändert hat.
	 */
	public static void getMoney(Game game, double increase) {
		assert game.getMode() == Mode.SHOPPING;
		while (increase > 0) {
			game.endShopping();
			increase += game.getCredits();
			simplePlay(game);
			assertEquals("Game endet earlier than expected. Failed to complete test. Does the "
					+ "credit increase when cards are played?", Mode.SHOPPING, game.getMode());
			increase -= game.getCredits();
		}
	}

	public static void assertHandCardDecreased(int handCards, Game game) {
		assertEquals("The card that the factory returned is passed to play. A card of same class and "
				+ "shape is guaranteed to be in the hand and no cards is left on draw pile. Therefore "
				+ "the amount of cards in hand must decrease.", handCards, game.getHandCardsCount());
	}
}
