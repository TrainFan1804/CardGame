package cardmaster;

import static cardmaster.TestUtils.createGameWithChanceCard;

import java.util.HashSet;
import java.util.Set;

// custom import
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

	public static void main(String[] args) {
		
		// Game g = new Game(10);

		// g.buy(1);
		// g.endShopping();
		// g.play(g.getHandCard(0), 0);

		final var game = createGameWithChanceCard(1000, true);

		//Game game = new Game(10);
		while (game.getMode() != Mode.END) {
			// Runden werden gespielt
			
			game.endShopping();
			game.play(game.getHandCard(0), 0);

		}
	}

	private final static int MAXDISCARDPILES = 3;

	private int currentRound;
	private int maxRounds;
	private double credits;
	
	private Mode mode;
	private Shop shop;
	private Hand playerHand;
	private DrawPile drawPile;
	private Ablagestapel[] discardPile;

	private boolean boughtOnce;

	/**
	 * Erzeugt ein neues Spiel.
	 *
	 * @param maxRounds Anzahl der Runden. Muss mindestens {@code 1} sein.
	 */
	public Game(int maxRounds) {

		if (maxRounds <= 0) {
		
			throw new IllegalArgumentException("Invalid argument: maxRounds must be greater than 0, received " + maxRounds);
		}

		this.currentRound = 1;
		this.maxRounds = maxRounds;
		this.credits = 10.0;

		this.mode = Mode.SHOPPING;
		this.shop = new Shop();
		this.shop.generateNewCards((int)this.credits);

		this.playerHand = new Hand();
		this.drawPile = new DrawPile();
		this.discardPile = new Ablagestapel[MAXDISCARDPILES];
		for (int i = 0; i < this.discardPile.length; i++) {
			
			this.discardPile[i] = new Ablagestapel();
		}
	}

	//////////////////////////////////////
	//	Allgemein verwendbare Methoden.	//
	//////////////////////////////////////

	/**
	 * Gibt zurück, ob es die letzte Runde war
	 * 
	 * @return {@code true} wenn es die letzte Runde war, sonst {@code false}
	 */
	public boolean wasLastRound() {

		return currentRound == maxRounds;
	}

	/**
	 * Gibt den aktuellen Punktestand zurück.
	 */
	public double getCredits() {
		
		return this.credits;
	}

	/**
	 * Gibt den aktuellen Modus zurück.
	 */
	public Mode getMode() {
		
		return this.mode;
	}

	/**
	 * Ändert den aktuellen Modus.
	 * 
	 * @param mode Den neuen Modus
	 */
	public void setMode(Mode mode) {
		
		this.mode = mode;
	}

	//////////////////////////////
	//	Methoden für die Piles	//
	//////////////////////////////

	public void clearDiscardPile() {

		for (int i = 0; i < this.discardPile.length; i++) {

			for (int j = 0; j < this.discardPile[i].size(); j++) {

				this.drawPile.addCard(this.discardPile[i].removeLast());
			}
		}
	}

	public void ziehstapelMischen() {

		this.drawPile.mischen();
	}

	private void drawCardFromPileIntoHand() {

		// fülle die Hand mit den 4 oberen Karten vom Drawpile
		while (!this.isDrawPileEmpty() && this.playerHand.isNotFull()) {

			this.playerHand.addCard(this.drawPile.removeLast());
		}
	}

	/**
	 * Rechnet aus, wieviele piles leer sind.
	 * <p>
	 * Für die Punkte berechnung wichtig
	 * 
	 * @return Die Anzahl der piles die leer sind
	 */
	private double calcNotEmptyPiles() {

		double count = 0;

		for (Pile p : this.discardPile) {

			if (!p.isEmpty()) {

				count += 0.5;  
			}
		}

		return count;
	}

	/**
	 * Gibt zurück, ob auf dem Nachziehstapel keine Karte liegt.
	 * <p>
	 * Dies ist am Anfang des Spiels der Fall. Da diese Methode nur im Modus
	 * {@link Mode#SHOPPING} verwendet werden darf, ist die Rückgabe nach dem ersten
	 * Kartenkauf immer {@code true}.
	 */
	public boolean isDrawPileEmpty() {

		return this.drawPile.isEmpty();
	}

	/**
	 * Gibt die Anzahl an Ablagestapel zurück.
	 */
	public int getStacksCount() {

		return this.discardPile.length;
	}

	/**
	 * Liefert die Form der auf den Ablagestapeln liegenden Karten. An Index
	 * 
	 * {@code i} ist die Form für den Stapel mit index {@code i} oder {@code null}.
	 * {@code I} ist aus de Intervall {@code [0, this.getStacksCount())}.
	 */
	public Shape[] getTopShapes() {

		int count = 0;
		Shape[] temp = new Shape[this.discardPile.length];

		for (Ablagestapel p : discardPile) {

			if (!p.isEmpty()) {

				temp[count] = p.getTopShape();
				count++;
			}
		}

		return temp;
	}

	//////////////////////////////////////
	//	Methoden für den Shopping-Modus	//
	//////////////////////////////////////

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
		
		
		if (this.credits >= this.shop.getCardPrice(shopItemIndex)) {
			
			Card card = this.shop.buy(shopItemIndex);

			boughtOnce = true;
			this.drawPile.addCard(card);
			this.credits -= card.getPrice();
			System.out.println("Karte: " + shopItemIndex + ": " + card.toString() + " für " + card.getPrice() + " gekauft.");

			return true;
		}

		return false;
	}

	/**
	 * Beendet die Shop-Interaktion und wechselt in den Playing-Modus. Wurde noch
	 * nie eine Karte gekauft, passiert nichts, da mindestens eine Karte fürs
	 * Spielen notwendig ist.
	 */
	public void endShopping() {
		
		if (!boughtOnce) {
		
			System.out.println("BUY ATLEAST ONE CARD DU LAPPEN");
			return;
		}

		shop.clearShop();
		this.startPlaying();
	}

	/**
	 * Gibt zurück, ob der Shop leer gekauft wurde.
	 */
	public boolean isShopEmpty() {

		return this.shop.isShopEmpty();
	}

	/**
	 * Gibt die Anzahl der noch im Shop verfügbaren Gegenstände an.
	 */
	public int getShopItemCount() {
		
		return this.shop.getShopItemCount();
	}

	/**
	 * Gibt eine benutzerfreundliche Darstellung für die Konsole des Gegenstands mit
	 * index {@code shopItemIndex} zurück.
	 *
	 * @param shopItemIndex index aus dem Intervall
	 *                      {@code [0, this.getShopItemCount())}.
	 */
	public String getShopItemDescription(int shopItemIndex) {

		return this.shop.getCardDescription(shopItemIndex);
	}

	/**
	 * Gibt den Preis des Gegenstands mit index {@code shopItemIndex} zurück.
	 *
	 * @param shopItemIndex index aus dem Intervall
	 *                      {@code [0, this.getShopItemCount())}.
	 */
	public int getShopItemPrice(int shopItemIndex) {

		// return this.shop.calcCardPrice(this.credits);
		return this.shop.getCardPrice(shopItemIndex);
	}

	//////////////////////////////////////
	//	Methoden für den Playing-Modus	//
	//////////////////////////////////////

	private void startPlaying() {
		
		this.clearDiscardPile();
		this.setMode(Mode.PLAYING);

		// while (this.mode == Mode.PLAYING) {

		// }
		
		this.drawCardFromPileIntoHand();
		
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
		
		//Throw falls karte nicht auf hand
		if (cardIsNotOnHand(card)) {
		
			throw new IllegalArgumentException(card.toString() + " is not on hand!");
		}

		if (stackIndexOutOfBounds(stackIndex)) {

			throw new IndexOutOfBoundsException("stackIndex: " + stackIndex + " is out of bounds. Valid indices are 0 to " + (this.discardPile.length - 1) + ".");
		}

		// die Karte auf einen pile legen
		this.discardPile[stackIndex].addCard(card);
		// System.out.println(card.toString() + "gelegt auf ablagestapel: " + stackIndex + "mit der länge: " + this.discardPile.length);
		
		// credits ausrechnen
		this.calcCredits();
		
		this.playerHand.removeCard(card);
		this.playerHand.sortCardsOnHand();
		
		// wenn letzte runde, beenden
		if (maxRounds == currentRound && this.getHandCardsCount() == 0) {
		
			this.setMode(Mode.END);
		} else if (this.drawPile.getTopCard() != null && this.getHandCardsCount() < 4) {
			
			this.drawCardFromPileIntoHand();	
		} else if (this.getHandCardsCount() == 0) {
			
			this.setMode(Mode.SHOPPING);
			this.shop.generateNewCards((int)this.credits);
			currentRound++; // Runde ist erst vorbei, wenn SHOPPING beginnt
		}
	}

	private boolean stackIndexOutOfBounds(int stackIndex) {
		
		if (stackIndex < 0 || stackIndex >= this.discardPile.length) { // Annahme, dass discardPile ein Array ist
			
			return true;
		}

		return false;
	}

	private boolean cardIsNotOnHand(Card card) {
		
		for (int i = 0; i < this.playerHand.getHandCardsCount(); i++) {

			if (this.playerHand.getHandCard(i).equals(card)) {
			
				return false;
			}
		}

		return true;
	}

	private void calcCredits() {
 
		// ausrechnen, wie viele piles leer sind (nur für Chance Karten, siehe Aufgabe)
		double newPoints = this.calcNotEmptyPiles();
		
		// ausrechnen, wie viele einzigartigen shapes oben liegen
		Set<Shape> specialSet = new HashSet<>();
		Shape[] topShapes = this.getTopShapes();
		
		for (Shape s : topShapes) {

			if (s != null) {

				specialSet.add(s);
			}
		}

		newPoints += 0.5 * (double) specialSet.size();
		this.credits += newPoints;
	}

	/**
	 * Gibt zurück, wie viele Karten aktuell auf der Hand gehalten werden.
	 */
	public int getHandCardsCount() {

		return this.playerHand.getHandCardsCount();
	}

	/**
	 * Gibt die Handkarte mit index {@code handCardIndex} zurück.
	 *
	 * @param handCardIndex index aus dem Intervall
	 *                      {@code [0, this.getHandCardsCount())}.
	 */
	public Card getHandCard(int handCardIndex) {

		if (handCardIndex < 0 || handCardIndex > this.playerHand.getHandCardsCount()) {

			throw new IndexOutOfBoundsException("CardIndex: " + handCardIndex + " is out of bounds for limits: " + -1 + " -> " + this.playerHand.getHandCardsCount() + 1);
		}

		return this.playerHand.getHandCard(handCardIndex);
	}

	public enum Mode {

		SHOPPING, PLAYING, END;
	}
	
}
