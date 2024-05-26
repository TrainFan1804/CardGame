package cardmaster;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cardmaster.Game.Mode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TestUtils {

	private static final int GAME_CREATION_LIMIT = 1000;

	/**
	 * Enthält zu jeder Form ein Prädikat für den Beschreibungstext. Mit
	 * <code>get(Shape)</code> wird das entsprechende Prädikat abgerufen. Es
	 * ignoriert Groß- und Kleinschreibung und erzwingt, dass die deutsche oder die
	 * englische Bezeichnung in der Beschreibung vorkommen muss oder aber ein
	 * Zeichen für Kreis (●), Stern (★) bzw Quadrat (■).
	 */
	public static final Map<Shape, Predicate<String>> SHAPE_PREDICATES = Map.of(
			// Für CIRCLE entweder "kreis" oder "circle" oder "●"
			Shape.CIRCLE,
			containsIgnoreCasePredicate("kreis").or(containsIgnoreCasePredicate("circle"))
					.or(containsIgnoreCasePredicate("●")),
			Shape.STAR,
			containsIgnoreCasePredicate("stern").or(containsIgnoreCasePredicate("star"))
					.or(containsIgnoreCasePredicate("★")),
			Shape.SQUARE, containsIgnoreCasePredicate("quadrat").or(containsIgnoreCasePredicate("square"))
					.or(containsIgnoreCasePredicate("■")));
	/**
	 * Ein Prädikat für den Beschreibungstext einer Karte. Die Form der Karte muss
	 * in der Beschreibung vorkommen.
	 */
	public static final Predicate<String> ANY_CARD = SHAPE_PREDICATES.values().stream().reduce(i -> false,
			Predicate::or); // Verodert alle Prädikate für die Formen
	/**
	 * Ein Prädikat für den Beschreibungstext einer Chance-Karte. Der Name (Chance)
	 * muss vorkommen. Die Groß- und Kleinschreibung ist dabei egal.
	 */
	public static final Predicate<String> CHANCE = containsIgnoreCasePredicate("Chance");
	/**
	 * Prädikat, das immer wahr ist.
	 */
	public static final Predicate<String> ANY = i -> true;

	/**
	 * Erzeugt ein Prädikat für eine Zeichenfolge, dass genau dann wahr ist, wenn
	 * der String <code>s</code> in dem Argument des Prädikates enthalten ist, egal
	 * hinsichtlich der Groß- und Kleinschreibung.
	 */
	public static Predicate<String> containsIgnoreCasePredicate(String s) {
		final var finalS = s.toLowerCase(Locale.ROOT);
		// Lambda-Ausdruck für eine Funktion von String zu bool. x ist das Argument des
		// Prädikats.
		return x -> x.toLowerCase(Locale.ROOT).contains(finalS);
	}

	/**
	 * Erzeugt ein Spiel mit <code>maxRounds</code> Runden, in dem mindestens eine
	 * Chance-Karte in der ersten Runde gekauft werden kann.
	 * <p>
	 * Dazu werden immer wieder Spiele erzeugt. Nach {@value #GAME_CREATION_LIMIT}
	 * Versuchen wird aufgegeben. Dann liegt höchstwahrscheinlich ein Fehler bei der
	 * Spielerzeugung vor.
	 *
	 * @param maxRounds Anzahl der Runden.
	 * @param buy       ob die Chance-Karte gleich gekauft werden soll.
	 */
	public static Game createGameWithChanceCard(int maxRounds, boolean buy) {
		return createGameWithCardsMatching("Contains a Chance card.", maxRounds, buy, null, CHANCE);
	}

	/**
	 * Erzeugt ein Spiel mit <code>maxRounds</code> Runden, in dem in der ersten
	 * Runde alle Karten gekauft werden können.
	 * <p>
	 * Dazu werden immer wieder Spiele erzeugt. Nach {@value #GAME_CREATION_LIMIT}
	 * Versuchen wird aufgegeben. Dann liegt höchstwahrscheinlich ein Fehler bei der
	 * Spielerzeugung vor.
	 *
	 * @param maxRounds Anzahl der Runden.
	 */
	public static Game createGameWhereAllCardsCanBeBought(int maxRounds) {
		return createGameMatching("has a shop where all cards can be bought.", maxRounds,
				TestUtils::allCardsCanBeBought);
		// Für Interessierte: Hier wird eine Methodenreferenz verwendet. Es wird ein
		// Predicate<String>
		// benötigt, was einer Funktion von String nach boolean entspricht.
		// TestUtils#allCardsCanBeBought ist genau so eine Funktion. Daher kann eine
		// Methodenreferenz
		// auf allCardsCanBeBought als Predicate<String> verwendet werden. Mehr
		// Informationen unter:
		// https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
	}

	/**
	 * Erzeugt ein Spiel mit <code>maxRounds</code> Runden, in dem in der ersten
	 * Runde alles gekauft werden kann. Ab Serie 2 gibt es mehr als Karten zu
	 * kaufen. Für Serie 1 ist es aber gleichbedeutend mit allen Karten.
	 * <p>
	 * Dazu werden immer wieder Spiele erzeugt. Nach {@value #GAME_CREATION_LIMIT}
	 * Versuchen wird aufgegeben. Dann liegt höchstwahrscheinlich ein Fehler bei der
	 * Spielerzeugung vor.
	 *
	 * @param maxRounds Anzahl der Runden.
	 */
	public static Game createGameWhereAllItemsCanBeBought(int maxRounds) {
		return createGameMatching("has a shop where all items can be bought.", maxRounds,
				TestUtils::allItemsCanBeBought);
	}

	/**
	 * Erzeugt ein Spiel mit <code>maxRounds</code> Runden, in dem in der ersten
	 * Runde ein Gegenstand für jedes Prädikat gekauft wurde.
	 * <p>
	 * Dazu werden immer wieder Spiele erzeugt. Nach {@value #GAME_CREATION_LIMIT}
	 * Versuchen wird aufgegeben. Dann liegt höchstwahrscheinlich ein Fehler bei der
	 * Spielerzeugung vor.
	 * <p>
	 * Die Karten im Shop werden nacheinander durchgearbeitet und dem ersten
	 * Prädikat zugeordnet, dass auf sie zutrifft und gekauft. Das Prädikat wird für
	 * spätere Karten nicht mehr verwendet.
	 *
	 * @param errorMsg       Teil der Fehlermeldung, wenn kein Spiel gefunden wurde.
	 * @param maxRounds      Anzahl der Runden.
	 * @param cardPredicates Bedingungen, die je von unterschiedlichen
	 *                       Beschreibungen erfüllt werden müssen.
	 */
	@SafeVarargs
	// Für Interessierte, die Generics schon kennen: varargs sind Arras und es gibt
	// keine Arrays mit
	// generischem Typ. Daher hat cardPredicates den Typ Predicate[]. Wird nun ein
	// Predicate<Integer>
	// nur als Predicate verwendet, also ohne generischen Typ (was für sich schon
	// eine Warnung
	// erzeugt), ist es möglich cardPredicates[0] dieses Predicate<Integer>
	// zuzuweisen. Dieser Zustand
	// wird heap pollution genannt, weil eine Variable, die zur Compilezeit
	// Predicate<String> zur
	// Laufzeit auf etwas verweist, was eigentlich Predicate<Integer> ist. Wird das
	// Prädikat
	// verwendet, kommt es zu einer ClassCastException, weil String nicht zu Integer
	// gecastet werden
	// kann. Die exception ist unerwartet, weil der Compiler solche Fehler zur
	// Compilezeit findet.
	// Daher gibt es für eine Methode mit generischen varargs eine Warnung vor einer
	// potenziellen heap
	// pollution. Mit @SafeVarargs kann der Programmierer dem Compiler mitteilen,
	// dass er selber
	// sichergestellt hat, dass es nicht zu einer heap pollution kommt. Dadurch gibt
	// es dann keine
	// Warnung. Diese Funktion ist sicher, weil sie cardPredicate nicht verändert
	// und
	// createGameWithCardsMatching auch mit @SafeVarargs gekennzeichnet ist. Wenn
	// Sie den
	// Funktionsaufrufen folgen, kommen SIe zu einer Funktion, die aus dem Array nur
	// liest. Dort geht
	// der generische Typ nicht verloren und sogar der Compiler würde herausfinden,
	// wenn etwas anderes
	// als ein String übergeben wird.
	public static Game createGameWithCardsMatching(String errorMsg, int maxRounds,
			Predicate<String>... cardPredicates) {
		return createGameWithCardsMatching(errorMsg, maxRounds, null, cardPredicates);
	}

	/**
	 * Erzeugt ein Spiel mit <code>maxRounds</code> Runden, in dem in der ersten
	 * Runde ein Gegenstand für jedes Prädikat gekauft wurde und das Spiel auch eine
	 * Bedingung erfüllt.
	 * <p>
	 * Dazu werden immer wieder Spiele erzeugt. Nach {@value #GAME_CREATION_LIMIT}
	 * Versuchen wird aufgegeben. Dann liegt höchstwahrscheinlich ein Fehler bei der
	 * Spielerzeugung vor.
	 * <p>
	 * Die Karten im Shop werden nacheinander durchgearbeitet und dem ersten
	 * Prädikat zugeordnet, dass auf sie zutrifft und gekauft. Das Prädikat wird für
	 * spätere Karten nicht mehr verwendet.
	 *
	 * @param errorMsg       Teil der Fehlermeldung, wenn kein Spiel gefunden wurde.
	 * @param maxRounds      Anzahl der Runden.
	 * @param gamePredicate  Bedingung, die vom Spiel erfüllt sein muss. Wird
	 *                       ignoriert, falls es <code>null</code> ist.
	 * @param cardPredicates Bedingungen, die je von unterschiedlichen
	 *                       Beschreibungen erfüllt werden müssen.
	 */
	@SafeVarargs
	public static Game createGameWithCardsMatching(String errorMsg, int maxRounds,
			Predicate<? super Game> gamePredicate, Predicate<String>... cardPredicates) {
		return createGameWithCardsMatching(errorMsg, maxRounds, true, gamePredicate, cardPredicates);
	}

	/**
	 * Erzeugt ein Spiel mit <code>maxRounds</code> Runden, in dem in der ersten
	 * Runde ein Gegenstand für jedes Prädikat gekauft werden kann und das Spiel
	 * auch eine Bedingung erfüllt.
	 * <p>
	 * Dazu werden immer wieder Spiele erzeugt. Nach {@value #GAME_CREATION_LIMIT}
	 * Versuchen wird aufgegeben. Dann liegt höchstwahrscheinlich ein Fehler bei der
	 * Spielerzeugung vor.
	 * <p>
	 * Die Karten im Shop werden nacheinander durchgearbeitet und dem ersten
	 * Prädikat zugeordnet, dass auf sie zutrifft und gekauft, sofern
	 * <code>buy == true</code> ist. Das Prädikat wird für spätere Karten nicht mehr
	 * verwendet.
	 * <p>
	 * <code>buy</code> muss <code>true</code> sein oder es darf höchstens eine
	 * Karte angegeben sein.
	 *
	 * @param errorMsg       Teil der Fehlermeldung, wenn kein Spiel gefunden wurde.
	 * @param maxRounds      Anzahl der Runden.
	 * @param buy            Gibt an, ob die Gegenstände gleich gekauft werden
	 *                       sollen.
	 * @param gamePredicate  Bedingung, die vom Spiel erfüllt sein muss. Wird
	 *                       ignoriert, falls es <code>null</code> ist.
	 * @param cardPredicates Bedingungen, die je von unterschiedlichen
	 *                       Beschreibungen erfüllt werden müssen.
	 */
	@SafeVarargs
	private static Game createGameWithCardsMatching(String errorMsg, int maxRounds, boolean buy,
			Predicate<? super Game> gamePredicate, Predicate<String>... cardPredicates) {
		assert buy || cardPredicates.length <= 1;
		assert cardPredicates.length <= 5;
		return createGameMatching("has a shop matching: " + errorMsg, maxRounds, game -> { // Lambda-Ausdruck für ein
																							// Predicate<Game>
			if (gamePredicate != null && !gamePredicate.test(game)) {
				return false; // return bezieht sich auf den Lambda-Ausdruck, nicht
								// createGameWithCardsMatching
			}
			final var fulfilledPredicates = new boolean[cardPredicates.length];
			int remainingPredicates = cardPredicates.length;

			for (int iShop = 0; iShop < game.getShopItemCount(); iShop++) {
				final var description = game.getShopItemDescription(iShop);
				if (!ANY_CARD.test(description)) {
					continue; // Ignore everything that is not a card
				}
				// For each card
				for (int iPred = 0; iPred < cardPredicates.length; iPred++) {
					if (!fulfilledPredicates[iPred] && cardPredicates[iPred].test(description)) {
						fulfilledPredicates[iPred] = true;
						--remainingPredicates;
						if (!game.buy(iShop)) {
							return false; // if we couldn't buy the cards, try a new game.
						}
						if (remainingPredicates == 0) {
							return true;
						}
					}
				}
			}
			return false;
		});
	}

	/**
	 * Erzeugt ein Spiel mit <code>maxRounds</code> Runden, dass die gegebene
	 * Bedingung erfüllt.
	 * <p>
	 * Dazu werden immer wieder Spiele erzeugt. Nach {@value #GAME_CREATION_LIMIT}
	 * Versuchen wird aufgegeben. Dann liegt höchstwahrscheinlich ein Fehler bei der
	 * Spielerzeugung vor.
	 * <p>
	 * Die Karten im Shop werden nacheinander durchgearbeitet und dem ersten
	 * Prädikat zugeordnet, dass auf sie zutrifft und gekauft. Das Prädikat wird für
	 * spätere Karten nicht mehr verwendet.
	 *
	 * @param errorMsg  Teil der Fehlermeldung, wenn kein Spiel gefunden wurde.
	 * @param maxRounds Anzahl der Runden.
	 */
	public static Game createGameMatching(String errorMsg, int maxRounds, Predicate<? super Game> gamePredicates) {
		for (int i = 0; i < GAME_CREATION_LIMIT; i++) { // GAME_CREATION_LIMIT viele Versuche
			final var game = new Game(maxRounds);
			if (gamePredicates.test(game)) {
				return game;
			}
		}
		fail("There is some error in game creation, item descriptions or pricing. Expected that when "
				+ "generating 1000 games, at least one game " + errorMsg);
		while (true)
			; // Unreachable
	}

	/**
	 * Kauft in <code>game</code> einen Gegenstand, der das Prädikat erfüllt. Das
	 * Spiel muss im Modus {@link Mode#SHOPPING} sein. Gibt es keinen Gegenstand,
	 * der dem Prädikat entspricht und auch gekauft werden kann, schlägt eine
	 * Assertion fehl.
	 *
	 * @param game      das Spiel, in dem gekauft werden soll.
	 * @param msg       textuelle Beschreibung für das, was das Prädikat erfüllt.
	 * @param predicate das vom Gegenstand zu erfüllende Prädikat.
	 */
	public static void buyCard(Game game, String msg, Predicate<String> predicate) {
		assert game.getMode() == Mode.SHOPPING;
		for (int i = 0; i < game.getShopItemCount(); i++) {
			if (predicate.test(game.getShopItemDescription(i))) {
				if (game.buy(i)) {
					return;
				}
			}
		}
		fail("Failed to buy " + msg + " in game " + game);
	}

	/**
	 * Kauft eine Chance-Karte. Gibt es keine kaufbare, schlägt eine Assertion fehl.
	 */
	public static void buyChance(Game game) {
		buyCard(game, "any Chance card", CHANCE);
	}

	/**
	 * Kauft eine Karte. Gibt es keine kaufbare, schlägt eine Assertion fehl.
	 */
	public static void buyAnyCard(Game game) {
		buyCard(game, "any card", ANY_CARD);
	}

	/**
	 * Kauft alle verbleibenden Karten im Shop. Kann eine Karte nicht gekauft
	 * werden, wird das ignoriert.
	 * <p>
	 * Das Spiel muss im Modus {@link Mode#SHOPPING} sein.
	 */
	public static void buyAllCards(Game game) {
		assert game.getMode() == Mode.SHOPPING;
		for (int i = game.getShopItemCount() - 1; i >= 0; i--) {
			if (ANY_CARD.test(game.getShopItemDescription(i))) {
				game.buy(i);
			}
		}
	}

	/**
	 * Kauft alle bis auf eine der verbleibenden Karten im Shop. Kann eine Karte
	 * nicht gekauft werden, wird das ignoriert. Ebenso wie wenn es gar keine Karte
	 * mehr gibt.
	 * <p>
	 * Das Spiel muss im Modus {@link Mode#SHOPPING} sein.
	 */
	public static void buyAllCardsExceptOne(Game game) {
		assert game.getMode() == Mode.SHOPPING;
		for (int i = game.getShopItemCount() - 1; i >= 1; i--) {
			if (ANY_CARD.test(game.getShopItemDescription(i))) {
				game.buy(i);
			}
		}
	}

	/**
	 * Spielt so lange Karten bis die Hand leer ist.
	 */
	public static void simplePlay(Game game) {
		while (game.getMode() == Mode.PLAYING) {
			assertTrue("Invariant for Mode.PLAYING: Hand must hold at least one card.", game.getHandCardsCount() > 0);
			game.play(game.getHandCard(0), 0);
		}
	}

	/**
	 * Stellt sicher, dass <code>value</code> aus dem Intervall
	 * <code>[min, max]</code> kommt.
	 */
	public static void assertInClosedIntervall(int min, int max, int value) {
		if (value > max || min > value) {
			fail("Expected a value from [" + min + ", " + max + "] but got " + value);
		}
	}

	/**
	 * Stellt sicher, dass <code>value</code> einer der Werte in
	 * <code>expectedOptions</code> ist.
	 */
	public static <T> void assertContains(Collection<T> expectedOptions, T value) {
		if (!expectedOptions.contains(value)) {
			fail("Expected one of " + expectedOptions + " but got " + value);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void sneakyThrow(Throwable throwable) throws T {
		throw (T) throwable;
	}

	/**
	 * Überprüft, ob der Codeausschnitt des ersten Arguments eine bestimmte
	 * Exception wirft.
	 *
	 * @param allowSubclass     ob auch unterklassen von
	 *                          <code>expectedException</code> geworfen werden
	 *                          dürfen.
	 * @param expectedException der Typ der erwartete Exception.
	 * @param test              der zu testende Code.
	 */
	public static <T extends Throwable> T assertThrows(boolean allowSubclass, Class<T> expectedException,
			ThrowsTest test) {
		try {
			test.run();
		} catch (Throwable t) {
			if (allowSubclass) {
				if (expectedException.isInstance(t)) {
					return expectedException.cast(t);
				} else {
					t.printStackTrace();
					fail("Expected an Exception of type " + expectedException.getName()
							+ " or a subtype but another exception was thrown: " + t.getMessage());
				}
			} else {
				if (expectedException.equals(t.getClass())) {
					return expectedException.cast(t);
				} else if (expectedException.isInstance(t)) {
					t.printStackTrace();
					fail("Expected an Exception of type " + expectedException.getName()
							+ " but the exception type was a subclass of the expected class: " + t.getMessage());
				} else {
					t.printStackTrace();
					fail("Expected an Exception of type " + expectedException.getName()
							+ " but another exception was thrown: " + t.getMessage());
				}
			}
		}
		fail("Expected an Exception of type " + expectedException.getName() + " but no exception was thrown.");
		while (true)
			; // Unreachable
	}

	/**
	 * Überprüft, ob der Codeausschnitt des ersten Arguments eine Exception wirft
	 * und die geworfene Exception von einem bestimmten Typ ist. Überprüft wird der
	 * Reihe nach bis der Typ passt. Danach kann die Exception noch weiter überprüft
	 * werden (Siehe {@link ThrowsExpected#thr(Class, Consumer)}).
	 *
	 * @param test                        der zu testende Code.
	 * @param expectedException           erste mögliche Exception.
	 * @param additionalExpectedException weitere Optionen für die Exception. Die
	 *                                    Exception muss mindestens einer von diesen
	 *                                    oder <code>expectedException</code>
	 *                                    entsprechen.
	 * @see ThrowsExpected#thr(Class)
	 * @see ThrowsExpected#thr(Class, Consumer)
	 */
	public static void assertThrows(ThrowsTest test, ThrowsExpected<?> expectedException,
			ThrowsExpected<?>... additionalExpectedException) {
		final var expectedOptions = Stream
				.concat(Stream.of(expectedException), Arrays.stream(additionalExpectedException)).toList();
		try {
			test.run();
		} catch (Throwable t) {
			for (final var throwsExpected : expectedOptions) {
				if (throwsExpected.test(t)) {
					return;
				}
			}
			t.printStackTrace();
			fail(startFailMessage(expectedOptions).append("an exception of type ").append(t.getClass().getName())
					.append(" was thrown: ").append(t.getMessage()).toString());
		}
		fail(startFailMessage(expectedOptions).append("no exception was throw.").toString());
		while (true)
			; // Unreachable
	}

	private static StringBuilder startFailMessage(List<ThrowsExpected<?>> expectedOptions) {
		final var builder = new StringBuilder("Expected an Exception of type ")
				.append(expectedOptions.get(0).classOfExpectedException.getName());
		for (int i = 1; i < expectedOptions.size(); i++) {
			if (i == expectedOptions.size() - 1) {
				builder.append(" or ");
			} else {
				builder.append(", ");
			}
		}
		builder.append(" or of a sub-type of the listed types but ");
		return builder;
	}

	/**
	 * Ignoriert bestimmte Exceptions bei der Ausführung des Codes.
	 */
	public static void ignoreExceptions(ThrowsTest test, Class<?>... ignoreExceptions) {
		final var names = Arrays.stream(ignoreExceptions).map(Class::getName).toArray(String[]::new);
		ignoreExceptions(test, names);
	}

	/**
	 * Ignoriert bestimmte Exceptions bei der Ausführung des Codes. Der
	 * Exception-Typ wird über den vollständigen Klassennamen verglichen.
	 */
	public static void ignoreExceptions(ThrowsTest test, String... ignoreExceptions) {
		try {
			test.run();
		} catch (Throwable t) {
			for (String ignoreException : ignoreExceptions) {
				if (ignoreException.equals(t.getClass().getName())) {
					return;
				}
			}
			sneakyThrow(t);
		}
	}

	/**
	 * Gibt zurück, ob alle Karten gekauft werden können.
	 */
	public static boolean allCardsCanBeBought(Game g) {
		int sum = 0;
		for (int i = 0; i < g.getShopItemCount(); i++) {
			if (ANY_CARD.test(g.getShopItemDescription(i))) {
				sum += g.getShopItemPrice(i);
			}
		}
		return sum <= g.getCredits();
	}

	/**
	 * Gibt zurück, ob alle Gegenstände gekauft werden können.
	 */
	public static boolean allItemsCanBeBought(Game g) {
		int sum = 0;
		for (int i = 0; i < g.getShopItemCount(); i++) {
			sum += g.getShopItemPrice(i);
		}
		return sum <= g.getCredits();
	}

	/**
	 * Abstraktion für Code, der eine Exception wirft.
	 */
	public interface ThrowsTest {

		void run() throws Throwable;
	}

	public static final class ThrowsExpected<T extends Throwable> {

		private final Class<T> classOfExpectedException;
		private final Consumer<? super T> exceptionAssertion;

		/**
		 * Erzeugt eine überprüfung für eine Exception.
		 *
		 * @param classOfExpectedException die Exception muss von diesem oder einem
		 *                                 Untertyp sein.
		 */
		public static <T extends Throwable> ThrowsExpected<T> thr(Class<T> classOfExpectedException) {
			return new ThrowsExpected<>(
					Objects.requireNonNull(classOfExpectedException, "classOfExpectedException must not be null."),
					null);
		}

		/**
		 * Erzeugt eine überprüfung für eine Exception. Beim Testen wird die Exception
		 * noch an <code>exceptionAssertion</code> übergeben, wo weitere Assertions
		 * durchgeführt werden können.
		 *
		 * @param classOfExpectedException die Exception muss von diesem oder einem
		 *                                 Untertyp sein.
		 * @param exceptionAssertion       weitere Assertions.
		 */
		public static <T extends Throwable> ThrowsExpected<T> thr(Class<T> classOfExpectedException,
				Consumer<? super T> exceptionAssertion) {
			return new ThrowsExpected<>(
					Objects.requireNonNull(classOfExpectedException, "classOfExpectedException must not be null."),
					Objects.requireNonNull(exceptionAssertion, "exceptionAssertion must not be null."));
		}

		private ThrowsExpected(Class<T> classOfExpectedException, Consumer<? super T> exceptionAssertion) {
			this.classOfExpectedException = classOfExpectedException;
			this.exceptionAssertion = exceptionAssertion;
		}

		public boolean test(Throwable t) {
			if (classOfExpectedException.isInstance(t)) {
				if (exceptionAssertion != null) {
					exceptionAssertion.accept(classOfExpectedException.cast(t));
				}
				return true;
			}
			return false;
		}
	}
}
