package cardmaster;

import cardmaster.Game.Mode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static cardmaster.TestUtils.ThrowsExpected.thr;
import static cardmaster.TestUtils.*;
import static org.junit.Assert.*;

public class TestScoreBoard {

	@Rule
	public Timeout globalTimeout = Timeout.millis(1000);
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME
			.withZone(ZoneId.systemDefault());

	private record TableEntry(Instant start, Instant end, double score, int place) {

		public TableEntry(Runnable effect, double score, int place) {
			this(Instant.now(), ((Supplier<Instant>) () -> {
				effect.run();
				return Instant.now();
			}).get(), score, place);
		}

		public boolean isInRange(Instant instant) {
			return !instant.isBefore(start) && !instant.isAfter(end);
		}
	}

	private void assertScoreBoard(List<TableEntry> expected) {
		assertEquals(expected.size(), scoreBoard.size());

		final var placeOptions = expected.stream()
				.collect(Collectors.groupingBy(e -> e.place, TreeMap::new, Collectors.toCollection(ArrayList::new)));
		assert placeOptions.values().stream().allMatch(v -> { // Same place must imply same score
			final var score = v.get(0).score;
			return v.stream().allMatch(e -> Math.abs(e.score - score) < 1E-12);
		});
		int activePlace = -1;
		double activeScore = -1;
		ArrayList<TableEntry> activeEntryList = null;

		for (int n = 0; n < scoreBoard.size(); n++) {
			if (activeEntryList == null || activeEntryList.isEmpty()) {
				final var entry = placeOptions.pollFirstEntry();
				assert entry != null;
				activePlace = entry.getKey();
				activeScore = entry.getValue().get(0).score;
				activeEntryList = entry.getValue();
			}

			assertEquals("Wrong place for entry at index " + n + ".", activePlace, scoreBoard.getPlace(n));

			assertEquals("Wrong score for entry at index " + n + ".", activeScore, scoreBoard.getScore(n), 1E-12);

			// Find entry in active list where instant matches
			final var instant = scoreBoard.getInstant(n);

			TableEntry tableEntry = null;
			for (TableEntry te : activeEntryList) {
				if (te.isInRange(instant)) {
					tableEntry = te;
				}
			}
			assertNotNull(activeEntryList.stream()
					.map(e -> "[" + FORMATTER.format(e.start) + " - " + FORMATTER.format(e.end) + "]")
					.collect(Collectors.joining(", ",
							"Got table entry for place " + activePlace
									+ " with unexpected time stamp. Expected one from (one of) the interval(s) ",
							"but got " + FORMATTER.format(instant))),
					tableEntry);

			activeEntryList.remove(tableEntry);
		}
		assert activeEntryList == null || activeEntryList.isEmpty();
	}

	private ScoreBoard scoreBoard;

	@Before
	public void before() {
		scoreBoard = ScoreBoard.getInstance();
		scoreBoard.clear();
	}

	@Test
	public void scoreBoardIsEmptyAfterClear() {
		assertScoreBoard(List.of());
	}

	@Test
	public void thereIsOnlyOneScoreBoard() {
		assertSame(ScoreBoard.getInstance(), ScoreBoard.getInstance());
	}

	@Test
	public void insertingSingle() {
		final var expected = List.of(new TableEntry(() -> {
			assertEquals("First entry in score board is always place 1.", 1, scoreBoard.add(100.0));
		}, 100.0, 1));
		assertScoreBoard(expected);
	}

	@Test
	public void insertingMultiple0() {
		final var expected = List.of(new TableEntry(() -> {
			assertEquals("First entry in score board is always place 1.", 1, scoreBoard.add(100.0));
		}, 100.0, 1), new TableEntry(() -> {
			assertEquals("Same score as best.", 1, scoreBoard.add(100.0));
		}, 100.0, 1));
		assertScoreBoard(expected);
	}

	@Test
	public void insertingMultiple1() {
		final var expected = List.of(new TableEntry(() -> {
			assertEquals("First entry in score board is always place 1.", 1, scoreBoard.add(9.0));
		}, 9.0, 2), new TableEntry(() -> {
			assertEquals("Beats best place", 1, scoreBoard.add(100.0));
		}, 100.0, 1));
		assertScoreBoard(expected);
	}

	@Test
	public void insertingMultiple2() {
		final var expected = List.of(new TableEntry(() -> {
			assertEquals("First entry in score board is always place 1.", 1, scoreBoard.add(9.0));
		}, 9.0, 1), new TableEntry(() -> {
			assertEquals("Behind first place", 2, scoreBoard.add(8.9999));
		}, 8.9999, 2));
		assertScoreBoard(expected);
	}

	@Test
	public void insertingMultiple3() {
		final var expected = List.of(new TableEntry(() -> {
			assertEquals("First entry in score board is always place 1.", 1, scoreBoard.add(9.0));
		}, 9.0, 1), new TableEntry(() -> {
			assertEquals("Behind first place", 2, scoreBoard.add(8.99991));
		}, 8.99991, 4), new TableEntry(() -> {
			assertEquals("Middle place", 2, scoreBoard.add(8.99999));
		}, 8.99999, 2), new TableEntry(() -> {
			assertEquals("Middle place", 2, scoreBoard.add(8.99999));
		}, 8.99999, 2), new TableEntry(() -> {
			assertEquals("Last place", 4, scoreBoard.add(8.99991));
		}, 8.99991, 4), new TableEntry(() -> {
			assertEquals("Last place", 6, scoreBoard.add(1.01));
		}, 1.01, 6));
		assertScoreBoard(expected);
	}

	private static void assertNotThrown(String message, ThrowsTest test) {
		try {
			test.run();
		} catch (Throwable e) {
			throw new AssertionError(message, e);
		}
	}

	private void writeRead() {
		final var out = new ByteArrayOutputStream();
		assertNotThrown("Save must not fail", () -> scoreBoard.save(out));
		// No flush
		scoreBoard.clear();
		class AssertingInputStream extends InputStream {
			private final byte[] data = out.toByteArray();
			private int next;

			@Override
			public int read() {
				return next < data.length ? (int) data[next++] : -1;
			}

			@Override
			public void close() {
			}
		}
		try (var in = new AssertingInputStream()) {
			assertTrue("Some information need to be written to the output stream.", in.data.length > 0);
			assertNotThrown("Load must not fail", () -> scoreBoard.load(in));
			assertEquals("Did not read all bytes that where written", in.data.length, in.next);
		}
	}

	@Test
	public void writeReadEmpty() {
		writeRead();
		assertScoreBoard(List.of());
	}

	@Test
	public void writeReadSingleScore() {
		final var expected = List.of(new TableEntry(() -> {
			scoreBoard.add(123.45);
		}, 123.45, 1));
		assertScoreBoard(expected);
		writeRead();
		assertScoreBoard(expected);
	}

	@Test
	public void writeReadMultipleSameScore() {
		final var expected = List.of(new TableEntry(() -> {
			scoreBoard.add(123.45);
		}, 123.45, 1), new TableEntry(() -> {
			scoreBoard.add(123.45);
		}, 123.45, 1));
		assertScoreBoard(expected);
		writeRead();
		assertScoreBoard(expected);
	}

	@Test
	public void writeReadMultipleDifferentScore() {
		final var expected = List.of(new TableEntry(() -> {
			scoreBoard.add(123.45);
		}, 123.45, 1), new TableEntry(() -> {
			scoreBoard.add(123.4);
		}, 123.4, 2), new TableEntry(() -> {
			scoreBoard.add(123.4);
		}, 123.4, 2), new TableEntry(() -> {
			scoreBoard.add(89.2);
		}, 89.2, 4), new TableEntry(() -> {
			scoreBoard.add(89.2);
		}, 89.2, 4), new TableEntry(() -> {
			scoreBoard.add(0.2);
		}, 0.2, 6));
		assertScoreBoard(expected);
		writeRead();
		assertScoreBoard(expected);
	}

	@Test
	public void writeReadMultipleScoreBoardVersions() throws Exception {
		final var expected0 = List.of(new TableEntry(() -> {
			scoreBoard.add(123.45);
		}, 123.45, 1), new TableEntry(() -> {
			scoreBoard.add(123.4);
		}, 123.4, 2), new TableEntry(() -> {
			scoreBoard.add(123.4);
		}, 123.4, 2), new TableEntry(() -> {
			scoreBoard.add(89.2);
		}, 89.2, 4), new TableEntry(() -> {
			scoreBoard.add(89.2);
		}, 89.2, 4), new TableEntry(() -> {
			scoreBoard.add(0.2);
		}, 0.2, 6));

		assertScoreBoard(expected0);
		final var out0 = new ByteArrayOutputStream();
		assertNotThrown("Save must not fail", () -> scoreBoard.save(out0));
		out0.flush();
		final var stored0 = out0.toByteArray();
		scoreBoard.clear();
		assertScoreBoard(List.of());

		final var expected1 = List.of(new TableEntry(() -> {
			scoreBoard.add(10.1);
		}, 10.1, 1));
		assertScoreBoard(expected1);
		final var out1 = new ByteArrayOutputStream();
		assertNotThrown("Save must not fail", () -> scoreBoard.save(out1));
		out1.flush();
		final var stored1 = out1.toByteArray();
		scoreBoard.clear();
		assertScoreBoard(List.of());

		scoreBoard.load(new ByteArrayInputStream(stored0));
		assertScoreBoard(expected0);
		scoreBoard.load(new ByteArrayInputStream(stored1));
		assertScoreBoard(expected1);
		scoreBoard.load(new ByteArrayInputStream(stored0));
		assertScoreBoard(expected0);
		scoreBoard.load(new ByteArrayInputStream(stored1));
		assertScoreBoard(expected1);
	}

	@Test
	public void writeDoesNotCloseStream() throws Exception {
		scoreBoard.add(10);
		final var closed = new boolean[1];
		final var flushed = new boolean[1];
		scoreBoard.save(new OutputStream() {
			@Override
			public void write(int b) {
			}

			@Override
			public void flush() {
				flushed[0] = true;
			}

			@Override
			public void close() {
				closed[0] = true;
			}
		});
		assertTrue("save must call close", closed[0]);
		assertTrue("save must call flush", flushed[0]);
	}

	@Test
	public void readDoesNotCloseStream() throws Exception {
		scoreBoard.add(10);
		final var out = new ByteArrayOutputStream();
		scoreBoard.save(out);
		out.flush();
		final var closed = new boolean[1];
		scoreBoard.load(new ByteArrayInputStream(out.toByteArray()) {
			@Override
			public void close() {
				closed[0] = true;
			}
		});
		assertTrue("read must call close", closed[0]);
	}

	@SuppressWarnings("unused")
	final static class NotAllowedClass implements Serializable {

		@Serial
		private static final long serialVersionUID = -4058109224285527019L;
		private final double a;
		private final double b;
		private final double c;

		NotAllowedClass(double a, double b, double c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Serial
		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			fail("Class was deserialized");
			in.defaultReadObject();
		}
	}

	@Test
	public void unchangedOnSaveException() {
		final var expected = List.of(new TableEntry(() -> scoreBoard.add(10.0), 10.0, 2),
				new TableEntry(() -> scoreBoard.add(10.0), 10.0, 2),
				new TableEntry(() -> scoreBoard.add(11.0), 11.0, 1), new TableEntry(() -> scoreBoard.add(9.0), 9.0, 4));
		assertScoreBoard(expected);
		final var readAfterException = new boolean[1];
		final var exception = new IOException[1];
		assertThrows(() -> scoreBoard.save(new OutputStream() {
			int remainingUntilException = 3;

			@Override
			public void write(int b) throws IOException {
				if (remainingUntilException-- == 0) {
					exception[0] = new IOException();
					throw exception[0];
				} else if (remainingUntilException < 0) {
					readAfterException[0] = true;
				}
			}
		}), thr(IOException.class, e -> {
			if (exception[0] == null) {
				fail("Unexpected exception: " + e.getMessage());
			} else if (e != exception[0] && e.getCause() != exception[0]) {
				fail("Original exception must either be thrown as is or wrapped into a different exception.");
			}
		}));
		if (readAfterException[0]) {
			fail("Exception from reading must be propagated immediately.");
		}
		assertScoreBoard(expected);
	}

	@Test
	public void unchangedOnLoadException() {
		assertScoreBoard(List.of(new TableEntry(() -> scoreBoard.add(10.0), 10.0, 2),
				new TableEntry(() -> scoreBoard.add(10.0), 10.0, 2),
				new TableEntry(() -> scoreBoard.add(11.0), 11.0, 1),
				new TableEntry(() -> scoreBoard.add(9.0), 9.0, 4)));
		final var out = new ByteArrayOutputStream();
		assertNotThrown("No exception expected", () -> {
			scoreBoard.save(out);
			out.flush();
		});
		final var bytes = out.toByteArray();
		if (bytes.length == 0) {
			return;
		}

		scoreBoard.clear();
		final var expected = List.of(new TableEntry(() -> scoreBoard.add(11.0), 11.0, 1),
				new TableEntry(() -> scoreBoard.add(9.0), 9.0, 2));
		assertScoreBoard(expected);

		final var incompleteData = Arrays.copyOf(bytes, bytes.length / 2);
		assertThrows(() -> scoreBoard.load(new ByteArrayInputStream(incompleteData)), thr(IOException.class));
		assertScoreBoard(expected);
	}

	@Test
	public void addAssertsScore() {
		for (Double invalid : List.of(-1.9, Double.NaN, Double.NEGATIVE_INFINITY)) {
			assertThrows(() -> scoreBoard.add(invalid), thr(IllegalArgumentException.class,
					e -> assertTrue(e.getMessage().contains(Double.toString(invalid)))));
			assertEquals(0, scoreBoard.size());
		}
	}

	@Test
	public void gettersAssertIndex() {
		assertThrows(() -> scoreBoard.getScore(-1), thr(IndexOutOfBoundsException.class, getIndexAssertion(-1)));
		assertThrows(() -> scoreBoard.getScore(0), thr(IndexOutOfBoundsException.class, getIndexAssertion(0)));
		assertThrows(() -> scoreBoard.getInstant(-1), thr(IndexOutOfBoundsException.class, getIndexAssertion(-1)));
		assertThrows(() -> scoreBoard.getInstant(0), thr(IndexOutOfBoundsException.class, getIndexAssertion(0)));
		assertThrows(() -> scoreBoard.getPlace(-1), thr(IndexOutOfBoundsException.class, getIndexAssertion(-1)));
		assertThrows(() -> scoreBoard.getPlace(0), thr(IndexOutOfBoundsException.class, getIndexAssertion(0)));
	}

	private static Consumer<IndexOutOfBoundsException> getIndexAssertion(int index) {
		return e -> assertTrue("Message must contain wrong index.", e.getMessage().contains(Integer.toString(index)));
	}

	@Test
	public void finishedGamesAreAdded() {
		int i = 0;
		while (i < 100) {
			assertEquals("After playing " + i + " games, there should be " + i + " entries.", i, scoreBoard.size());
			final var game = createGameWithChanceCard(1, true);
			game.endShopping();
			simplePlay(game);
			i++;
		}
		assertEquals(i, scoreBoard.size());
	}

	@Test
	public void gameIsAddedWithFinalScore() {
		final var game = createGameWithChanceCard(1, true);
		game.endShopping();
		final var tableEntry = new TableEntry(() -> simplePlay(game), game.getCredits() + 1.0, // get one credit in
																								// round
				1);
		assertEquals(Mode.END, game.getMode());
		if (scoreBoard.size() == 1) {
			assertEquals(game.getCredits(), scoreBoard.getScore(0), 1E-12);
			assertScoreBoard(List.of(tableEntry));
		}
	}

//////////////////////////////////////
// Optionaler Test für Serializable //
//////////////////////////////////////
//
// In moodle wäre dieser Test nicht erfüllbar und würde auch dafür sorgen, dass andere Tests
// fehlschlagen. Daher ist er überall auskommentiert. Wenn Sie möchten, können Sie lokal versuchen
// ihn zu bestehen. Committen Sie aber nur, wenn Sie die Ausnahme auch abfangen.
//
//  @Test
//  public void objectInputFilterUsedWhenUsingSerializable() throws Exception {
//    // Dieser Test stellt sicher, dass ein ObjectInputFilter verwendet wird, der nur bestimmte
//    // Klassen erlaubt. Wenn Serializable gar nicht verwendet wird, muss auch keine Filter verwendet
//    // werden. Daher muss zunächst erkannt werden, ob der Test angewendet werden kann. Dafür werden
//    // die ersten Bytes der Ausgabe verwendet.
//
//    final var expected = List.of(
//        new TableEntry(() -> scoreBoard.add(1.0), 1.0, 1)
//    );
//    var out = new ByteArrayOutputStream();
//    scoreBoard.save(out);
//    out.flush();
//    final var testIn = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
//    if (testIn.readShort() == ObjectInputStream.STREAM_MAGIC) {
//      // Sieht aus wie ein Object Serialization Stream
//      assertScoreBoard(expected); // War es vorher richtig?
//      // Klasse, die nicht gelesen werden darf
//      out = new ByteArrayOutputStream();
//      try (final var objOut = new ObjectOutputStream(out)) {
//        objOut.writeObject(new NotAllowedClass[]{
//            new NotAllowedClass(0.0, 0.0, 0.0),
//            new NotAllowedClass(1.0, 2.0, 3.0)
//        });
//      }
//
//      final var in = new ByteArrayInputStream(out.toByteArray());
//
//      // Der Filter wirft die Exception automatisch, wenn er richtig konfiguriert ist.
//      assertThrows(() -> scoreBoard.load(in), thr(InvalidClassException.class));
//
//      assertScoreBoard(expected);
//    }
//  }
}
