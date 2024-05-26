package cardmaster;

import static cardmaster.FactoryTestUtils.createGame;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestQuadrupelCard {

	@Rule
	public Timeout globalTimeout = Timeout.millis(10000);

	@Test
	public void noPointsForSingleCard() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleQuadrupel0 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel1 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel2 = factory.create("Quadrupel", Shape.CIRCLE);
		final var starQuadrupel0 = factory.create("Quadrupel", Shape.STAR);
		final var starQuadrupel1 = factory.create("Quadrupel", Shape.STAR);
		final var starQuadrupel2 = factory.create("Quadrupel", Shape.STAR);
		final var squareQuadrupel0 = factory.create("Quadrupel", Shape.SQUARE);
		final var squareQuadrupel1 = factory.create("Quadrupel", Shape.SQUARE);
		final var squareQuadrupel2 = factory.create("Quadrupel", Shape.SQUARE);
		final var game = createGame(1, 2, 0, circleQuadrupel0, circleQuadrupel1, circleQuadrupel2, starQuadrupel0,
				starQuadrupel1, starQuadrupel2, squareQuadrupel0, squareQuadrupel1, squareQuadrupel2);
		game.endShopping();

		final var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleQuadrupel0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel1, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel2, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starQuadrupel0, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(squareQuadrupel0, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starQuadrupel1, 4);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(squareQuadrupel1, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starQuadrupel2, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(squareQuadrupel2, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);
	}

	@Test
	public void pointsForSingleQuadrupel() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleQuadrupel0 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel1 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel2 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel3 = factory.create("Quadrupel", Shape.CIRCLE);
		final var game = createGame(1, 1, 0, circleQuadrupel0, circleQuadrupel1, circleQuadrupel2, circleQuadrupel3);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleQuadrupel0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel1, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel2, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel3, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);
	}

	@Test
	public void noPointsIfOtherShape() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleQuadrupel0 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel1 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel2 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel3 = factory.create("Quadrupel", Shape.CIRCLE);
		final var starQuadrupel = factory.create("Quadrupel", Shape.STAR);
		final var game = createGame(1, 2, 0, circleQuadrupel0, circleQuadrupel1, circleQuadrupel2, circleQuadrupel3,
				starQuadrupel);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleQuadrupel0, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel1, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel2, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel3, 4);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);

		game.play(starQuadrupel, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);
	}

	@Test
	public void pointsForSingleQuadrupelOnlyForSameShape() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleQuadrupel0 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel1 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel2 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel3 = factory.create("Quadrupel", Shape.CIRCLE);
		final var starQuadrupel0 = factory.create("Quadrupel", Shape.STAR);
		final var starQuadrupel1 = factory.create("Quadrupel", Shape.STAR);
		final var starQuadrupel2 = factory.create("Quadrupel", Shape.STAR);
		final var starQuadrupel3 = factory.create("Quadrupel", Shape.STAR);
		final var game = createGame(1, 5, 0, circleQuadrupel0, circleQuadrupel1, circleQuadrupel2, circleQuadrupel3,
				starQuadrupel0, starQuadrupel1, starQuadrupel2, starQuadrupel3);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleQuadrupel0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starQuadrupel0, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starQuadrupel1, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel1, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel2, 4);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel3, 5);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);

		game.play(starQuadrupel2, 6);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starQuadrupel3, 7);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);
	}

	@Test
	public void pointsForMultipleQuadrupel() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleQuadrupel0 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel1 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel2 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel3 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel4 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel5 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel6 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel7 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel8 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel9 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel10 = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleQuadrupel11 = factory.create("Quadrupel", Shape.CIRCLE);
		final var game = createGame(1, 9, 0, circleQuadrupel0, circleQuadrupel1, circleQuadrupel2, circleQuadrupel3,
				circleQuadrupel4, circleQuadrupel5, circleQuadrupel6, circleQuadrupel7, circleQuadrupel8,
				circleQuadrupel9, circleQuadrupel10, circleQuadrupel11);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleQuadrupel0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel1, 9);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel2, 7);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleQuadrupel3, 6);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);

		game.play(circleQuadrupel4, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);

		game.play(circleQuadrupel5, 8);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);

		game.play(circleQuadrupel6, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);

		game.play(circleQuadrupel7, 5);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 20, game.getCredits(), 1E-12);

		game.play(circleQuadrupel8, 4);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 20, game.getCredits(), 1E-12);

		game.play(circleQuadrupel9, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 20, game.getCredits(), 1E-12);

		game.play(circleQuadrupel10, 10);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 20, game.getCredits(), 1E-12);

		game.play(circleQuadrupel11, 11);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 30, game.getCredits(), 1E-12);
	}

	@Test
	public void onlyShapeMatters() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleQuadrupel = factory.create("Quadrupel", Shape.CIRCLE);
		final var circleChance = factory.create("Chance", Shape.CIRCLE);
		final var circlePaar = factory.create("Paar", Shape.CIRCLE);
		final var circleTripel = factory.create("Tripel", Shape.CIRCLE);
		final var game = createGame(1, 1, 0, circleQuadrupel, circleChance, circlePaar, circleTripel);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circlePaar, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleChance, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 1.5, game.getCredits(), 1E-12);

		game.play(circleTripel, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 5, game.getCredits(), 1E-12);

		game.play(circleQuadrupel, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);
	}
}
