package cardmaster;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import cardmaster.scoreboard.Bestenliste;

/**
 * Singleton für das Verwalten einer Bestenliste. Jeder Eintrag setzt sich aus
 * dem finalen Punktestand des Spiels, einem Zeitstempel und dem aktuellen Platz
 * in der Bestenliste zusammen. Einträge werden über ihren Index abgerufen.
 * Dabei sind die Einträge nach Platz aufsteigend bzw. gleichbedeutend
 * Punktestand absteigend sortiert. In diesem Sinne gleiche Einträge sind nicht
 * weiter sortiert:
 * <table>
 * <tr>
 * <td>Index ({@code n})</td>
 * <td>Platz</td>
 * <td>Punktestand</td>
 * <td>Zeitstempel</td>
 * </tr>
 * <tr>
 * <td>0</td>
 * <td>1</td>
 * <td>300.5</td>
 * <td>2024-01-29T07:20:00Z</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>2</td>
 * <td>290.0</td>
 * <td>2024-01-22T15:14:13Z</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>2</td>
 * <td>290.0</td>
 * <td>2024-01-22T15:15:13Z</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>4</td>
 * <td>150.0</td>
 * <td>2024-01-23T08:59:42Z</td>
 * </tr>
 * <tr>
 * <td>4</td>
 * <td>5</td>
 * <td>140.9</td>
 * <td>2024-02-01T23:13:37Z</td>
 * </tr>
 * <tr>
 * <td>5</td>
 * <td>5</td>
 * <td>140.9</td>
 * <td>2024-01-01T03:37:20Z</td>
 * </tr>
 * <tr>
 * <td>5</td>
 * <td>5</td>
 * <td>140.9</td>
 * <td>2024-01-27T18:38:01Z</td>
 * </tr>
 * <tr>
 * <td>7</td>
 * <td>8</td>
 * <td>110.0</td>
 * <td>2024-01-01T00:00:00Z</td>
 * </tr>
 * <tr>
 * <td>8</td>
 * <td>9</td>
 * <td>80.0</td>
 * <td>2023-12-31T23:59:59Z</td>
 * </tr>
 * </table>
 * <p>
 * Initial ist die Bestenliste leer. Erst wenn externer Code
 * {@link #load(InputStream)} aufruft, wird der alte Zustand wiederhergestellt.
 * Speichern muss auch von externem Code veranlasst werden.
 */
public interface ScoreBoard {

	/**
	 * Gibt die eine Instanz der {@link ScoreBoard}-Implementierung zurück.
	 */
	static ScoreBoard getInstance() {

		return Bestenliste.getInstance();
	}

	/**
	 * Speichert den Inhalt der Bestenliste in den Ausgabestrom ab. Die Methode
	 * schließt den Stream auf jeden Fall.
	 */
	void save(OutputStream out) throws Exception;

	/**
	 * Läd den Inhalt der Bestenliste im Format, das von {@link #save(OutputStream)}
	 * geschrieben wurde. Konnte die Bestenliste gelesen werden und ist sie valide,
	 * dann wird die aktuelle Bestenliste durch die Neue ersetzt. Andernfalls bleibt
	 * die aktuelle Bestenliste unverändert. Die Methode schließt den Stream auf
	 * jeden Fall.
	 */
	void load(InputStream in) throws Exception;

	/**
	 * Fügt einen neuen Eintrag in die Bestenliste ein. Der Zeitstempel ist
	 * {@code Instant.now()}.
	 *
	 * @param score der Punktestand des neuen Eintrags.
	 * @return der Platz des neuen Eintrags.
	 * @throws IllegalArgumentException wenn {@code score} keine nicht negative Zahl
	 *                                  ist.
	 */
	int add(double score);

	/**
	 * Gibt die Anzahl an Einträgen zurück.
	 */
	int size();

	/**
	 * Gibt den Zeitstempel für Eintrag {@code n} zurück.
	 * <p>
	 * Hinweis fürs Anzeigen der Zeitstempel: Dazu können Sie die
	 * {@link java.time.format.DateTimeFormatter}-Klasse verwenden. Instant
	 * repräsentiert einen Zeitpunkt. Um daraus eine menschenlesbare Zeichenfolge zu
	 * machen, ist die Zeitzone notwendig, in der das Datum angegeben werden soll.
	 * {@link Instant#toString()} verwendet dafür UTC. FÜr die lokale Zeitzone
	 * können Sie entweder dem Formatter sagen, in welcher Zeitzone formatiert
	 * werden soll ({@link DateTimeFormatter#withZone(ZoneId)}) oder {@link Instant}
	 * in ein {@link java.time.ZonedDateTime} umwandeln
	 * ({@link Instant#atZone(ZoneId)}). Die {@link ZoneId} der aktuellen Zeitzone
	 * erhalten Sie durch {@link ZoneId#systemDefault()}.
	 *
	 * @param n Index des Eintrags. Ist aus dem Interval {@code [0, size())}.
	 * @throws IndexOutOfBoundsException Falls {@code n} nicht aus
	 *                                   {@code [0, size())} stammt.
	 */
	Instant getInstant(int n);

	/**
	 * Gibt den Punktestand für Eintrag {@code n} zurück.
	 *
	 * @param n Index des Eintrags. Ist aus dem Interval {@code [0, size())}.
	 * @throws IndexOutOfBoundsException Falls {@code n} nicht aus
	 *                                   {@code [0, size())} stammt.
	 */
	double getScore(int n);

	/**
	 * Gibt den Platz für Eintrag {@code n} zurück.
	 *
	 * @param n Index des Eintrags. Ist aus dem Interval {@code [0, size())}.
	 * @throws IndexOutOfBoundsException Falls {@code n} nicht aus
	 *                                   {@code [0, size())} stammt.
	 */
	int getPlace(int n);

	/**
	 * Leert die Bestenliste.
	 */
	void clear();
}
