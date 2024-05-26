package cardmaster;

// java import
import cardmaster.cards.Card;
import cardmaster.collections.AlgoArrayList;
import cardmaster.interfaces.Item;

import cardmaster.Upgrade.UpgradeDescriptions;

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

	// UPGRADES
	private int additionalCardInShopPurchases;
	private int additionalPlayStackPurchases;
	private int additionalHandCardPurchases;

	private int maxDiscardPiles = 3;

	private int currentRound;
	private int maxRounds;
	private boolean boughtCardOnce;
	private double credits;

	private Mode mode;
	private Shop shop;
	private Hand playerHand;

	private DrawPile drawPile;
	private DiscardPile[] discardPile;

	private CardFactory factory;

	/**
	 * Erzeugt ein neues Spiel.
	 * 
	 * @param maxRounds Anzahl der Runden. Muss mindestens {@code 1} sein.
	 */
	public Game(int maxRounds) {

		this(maxRounds, new CardFactory()); // Nutzt die Basis-CardFactory

	}

	public Game(int maxRounds, CardFactory factory) {

		gameInit(maxRounds);
		this.factory = factory;
		refillShop();
	}

	/**
	 * Initialisiert das Spiel mit allen nötigen Komponenten und Konfigurationen
	 * 
	 * @param maxRounds
	 */
	public void gameInit(int maxRounds) {

		if (maxRounds < 1) {

			throw new IllegalArgumentException(
					"Invalid argument: maxRounds must be greater than 0, received: " + maxRounds);
		}

		this.maxRounds = maxRounds;
		this.currentRound = 1;
		this.credits = 10;
		this.boughtCardOnce = false;

		this.mode = Mode.SHOPPING;
		this.shop = new Shop();

		this.playerHand = new Hand();
		this.drawPile = new DrawPile();
		this.discardPile = new DiscardPile[3];

		this.additionalCardInShopPurchases = 0;
		this.additionalPlayStackPurchases = 0;
		this.additionalHandCardPurchases = 0;

		for (int i = 0; i < this.discardPile.length; i++) {

			this.discardPile[i] = new DiscardPile();
		}

		this.factory = new CardFactory();
		refillShop();

	}

	// Allgemein verwendbare Methoden.

	/**
	 * Ändert die credits des Spielers mit dem übergebenen Parameter
	 * 
	 * @param d
	 */
	public void changeCredits(double d) {

		if (credits + d < 0) {

			return;
		} else {

			credits += d;
		}
	}

	/**
	 * @return Aktuellen Punktestand des Spielers.
	 */
	public double getCredits() {

		return credits;
	}

	/**
	 * @return Gibt den aktuellen Modus des Spiels zurück.
	 */
	public Mode getMode() {

		return this.mode;
	}

	/**
	 * Setzt den aktuellen Modus des Spiels auf den neuen Modus.
	 * 
	 * @param newMode
	 */
	public void setMode(Mode newMode) {

		this.mode = newMode;
	}

	/**
	 * 
	 * @return Gibt die Anzahl an Ablagestapel zurück.
	 */
	public int getStacksCount() {

		return maxDiscardPiles;
	}

	// Methoden für den Shopping-Modus

	/**
	 * Gibt zurück, ob der Shop leer gekauft wurde.
	 * 
	 * @return boolean
	 */
	public boolean isShopEmpty() {

		if (this.mode != Mode.SHOPPING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.SHOPPING);
		}

		return shop.isEmpty();
	}

	/**
	 * @return Die Anzahl der noch im Shop verfügbaren Gegenstände an.
	 */
	public int getShopItemCount() {

		if (this.mode != Mode.SHOPPING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.SHOPPING);
		}

		return shop.getItemCount();
	}

	/**
	 * Gibt eine benutzerfreundliche Darstellung für die Konsole des Gegenstands mit
	 * index {@code shopItemIndex} zurück.
	 *
	 * @param shopItemIndex index aus dem Intervall
	 *                      {@code [0, this.getShopItemCount())}.
	 */
	public String getShopItemDescription(int shopItemIndex) {

		if (this.mode != Mode.SHOPPING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.SHOPPING);
		}

		return shop.shopItemDescription(shopItemIndex);
	}

	/**
	 * Gibt den Preis des Gegenstands mit index {@code shopItemIndex} zurück.
	 *
	 * @param shopItemIndex index aus dem Intervall
	 *                      {@code [0, this.getShopItemCount())}.
	 */
	public int getShopItemPrice(int shopItemIndex) {

		if (this.mode != Mode.SHOPPING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.SHOPPING);
		}

		return shop.itemPrice(shopItemIndex);
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

		if (this.mode != Mode.SHOPPING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.SHOPPING);
		}

		

		if (this.shop.shopItemDescription(shopItemIndex) != null && credits >= this.shop.itemPrice(shopItemIndex)) {

			Item item = this.shop.buy(shopItemIndex);
			boughtCardOnce = true;

			if (item instanceof Card && this.playerHand.getMaxHandCards() > this.drawPile.size()) {

				this.drawPile.addCard((Card) item);
			} else if (item instanceof Upgrade) {

				handleUpgrade((Upgrade) item);
			} else {
				return false;
			}

			this.credits -= item.getPrice();
			return true;
		}

		return false;
	}

	/**
	 * Führt den Effekt des gekauften Upgrades aus, dafür wird das jeweile Upgrade
	 * als Parameter übergeben
	 * 
	 * @param item
	 */
	private void handleUpgrade(Upgrade item) {

		switch (item.getDescription()) {
			case ADD_HANDCARD:

				additionalHandCardPurchases++;
				this.playerHand.sizeUpgrade();
				break;
			case ADD_SHOP:

				additionalCardInShopPurchases++;
				this.shop.sizeUpgrade();
				break;
			case ADD_STACK:

				additionalPlayStackPurchases++;
				addDiscardPile();
				break;
		}
	}

	/**
	 * Gibt zurück, ob auf dem Nachziehstapel keine Karte liegt.
	 * <p>
	 * Dies ist am Anfang des Spiels der Fall. Da diese Methode nur im Modus
	 * {@link Mode#SHOPPING} verwendet werden darf, ist die Rückgabe nach dem ersten
	 * Kartenkauf immer {@code true}.
	 */
	public boolean isDrawPileEmpty() {

		if (this.mode != Mode.SHOPPING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.SHOPPING);
		}

		return this.drawPile.isEmpty();
	}

	/**
	 * Füllt den Shop erneut mit fehlenden Karten entsprechend der currentMaxSize
	 * auf
	 * 
	 * @see getSize
	 */
	private void refillShop() {

		this.shop.clearShopItems();
		this.shop.addItem(new Upgrade(UpgradeDescriptions.ADD_SHOP), additionalCardInShopPurchases);
		this.shop.addItem(new Upgrade(UpgradeDescriptions.ADD_STACK), additionalPlayStackPurchases);
		this.shop.addItem(new Upgrade(UpgradeDescriptions.ADD_HANDCARD), additionalHandCardPurchases);

		for (int i = this.shop.getItemCount(); i < this.shop.getSize(); i++) {

			this.shop.addItem(factory.createRandom(), this.credits);
		}
	}

	/**
	 * Beendet die Shop-Interaktion und wechselt in den Playing-Modus. Wurde noch
	 * nie eine Karte gekauft, passiert nichts, da mindestens eine Karte fürs
	 * Spielen notwendig ist.
	 */
	public void endShopping() {

		if (this.mode != Mode.SHOPPING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.SHOPPING);
		}

		if (!boughtCardOnce)
			return;

		shop.clearShopItems();

		for (int i = 0; i < this.discardPile.length; i++) {

			this.drawPile.addAllCards(this.discardPile[i].getAllCards());
			this.discardPile[i].clear();
		}

		this.drawPile.mischen();

		while (!this.isDrawPileEmpty() && this.playerHand.isNotFull()) {

			this.playerHand.addCard(this.drawPile.getTopCard());
		}

		this.setMode(Mode.PLAYING);

		if (currentRound >= maxRounds && this.getHandCardsCount() == 0) {
			this.setMode(Mode.END);
		}

	}

	// Methoden für den Playing-Modus

	/**
	 * Gibt zurück, wie viele Karten aktuell auf der Hand gehalten werden.
	 */
	public int getHandCardsCount() {

		if (this.mode != Mode.PLAYING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.PLAYING);
		}

		return this.playerHand.getHandCardsCount();
	}

	/**
	 * Gibt die Handkarte mit index {@code handCardIndex} zurück.
	 *
	 * @param handCardIndex index aus dem Intervall
	 *                      {@code [0, this.getHandCardsCount())}.
	 */
	public Card getHandCard(int handCardIndex) {

		if (this.mode != Mode.PLAYING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.PLAYING);
		}

		return this.playerHand.getHandCardAtIndex(handCardIndex);
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

		if (this.mode != Mode.PLAYING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.PLAYING);
		}

		// Throw falls karte nicht auf hand
		if (!this.playerHand.checkIfCardIsInHand(card)) {

			throw new IllegalArgumentException(card.toString() + " is not on hand!");
		}

		if (stackIndex >= this.discardPile.length || stackIndex < 0) {

			throw new IndexOutOfBoundsException("stackIndex: " + stackIndex
					+ " is out of bounds. Valid indices are 0 to " + (this.discardPile.length - 1) + ".");
		}

		// die Karte auf einen pile legen
		this.discardPile[stackIndex].addCard(card);

		// credits ausrechnen
		this.changeCredits(card.calcCredits(this.discardPile));

		// Karte aus der Hand entfernen
		this.playerHand.playCard(card);

		// wenn letzte runde, beenden
		if (currentRound >= maxRounds && this.getHandCardsCount() == 0) {

			this.setMode(Mode.END); // Beende das Spiel
			this.saveScore();
		} else {
			// Wenn noch Karten im Nachziehstapel sind, ziehe nach
			while (!this.drawPile.isEmpty() && this.playerHand.isNotFull()) {

				this.playerHand.addCard(this.drawPile.getTopCard());
			}
			// Wenn keine Karten mehr auf der Hand sind, gehe zurück in den Shopping-Modus
			if (this.getHandCardsCount() == 0) {
				if (currentRound < maxRounds) {

					currentRound++; // Starte die nächste Runde
					for (int i = 0; i < this.discardPile.length; i++) {

						this.drawPile.addAllCards(this.discardPile[i].getAllCards());
						this.discardPile[i].clear();
					}
					this.setMode(Mode.SHOPPING);
					refillShop();
				} else {
					this.setMode(Mode.END); // Beende das Spiel, falls maxRounds erreicht sind
					this.saveScore();
				}
			}
		}
	}

	/**
	 * Erstellt ein Eintrag in das Scoreboard mit den Credits des Spielers
	 */
	private void saveScore() {

		ScoreBoard scoreBoard = ScoreBoard.getInstance();

		scoreBoard.add(this.credits);
	}

	/**
	 * Liefert die Form der auf den Ablagestapeln liegenden Karten. An Index
	 * {@code i} ist die Form für den Stapel mit index {@code i} oder {@code null}.
	 * {@code I} ist aus de Intervall {@code [0, this.getStacksCount())}.
	 */
	public Shape[] getTopShapes() {

		if (this.mode != Mode.PLAYING) {

			throw new IllegalCallException(Game.getCurrentMethodName(), this.mode, Mode.PLAYING);
		}

		Shape[] topShapes = new Shape[this.discardPile.length];

		for (int i = 0; i < this.discardPile.length; i++) {

			DiscardPile pile = this.discardPile[i];

			if (pile.isEmpty()) {

				topShapes[i] = null;
			} else {

				topShapes[i] = pile.getTopShape();
			}
		}

		return topShapes;
	}

	/**
	 * ENUMs für die Modes des Spiels
	 */
	public enum Mode {

		SHOPPING, PLAYING, END;
	}

	/**
	 * Fügt einen zusätzlichen Ablagestapel hinzu.
	 * Beispiel: Man kauft das jeweilige Upgrade -> Ablagestapel + 1
	 */
	public void addDiscardPile() {

		this.maxDiscardPiles++;
		DiscardPile[] temp = new DiscardPile[this.maxDiscardPiles];

		for (int i = 0; i < this.discardPile.length; i++) {

			temp[i] = this.discardPile[i];
		}

		temp[this.maxDiscardPiles - 1] = new DiscardPile();

		this.discardPile = temp;
	}

	/**
	 * Gibt eine Karte des festgelegten Ablagestapels zurück.
	 * Der Ablagestapel wird über den Index angesteuert
	 * 
	 * @param index
	 * @return Oberste Karte des Ablagestapels
	 */
	public Card getDiscardPileTopCard(int index) {
		if (index < 0 || index >= discardPile.length || discardPile[index].isEmpty()) {

			return null;
		}
		return discardPile[index].getTopCard();
	}

	/**
	 * Liefert alle Items aus dem Shop zurück
	 * 
	 * @return Alle Items aus dem Shop als Array
	 */
	public Object[] getAllShopItems() {

		return this.shop.getAllItems();
	}

	public Object[] getAllHandCards() {

		AlgoArrayList<Object> allCardsList = new AlgoArrayList<>();

		for (Card card : playerHand.getAllHandCards()) {

			allCardsList.add(card);
		}

		for (Card card : drawPile.getAllDrawCards()) {

			allCardsList.add(card);
		}

		return allCardsList.toArray();
    }

	public int getAllHandCardsCount(Object[] cards) {

		return cards.length;
	}

	public int getAllHandCardsMax() {

		return this.playerHand.getMaxHandCards();
	}

	public DiscardPile[] getDiscardPiles() {

		return this.discardPile;
	}

	/**
	 * 
	 * @return aktuelle runde im Spiel
	 */
	public int getCurrentRound() {

		return this.currentRound;
	}

	/**
	 * 
	 * @return Aktuelle Methode als Name für die Exception
	 */
	private static String getCurrentMethodName() {

		return Thread.currentThread().getStackTrace()[1].toString();
	}

    public int maxRounds() {
        
		return this.maxRounds;
    }
}
