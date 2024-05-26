package cardmaster;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TestCredits {

	/*
	 * Zum Testen wird das Framework JUnit in Version 4 verwendet. Dies durchsucht
	 * alle Klasse im test-Ordner nach Methoden, vor denen @Test steht. Jede dieser
	 * Methoden ist dann ein Test. Der Test ist bestanden, wenn die Methode bis zum
	 * Ende ausgeführt werden kann. Mit assert* wird die Ausführung unterbrochen,
	 * falls etwas nicht stimmt. assertEquals überprüft, ob beide Werte gleich sind.
	 * Bei Gleitkommazahlen müssen diese nur ähnlich genug sein, was über den
	 * dritten Parameter eingestellt wird. Zusätzlich müssen alle Tests in einer
	 * bestimmten Zeit abgeschlossen werden. Es geht nicht darum, dass Ihr Code
	 * besonders schnell sein muss, sondern dass darin keine Endlosschleifen sind.
	 * Diese Regel wird in jeder Testklasse angegeben. Die Zeit gilt dann für jede
	 * Methode einzeln.
	 *
	 * Falls Sie sich selber mit JUnit für eigene Tests befassen, achten Sie darauf,
	 * dass es mittlerweile auch JUnit 5 gibt, wo vieles neues hinzugekommen ist,
	 * was Sie in JUNit 4 nicht verwenden können.
	 */
	@Rule
	public Timeout globalTimeout = Timeout.millis(100);

	@Test
	public void initialCredits() {
		assertEquals("Player starts with 10 credits", 10.0, new Game(1).getCredits(), 1E-12);
	}
}
