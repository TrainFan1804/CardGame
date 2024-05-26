package cardmaster;

import static cardmaster.FactoryTestUtils.ADDITIONAL_HAND_CARD;
import static cardmaster.FactoryTestUtils.ADDITIONAL_SHOP;
import static cardmaster.FactoryTestUtils.ADDITIONAL_STACK;
import static cardmaster.FactoryTestUtils.getMoney;
import static cardmaster.TestUtils.ANY_CARD;
import static cardmaster.TestUtils.buyCard;
import static cardmaster.TestUtils.createGameWithChanceCard;
import static cardmaster.TestUtils.simplePlay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import cardmaster.Game.Mode;
import java.util.function.Predicate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestShopUpgrades {

	@Rule
	public Timeout globalTimeout = Timeout.millis(100);

	@Test
	public void upgradesAreAvailable() {
		final var game = createGameWithChanceCard(10, true);
		while (game.getMode() != Mode.END) {
			assertShopHasAdditionalStack(game, true);
			assertShopHasAdditionalHandCard(game, true);
			assertShopHasShopUpgrade(game, true);
			game.endShopping();
			simplePlay(game);
		}
	}

	@Test
	public void onlyOnyUpgradePerTypeAndRound() {
		final var game = createGameWithChanceCard(10, true);
		assertShopHasAdditionalStack(game, true);
		assertShopHasAdditionalHandCard(game, true);
		assertShopHasShopUpgrade(game, true);
		buyCard(game, "additional stack card upgrade", ADDITIONAL_STACK); // Kauft einen zusätzlichen Ablagestapel
		buyCard(game, "additional hand card upgrade", ADDITIONAL_HAND_CARD); // Kauft eine zusätzliche Handkarte
		buyCard(game, "additional shop item upgrade", ADDITIONAL_SHOP); // Kauft eine zusätzliche Karte im Shop
		// Können nur ein mal pro Runde gekauft werden.
		assertShopHasAdditionalStack(game, false);
		assertShopHasAdditionalHandCard(game, false);
		assertShopHasShopUpgrade(game, false);
		game.endShopping();
		simplePlay(game);
		// Nun sind alle Upgrades wieder verfügbar.
		assertShopHasAdditionalStack(game, true);
		assertShopHasAdditionalHandCard(game, true);
		assertShopHasShopUpgrade(game, true);
	}

	@Test
	public void priceIncreasesPerType() {
		final var game = createGameWithChanceCard(1000, true);
		assertShopHasAdditionalStack(game, true);
		assertShopHasAdditionalHandCard(game, true);
		assertShopHasShopUpgrade(game, true);
		assertEquals(1, getPrice(game, ADDITIONAL_STACK));
		assertEquals(1, getPrice(game, ADDITIONAL_HAND_CARD));
		assertEquals(1, getPrice(game, ADDITIONAL_SHOP));
		buyCard(game, "additional hand card upgrade", ADDITIONAL_HAND_CARD);
		buyCard(game, "additional shop item upgrade", ADDITIONAL_SHOP);
		assertShopHasAdditionalStack(game, true);
		assertShopHasAdditionalHandCard(game, false);
		assertShopHasShopUpgrade(game, false);

		game.endShopping();
		simplePlay(game);

		assertShopHasAdditionalStack(game, true);
		assertShopHasAdditionalHandCard(game, true);
		assertShopHasShopUpgrade(game, true);
		assertEquals(1, getPrice(game, ADDITIONAL_STACK));
		assertEquals(2, getPrice(game, ADDITIONAL_HAND_CARD));
		assertEquals(2, getPrice(game, ADDITIONAL_SHOP));
		buyCard(game, "additional stack card upgrade", ADDITIONAL_STACK);
		buyCard(game, "additional hand card upgrade", ADDITIONAL_HAND_CARD);
		assertShopHasAdditionalStack(game, false);
		assertShopHasAdditionalHandCard(game, false);
		assertShopHasShopUpgrade(game, true);

		getMoney(game, 9);

		assertShopHasAdditionalStack(game, true);
		assertShopHasAdditionalHandCard(game, true);
		assertShopHasShopUpgrade(game, true);
		assertEquals(2, getPrice(game, ADDITIONAL_STACK));
		assertEquals(5, getPrice(game, ADDITIONAL_HAND_CARD));
		assertEquals(2, getPrice(game, ADDITIONAL_SHOP));
		buyCard(game, "additional stack card upgrade", ADDITIONAL_STACK);
		buyCard(game, "additional hand card upgrade", ADDITIONAL_HAND_CARD);
		buyCard(game, "additional shop item upgrade", ADDITIONAL_SHOP);
		assertShopHasAdditionalStack(game, false);
		assertShopHasAdditionalHandCard(game, false);
		assertShopHasShopUpgrade(game, false);

		getMoney(game, 20);

		assertShopHasAdditionalStack(game, true);
		assertShopHasAdditionalHandCard(game, true);
		assertShopHasShopUpgrade(game, true);
		assertEquals(5, getPrice(game, ADDITIONAL_STACK));
		assertEquals(10, getPrice(game, ADDITIONAL_HAND_CARD));
		assertEquals(5, getPrice(game, ADDITIONAL_SHOP));
		buyCard(game, "additional stack card upgrade", ADDITIONAL_STACK);
		buyCard(game, "additional hand card upgrade", ADDITIONAL_HAND_CARD);
		buyCard(game, "additional shop item upgrade", ADDITIONAL_SHOP);
		assertShopHasAdditionalStack(game, false);
		assertShopHasAdditionalHandCard(game, false);
		assertShopHasShopUpgrade(game, false);
	}

	@Test
	public void additionalStackIsAdded() {
		final var game = createGameWithChanceCard(1000, true);

		game.endShopping();
		assertEquals(3, game.getStacksCount());
		simplePlay(game);

		assertShopHasAdditionalStack(game, true);
		buyCard(game, "additional stack card upgrade", ADDITIONAL_STACK);
		assertShopHasAdditionalStack(game, false);

		game.endShopping();
		assertEquals(4, game.getStacksCount());
		simplePlay(game);

		assertShopHasAdditionalStack(game, true);
		buyCard(game, "additional stack card upgrade", ADDITIONAL_STACK);
		assertShopHasAdditionalStack(game, false);

		game.endShopping();
		assertEquals(5, game.getStacksCount());
		simplePlay(game);

		game.endShopping();
		assertEquals(5, game.getStacksCount());
		simplePlay(game);

		game.endShopping();
		assertEquals(5, game.getStacksCount());
	}

	@Test
	public void additionalHandCardIsAdded() {
		final var game = createGameWithChanceCard(1000, true);

		var remaining = 5;
		while (remaining > 0) {
			game.endShopping();
			simplePlay(game);
			for (int i = 0; i < game.getShopItemCount(); i++) {
				if (ANY_CARD.test(game.getShopItemDescription(i)) && game.buy(i)) {
					--remaining;
					break;
				}
			}
		}

		getMoney(game, 1);
		game.endShopping();
		assertEquals(4, game.getHandCardsCount());
		simplePlay(game);

		assertShopHasAdditionalHandCard(game, true);
		buyCard(game, "additional hand card upgrade", ADDITIONAL_HAND_CARD);
		assertShopHasAdditionalHandCard(game, false);

		game.endShopping();
		assertEquals(5, game.getHandCardsCount());
		simplePlay(game);

		assertShopHasAdditionalHandCard(game, true);
		buyCard(game, "additional hand card upgrade", ADDITIONAL_HAND_CARD);
		assertShopHasAdditionalHandCard(game, false);

		game.endShopping();
		assertEquals(6, game.getHandCardsCount());
		simplePlay(game);

		game.endShopping();
		assertEquals(6, game.getHandCardsCount());
		simplePlay(game);

		game.endShopping();
		assertEquals(6, game.getHandCardsCount());
	}

	@Test
	public void additionalShopItemIsAddedAfterNextRound() {
		final var game = createGameWithChanceCard(1000, true);

		game.endShopping();
		simplePlay(game);

		assertEquals(8, game.getShopItemCount());
		assertShopHasShopUpgrade(game, true);
		buyCard(game, "additional shop item upgrade", ADDITIONAL_SHOP);
		assertShopHasShopUpgrade(game, false);
		assertEquals(7, game.getShopItemCount());

		game.endShopping();
		simplePlay(game);

		assertEquals(9, game.getShopItemCount());
		assertShopHasShopUpgrade(game, true);
		buyCard(game, "additional shop item upgrade", ADDITIONAL_SHOP);
		assertShopHasShopUpgrade(game, false);
		assertEquals(8, game.getShopItemCount());

		game.endShopping();
		simplePlay(game);
		assertEquals(10, game.getShopItemCount());

		game.endShopping();
		simplePlay(game);
		assertEquals(10, game.getShopItemCount());

		game.endShopping();
		simplePlay(game);
		assertEquals(10, game.getShopItemCount());
	}

	/**
	 * Stellt sicher, dass der Shop ein Upgrade für einen zusätzlichen Ablagestapel
	 * hat (<code>present
	 * == true</code>) oder nicht (<code>present == false</code>).
	 * <p>
	 * Eine Beschreibung muss {@link FactoryTestUtils#ADDITIONAL_STACK} erfüllen
	 * bzw. keine Beschreibung darf.
	 */
	public void assertShopHasAdditionalStack(Game game, boolean present) {
		if (present) {
			assertShopHas(game, "the stack upgrade option.", ADDITIONAL_STACK);
		} else {
			assertShopHasNot(game, "the stack upgrade option.", ADDITIONAL_STACK);
		}
	}

	/**
	 * Stellt sicher, dass der Shop ein Upgrade für eine zusätzliche Handkarte hat
	 * (<code>present ==
	 * true</code>) oder nicht (<code>present == false</code>).
	 * <p>
	 * Eine Beschreibung muss {@link FactoryTestUtils#ADDITIONAL_HAND_CARD} erfüllen
	 * bzw. keine Beschreibung darf.
	 */
	public void assertShopHasAdditionalHandCard(Game game, boolean present) {
		if (present) {
			assertShopHas(game, "the additional hand card upgrade option.", ADDITIONAL_HAND_CARD);
		} else {
			assertShopHasNot(game, "the additional hand card upgrade option.", ADDITIONAL_HAND_CARD);
		}
	}

	/**
	 * Stellt sicher, dass der Shop ein Upgrade für eine zusätzliche Karte im Shop
	 * hat (<code>present
	 * == true</code>) oder nicht (<code>present == false</code>).
	 * <p>
	 * Eine Beschreibung muss {@link FactoryTestUtils#ADDITIONAL_SHOP} erfüllen bzw.
	 * keine Beschreibung darf.
	 */
	public void assertShopHasShopUpgrade(Game game, boolean present) {
		if (present) {
			assertShopHas(game, "the increase shop upgrade option.", ADDITIONAL_SHOP);
		} else {
			assertShopHasNot(game, "the increase shop upgrade option.", ADDITIONAL_SHOP);
		}
	}

	/**
	 * Stellt sicher, dass es im Shop einen Gegenstand gibt, auf dessen Beschreibung
	 * das Prädikat zutrifft.
	 */
	public void assertShopHas(Game game, String msg, Predicate<String> additionalHandCard) {
		for (int i = 0; i < game.getShopItemCount(); i++) {
			if (additionalHandCard.test(game.getShopItemDescription(i))) {
				return;
			}
		}
		fail("Shop did not contain " + msg);
	}

	/**
	 * Stellt sicher, dass es im Shop keinen Gegenstand gibt, auf dessen
	 * Beschreibung das Prädikat zutrifft.
	 */
	public void assertShopHasNot(Game game, String msg, Predicate<String> additionalHandCard) {
		for (int i = 0; i < game.getShopItemCount(); i++) {
			if (additionalHandCard.test(game.getShopItemDescription(i))) {
				fail("Shop did contain " + msg);
			}
		}
	}

	/**
	 * Bestimmt den Preis des ersten Gegenstands, auf den dessen Beschreibung das
	 * Prädikat zutrifft.
	 */
	public int getPrice(Game game, Predicate<String> additionalHandCard) {
		for (int i = 0; i < game.getShopItemCount(); i++) {
			if (additionalHandCard.test(game.getShopItemDescription(i))) {
				return game.getShopItemPrice(i);
			}
		}
		fail();
		return 0;
	}
}
