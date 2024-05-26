package cardmaster;

import static cardmaster.FactoryTestUtils.createGame;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestPaarCard {

	@Rule
	public Timeout globalTimeout = Timeout.millis(10000);

	@Test
	public void noPointsForSingleCard() {
		final var factory = CardFactory.getDefaultFactory();
		final var circlePaar = factory.create("Paar", Shape.CIRCLE);
		final var starPaar = factory.create("Paar", Shape.STAR);
		final var squarePaar = factory.create("Paar", Shape.SQUARE);
		final var game = createGame(1, 0, 0, circlePaar, starPaar, squarePaar);
		game.endShopping();

		final var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circlePaar, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starPaar, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(squarePaar, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);
	}

	@Test
	public void pointsForSinglePaar() {
		final var factory = CardFactory.getDefaultFactory();
		final var circlePaar0 = factory.create("Paar", Shape.CIRCLE);
		final var circlePaar1 = factory.create("Paar", Shape.CIRCLE);
		final var game = createGame(1, 0, 0, circlePaar0, circlePaar1);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circlePaar0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circlePaar1, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 2, game.getCredits(), 1E-12);
	}

	@Test
	public void pointsForSinglePaarOnlyForSameShape() {
		final var factory = CardFactory.getDefaultFactory();
		final var circlePaar0 = factory.create("Paar", Shape.CIRCLE);
		final var circlePaar1 = factory.create("Paar", Shape.CIRCLE);
		final var starPaar0 = factory.create("Paar", Shape.STAR);
		final var starPaar1 = factory.create("Paar", Shape.STAR);
		final var game = createGame(1, 1, 0, circlePaar0, circlePaar1, starPaar0, starPaar1);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circlePaar0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starPaar0, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starPaar1, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 2, game.getCredits(), 1E-12);

		game.play(circlePaar1, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 2, game.getCredits(), 1E-12);
	}

	@Test
	public void pointsForMultiplePaar() {
		final var factory = CardFactory.getDefaultFactory();
		final var circlePaar0 = factory.create("Paar", Shape.CIRCLE);
		final var circlePaar1 = factory.create("Paar", Shape.CIRCLE);
		final var circlePaar2 = factory.create("Paar", Shape.CIRCLE);
		final var circlePaar3 = factory.create("Paar", Shape.CIRCLE);
		final var circlePaar4 = factory.create("Paar", Shape.CIRCLE);
		final var circlePaar5 = factory.create("Paar", Shape.CIRCLE);
		final var game = createGame(1, 3, 0, circlePaar0, circlePaar1, circlePaar2, circlePaar3, circlePaar4,
				circlePaar5);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circlePaar0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circlePaar1, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 2, game.getCredits(), 1E-12);

		game.play(circlePaar2, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 2, game.getCredits(), 1E-12);

		game.play(circlePaar3, 5);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 4, game.getCredits(), 1E-12);

		game.play(circlePaar4, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 4, game.getCredits(), 1E-12);

		game.play(circlePaar5, 4);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 6, game.getCredits(), 1E-12);
	}

	@Test
	public void onlyShapeMatters() {
		final var factory = CardFactory.getDefaultFactory();
		final var circlePaar = factory.create("Paar", Shape.CIRCLE);
		final var circleChance = factory.create("Chance", Shape.CIRCLE);
		final var game = createGame(1, 0, 0, circlePaar, circleChance);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleChance, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 1, game.getCredits(), 1E-12);

		game.play(circlePaar, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 2, game.getCredits(), 1E-12);
	}
}
