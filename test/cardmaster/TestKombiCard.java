package cardmaster;

import static cardmaster.FactoryTestUtils.createGame;
import static cardmaster.TestUtils.ThrowsExpected.thr;
import static cardmaster.TestUtils.assertThrows;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestKombiCard {

	@Rule
	public Timeout globalTimeout = Timeout.millis(10000);

	@Test
	public void factoryCreatesCardWithCorrectShape() {
		final var factory = CardFactory.getDefaultFactory();
		for (int i = 0; i < 100; i++) {
			for (Shape shape : Shape.values()) {
				assertEquals(shape, factory.create("Kombi", shape).getShape());
			}
		}
	}

	@Test
	public void bothPartsGenerateCredits() {
		final var factory = CardFactory.getDefaultFactory();
		final var circlePaar0 = factory.create("Paar", Shape.CIRCLE);
		final var circleChance0 = factory.create("Chance", Shape.CIRCLE);
		final var circleKombi0 = factory.combine(circleChance0, circlePaar0);
		final var circlePaar1 = factory.create("Paar", Shape.CIRCLE);
		final var circleChance1 = factory.create("Chance", Shape.CIRCLE);
		final var circleKombi1 = factory.combine(circlePaar1, circleChance1);
		final var game = createGame(1, 0, 0, circleKombi0, circleKombi1);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleKombi0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 0.5 + 0.5, // Chance 1 shape + 1 card
				game.getCredits(), 1E-12);

		game.play(circleKombi1, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 0.5 + 1.0 // Chance 1 shape + 2 cards
				+ 2.0, // Paar
				game.getCredits(), 1E-12);
	}

	@Test
	public void bothPartsGenerateCreditsNestedKombi() {
		final var factory = CardFactory.getDefaultFactory();
		final var circleTripel0 = factory.create("Tripel", Shape.CIRCLE);
		final var circlePaar0 = factory.create("Paar", Shape.CIRCLE);
		final var circleChance0 = factory.create("Chance", Shape.CIRCLE);
		final var circleKombi0 = factory.combine(factory.combine(circleTripel0, circlePaar0), circleChance0);
		final var circleTripel1 = factory.create("Tripel", Shape.CIRCLE);
		final var circlePaar1 = factory.create("Paar", Shape.CIRCLE);
		final var circleChance1 = factory.create("Chance", Shape.CIRCLE);
		final var circleKombi1 = factory.combine(factory.combine(circleTripel1, circlePaar1), circleChance1);
		final var circleTripel2 = factory.create("Tripel", Shape.CIRCLE);
		final var circlePaar2 = factory.create("Paar", Shape.CIRCLE);
		final var circleChance2 = factory.create("Chance", Shape.CIRCLE);
		final var circleKombi2 = factory.combine(factory.combine(circleTripel2, circlePaar2), circleChance2);
		final var circleTripel3 = factory.create("Tripel", Shape.CIRCLE);
		final var circlePaar3 = factory.create("Paar", Shape.CIRCLE);
		final var circleChance3 = factory.create("Chance", Shape.CIRCLE);
		final var circleKombi3 = factory.combine(factory.combine(circleTripel3, circlePaar3), circleChance3);
		final var game = createGame(1, 1, 0, circleKombi0, circleKombi1, circleKombi2, circleKombi3);
		game.endShopping();

		var credits = game.getCredits();
		var handCards = game.getHandCardsCount();
		game.play(circleKombi0, 0);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 0.5 + 0.5, // Chance 1 shape + 1 card
				game.getCredits(), 1E-12);

		game.play(circleKombi1, 1);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 0.5 + 1.0 // Chance 1 shape + 2 cards
				+ 2.0, // Paar
				game.getCredits(), 1E-12);

		game.play(circleKombi2, 2);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 0.5 + 1.5 // Chance 1 shape + 3 cards
				+ 2.0 // Paar
				+ 5.0, // Tripel
				game.getCredits(), 1E-12);

		game.play(circleKombi3, 3);
		FactoryTestUtils.assertHandCardDecreased(--handCards, game);
		assertEquals(credits += 0.5 + 2.0 // Chance 1 shape + 4 cards
				+ 4.0 // Paar 2 times
				+ 5.0, // Tripel
				game.getCredits(), 1E-12);
	}

	@Test
	public void shapeWhenOnTopIsReportedCorrectly() {
		final var factory = CardFactory.getDefaultFactory();
		final var starPaar = factory.create("Paar", Shape.STAR);
		final var starChance = factory.create("Chance", Shape.STAR);
		final var starKombi = factory.combine(starChance, starPaar);
		final var squarePaar = factory.create("Paar", Shape.SQUARE);
		final var squareChance = factory.create("Chance", Shape.SQUARE);
		final var squareKombi = factory.combine(squarePaar, squareChance);
		final var circlePaar = factory.create("Paar", Shape.CIRCLE);
		final var circleChance = factory.create("Chance", Shape.CIRCLE);
		final var circleKombi = factory.combine(circlePaar, circleChance);
		final var game = createGame(1, 2, 0, starKombi, squareKombi, circleKombi);
		game.endShopping();

		game.play(starKombi, 4);
		assertArrayEquals(new Shape[] { null, null, null, null, Shape.STAR }, game.getTopShapes());

		game.play(circleKombi, 1);
		assertArrayEquals(new Shape[] { null, Shape.CIRCLE, null, null, Shape.STAR }, game.getTopShapes());
	}

	@Test
	public void combineAssertsNotNull() {
		final var factory = CardFactory.getDefaultFactory();
		final var card = factory.createRandom();
		assertThrows(false, NullPointerException.class, () -> factory.combine(null, card));
		assertThrows(false, NullPointerException.class, () -> factory.combine(card, null));
		assertThrows(false, NullPointerException.class, () -> factory.combine(null, null));
	}

	@Test
	public void combineAssertsShape() {
		final var factory = CardFactory.getDefaultFactory();
		for (Shape shape0 : Shape.values()) {
			for (Shape shape1 : Shape.values()) {
				if (shape0 != shape1) {
					final var card0 = factory.create("Paar", shape0);
					final var card1 = factory.create("Paar", shape1);
					assertThrows(() -> factory.combine(card0, card1), thr(IllegalArgumentException.class, e -> {
						final var message = e.getMessage();
						assertTrue(message.contains(shape0.name()));
						assertTrue(message.contains(shape1.name()));
						assertTrue(message.contains(card0.toString()));
						assertTrue(message.contains(card1.toString()));
					}));
				}
			}
		}
	}

	@Test
	public void combineKeepsShape() {
		final var factory = CardFactory.getDefaultFactory();
		for (int i = 0; i < 100; i++) {
			for (Shape shape : Shape.values()) {
				assertEquals(shape,
						factory.combine(factory.create("Paar", shape), factory.create("Tripel", shape)).getShape());
			}
		}
	}
}
