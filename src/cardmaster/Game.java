package cardmaster;

import cardmaster.cards.Card;

/**
 * Klasse für die Durchführung eines Spiels. Ein neues Spiel beginnt in Runde 1
 * im Modus {@link Mode#SHOPPING}. Nun muss mindestens eine Karte gekauft
 * werden. Dafür können die Methode für den Shopping-Modus (siehe unten)
 * verwendet werden. Der Nutzer dieser Klasse muss {@link #endShopping()}
 * aufrufen, um in den Playing-Modus ({@link Mode#PLAYING}) zu wechseln. Nun
 * werden die Methoden für den Playing-Modus verwendet, um wiederholt eine Karte
 * aus der Hand zu spielen ({@link #play(Card, int)}). Beim Wechseln in den
 * Playing-Modus und nach Legen einer Karte, wird die Hand wieder aufgefüllt,
 * sofern möglich. Wurde die letzte Karte aus der Hand gelegt, wird entweder,
 * falls das die letzte Runde war, in den End-Modus ({@link Mode#END})
 * gewechselt oder zurück in den Shopping-Modus. Beim Starten des Shopping-Modus
 * wird der Shop mit komplett zufälligen neuen Gegenständen gefüllt. Am Anfang
 * passen fünf Gegenstände in den Shop.
 */
public class Game {

	private double credits;
	
	/**
	 * Erzeugt ein neues Spiel.
	 *
	 * @param maxRounds Anzahl der Runden. Muss mindestens {@code 1} sein.
	 */
	public Game(int maxRounds) {
		// TODO
		this.credits = 10;
	}

	// Allgemein verwendbare Methoden.

	/**
	 * Gibt den aktuellen Punktestand zurück.
	 */
	public double getCredits() {
		
		return credits; // TODO
	}

	/**
	 * Gibt den aktuellen Modus zurück.
	 */
	public Mode getMode() {
		return null; // TODO
	}

	/**
	 * Gibt die Anzahl an Ablagestapel zurück.
	 */
	public int getStacksCount() {
		return 0; // TODO
	}

	// Methoden für den Shopping-Modus

	/**
	 * Gibt zurück, ob der Shop leer gekauft wurde.
	 */
	public boolean isShopEmpty() {
		return false; // TODO
	}

	/**
	 * Gibt die Anzahl der noch im Shop verfügbaren Gegenstände an.
	 */
	public int getShopItemCount() {
		return 0; // TODO
	}

	/**
	 * Gibt eine benutzerfreundliche Darstellung für die Konsole des Gegenstands mit
	 * index {@code shopItemIndex} zurück.
	 *
	 * @param shopItemIndex index aus dem Intervall
	 *                      {@code [0, this.getShopItemCount())}.
	 */
	public String getShopItemDescription(int shopItemIndex) {
		return null; // TODO
	}

	/**
	 * Gibt den Preis des Gegenstands mit index {@code shopItemIndex} zurück.
	 *
	 * @param shopItemIndex index aus dem Intervall
	 *                      {@code [0, this.getShopItemCount())}.
	 */
	public int getShopItemPrice(int shopItemIndex) {
		return 0; // TODO
	}

	/**
	 * Versucht den Gegenstand an index {@code shopItemIndex} zu kaufen und entfernt
	 * den Gegenstand bei Erfolg aus dem Sortiment.
	 * <p>
	 * Bei erfolgreichem Kauf können sich die Indexe der Gegenstände ändern. Alle
	 * vorherigen Rückgaben von {@link #getShopItemCount} und
	 * {@link #getShopItemDescription(int)} sind nicht mehr gültig.
	 *
	 * @param shopItemIndex index aus dem Intervall
	 *                      {@code [0, this.getShopItemCount())}.
	 * @return Ob der Gegenstand gekauft wurde. Bei einem zu geringen Punktestand
	 *         hingegen {@code false}.
	 */
	public boolean buy(int shopItemIndex) {
		return false; // TODO
	}

	/**
	 * Gibt zurück, ob auf dem Nachziehstapel keine Karte liegt.
	 * <p>
	 * Dies ist am Anfang des Spiels der Fall. Da diese Methode nur im Modus
	 * {@link Mode#SHOPPING} verwendet werden darf, ist die Rückgabe nach dem ersten
	 * Kartenkauf immer {@code true}.
	 */
	public boolean isDrawPileEmpty() {
		return false; // TODO
	}

	/**
	 * Beendet die Shop-Interaktion und wechselt in den Playing-Modus. Wurde noch
	 * nie eine Karte gekauft, passiert nichts, da mindestens eine Karte fürs
	 * Spielen notwendig ist.
	 */
	public void endShopping() {
		// TODO
	}

	// Methoden für den Playing-Modus

	/**
	 * Gibt zurück, wie viele Karten aktuell auf der Hand gehalten werden.
	 */
	public int getHandCardsCount() {
		return 0; // TODO
	}

	/**
	 * Gibt die Handkarte mit index {@code handCardIndex} zurück.
	 *
	 * @param handCardIndex index aus dem Intervall
	 *                      {@code [0, this.getHandCardsCount())}.
	 */
	public Card getHandCard(int handCardIndex) {
		return null; // TODO
	}

	/**
	 * Legt die Handkarte auf den Ablagestapel mit index {@code stackIndex}. Stellen
	 * Sie mit assert sicher, dass die Karte auch wirklich auf der Hand ist.
	 * <p>
	 * Die Indexe der Karten kann sich hierbei ändern. Vor allem wird eine Karte -
	 * wenn möglich - nachgezogen. Dadurch sind alle vorherigen Rückgaben von
	 * {@link #getHandCardsCount()} und {@link #getHandCard(int)} nicht mehr gültig.
	 *
	 * @param card       die zu legende Handkarte.
	 * @param stackIndex index aus dem Intervall {@code [0, this.getStacksCount())}.
	 */
	public void play(Card card, int stackIndex) {
		// TODO
	}

	/**
	 * Liefert die Form der auf den Ablagestapeln liegenden Karten. An Index
	 * {@code i} ist die Form für den Stapel mit index {@code i} oder {@code null}.
	 * {@code I} ist aus de Intervall {@code [0, this.getStacksCount())}.
	 */
	public Shape[] getTopShapes() {
		return null; // TODO
	}

	public enum Mode {
		SHOPPING, PLAYING, END;
	}
}
