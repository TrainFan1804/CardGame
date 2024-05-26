package cardmaster;

import static cardmaster.TestUtils.ANY_CARD;
import static cardmaster.TestUtils.CHANCE;
import static cardmaster.TestUtils.createGameWithChanceCard;
import static cardmaster.TestUtils.simplePlay;
import static org.junit.Assert.assertTrue;

import cardmaster.Game.Mode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestSeries1GameConstructor {

	@Rule
	public Timeout globalTimeout = Timeout.millis(100);

	@Test
	public void onlyChanceCardsIfNoFactoryWasSpecified() {
		final var game = createGameWithChanceCard(10, true);
		while (game.getMode() != Mode.END) {
			for (int iShop = 0; iShop < game.getShopItemCount(); iShop++) {
				final var description = game.getShopItemDescription(iShop);
				if (ANY_CARD.test(description)) {
					assertTrue("Whan using Game(int), only chance cards can be offered by shop but " + "there was: "
							+ description, CHANCE.test(description));
				}
			}
			game.endShopping();
			simplePlay(game);
		}
	}
}
