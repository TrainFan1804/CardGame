package cardmaster;

import static cardmaster.FactoryTestUtils.createGame;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestTripelCard {

	@Rule
	public Timeout globalTimeout = Timeout.millis(10000);

	@Test
	public void noPointsForSingleCard() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleTripel0 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel1 = factory.create("Tripel", Shape.CIRCLE);
		final var starTripel0 = factory.create("Tripel", Shape.STAR);
		final var starTripel1 = factory.create("Tripel", Shape.STAR);
		final var squareTripel0 = factory.create("Tripel", Shape.SQUARE);
		final var squareTripel1 = factory.create("Tripel", Shape.SQUARE);
		final var game = createGame(1, 0, 0, circleTripel0, circleTripel1, starTripel0, starTripel1, squareTripel0,
				squareTripel1);
		game.endShopping();

		final var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleTripel0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleTripel1, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starTripel0, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(squareTripel0, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starTripel1, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(squareTripel1, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);
	}

	@Test
	public void pointsForSingleTripel() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleTripel0 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel1 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel2 = factory.create("Tripel", Shape.CIRCLE);
		final var game = createGame(1, 0, 0, circleTripel0, circleTripel1, circleTripel2);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleTripel0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleTripel1, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleTripel2, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 5, game.getCredits(), 1E-12);
	}

	@Test
	public void pointsForSingleTripelOnlyForSameShape() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleTripel0 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel1 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel2 = factory.create("Tripel", Shape.CIRCLE);
		final var starTripel0 = factory.create("Tripel", Shape.STAR);
		final var starTripel1 = factory.create("Tripel", Shape.STAR);
		final var starTripel2 = factory.create("Tripel", Shape.STAR);
		final var game = createGame(1, 3, 0, circleTripel0, circleTripel1, circleTripel2, starTripel0, starTripel1,
				starTripel2);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleTripel0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starTripel0, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(starTripel1, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleTripel1, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleTripel2, 4);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 5, game.getCredits(), 1E-12);

		game.play(starTripel2, 5);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 5, game.getCredits(), 1E-12);
	}

	@Test
	public void pointsForMultipleTripel() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleTripel0 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel1 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel2 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel3 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel4 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel5 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel6 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel7 = factory.create("Tripel", Shape.CIRCLE);
		final var circleTripel8 = factory.create("Tripel", Shape.CIRCLE);
		final var game = createGame(1, 6, 0, circleTripel0, circleTripel1, circleTripel2, circleTripel3, circleTripel4,
				circleTripel5, circleTripel6, circleTripel7, circleTripel8);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleTripel0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleTripel1, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits, game.getCredits(), 1E-12);

		game.play(circleTripel2, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 5, game.getCredits(), 1E-12);

		game.play(circleTripel3, 6);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 5, game.getCredits(), 1E-12);

		game.play(circleTripel4, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 5, game.getCredits(), 1E-12);

		game.play(circleTripel5, 4);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);

		game.play(circleTripel6, 5);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);

		game.play(circleTripel7, 8);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 10, game.getCredits(), 1E-12);

		game.play(circleTripel8, 7);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 15, game.getCredits(), 1E-12);
	}

	@Test
	public void onlyShapeMatters() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleTripel = factory.create("Tripel", Shape.CIRCLE);
		final var circleChance = factory.create("Chance", Shape.CIRCLE);
		final var circlePaar = factory.create("Paar", Shape.CIRCLE);
		final var game = createGame(1, 0, 0, circleTripel, circleChance, circlePaar);
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
	}
}
