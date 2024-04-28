package cardmaster;

import cardmaster.Upgrade.UpgradeDescriptions;
// java import
import cardmaster.cards.Card;
import cardmaster.interfaces.Item;

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

	// GAME INIT

	public void gameInit(int maxRounds) {

		if (maxRounds < 1) {
	
			throw new IllegalArgumentException("Invalid argument: maxRounds must be greater than 0, received: " + maxRounds);
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
	
	public void changeCredits(double d) {
		
		if(credits + d < 0) {
			
			return;
		}
		else {

			credits += d;
		}
	}

	/**
	 * Gibt den aktuellen Punktestand zurück.
	 */
	public double getCredits() {

		return credits;
	}

	/**
	 * Gibt den aktuellen Modus zurück.
	 */
	public Mode getMode() {
		
		return this.mode;
	}
	
	/**
	 * Setzt den aktuellen Modus.
	 */
	public void setMode(Mode newMode) {
		
		this.mode = newMode;
	}
	
	/**
	 * Gibt die Anzahl an Ablagestapel zurück.
	 */
	public int getStacksCount() {
		
		return maxDiscardPiles; 
	}
	
	// Methoden für den Shopping-Modus
	
	/**
	 * Gibt zurück, ob der Shop leer gekauft wurde.
	 */
	public boolean isShopEmpty() {
		
		return shop.isEmpty();
	}
	
	/**
	 * Gibt die Anzahl der noch im Shop verfügbaren Gegenstände an.
	 */
	public int getShopItemCount() {
		
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
		
		return shop.shopItemDescription(shopItemIndex);
	}
	
	/**
	 * Gibt den Preis des Gegenstands mit index {@code shopItemIndex} zurück.
	 *
	 * @param shopItemIndex index aus dem Intervall
	 *                      {@code [0, this.getShopItemCount())}.
	 */
	public int getShopItemPrice(int shopItemIndex) {
		
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
		
		if (this.shop.shopItemDescription(shopItemIndex) != null && credits >= this.shop.itemPrice(shopItemIndex) ) {
			
			Item item = this.shop.buy(shopItemIndex);
			boughtCardOnce = true;
			
			if (item instanceof Card) {
				
				this.drawPile.addCard((Card) item);
			} else {
				
				handleUpgrade((Upgrade) item);
			}
			
			
			this.credits -= item.getPrice();
			return true;
		}
		
		return false;
	}

	private void handleUpgrade(Upgrade item) {
		
		switch (item.getDescription()) {
			case ADD_HANDCARD:

				additionalHandCardPurchases++;
				// item.calcPrice(additionalHandCardPurchases);
				this.playerHand.sizeUpgrade();
				break;
			case ADD_SHOP:

				additionalCardInShopPurchases++;
				// item.calcPrice(additionalCardInShopPurchases);
				this.shop.sizeUpgrade();
				break;
			case ADD_STACK:
			
				additionalPlayStackPurchases++;
				// item.calcPrice(additionalPlayStackPurchases);
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
		
		return this.drawPile.isEmpty();
	}

	/**
	 * Füllt den Shop erneut mit fehlenden Karten entsprechend der currentMaxSize auf
	 * @see getSize
	 */
	private void refillShop() {

		this.shop.clearShopItems();
		this.shop.addItem(new Upgrade(UpgradeDescriptions.ADD_SHOP), additionalCardInShopPurchases);
		this.shop.addItem(new Upgrade(UpgradeDescriptions.ADD_STACK), additionalPlayStackPurchases);
		this.shop.addItem(new Upgrade(UpgradeDescriptions.ADD_HANDCARD), additionalHandCardPurchases);

		for (int i = this.shop.getItemCount(); i < this.shop.getSize(); i++) {

			this.shop.addItem(factory.createRandom(), this.credits);
			System.out.println(this.shop.shopItemDescription(i));
		}
	}
	
	/**
	 * Beendet die Shop-Interaktion und wechselt in den Playing-Modus. Wurde noch
	 * nie eine Karte gekauft, passiert nichts, da mindestens eine Karte fürs
	 * Spielen notwendig ist.
	 */
	public void endShopping() {
		
		if (!boughtCardOnce) return;
		
		shop.clearShopItems();
		
		for (int i = 0; i < this.discardPile.length; i++) {
			
			this.drawPile.addAllCards(this.discardPile[i].getAllCards());
			this.discardPile[i].clear();
		}
		
		this.drawPile.mischen();
		
		this.setMode(Mode.PLAYING);

		while (!this.isDrawPileEmpty() && this.playerHand.isNotFull()) {

			this.playerHand.addCard(this.drawPile.getTopCard());
		}

		if (currentRound >= maxRounds && this.getHandCardsCount() == 0) {
			this.setMode(Mode.END);
		}
	}

	// Methoden für den Playing-Modus

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
		
		// Throw falls karte nicht auf hand
		if (!this.playerHand.checkIfCardIsInHand(card)) {
		
			throw new IllegalArgumentException(card.toString() + " is not on hand!");
		}

		if (stackIndex >= this.discardPile.length || stackIndex < 0) {

			throw new IndexOutOfBoundsException("stackIndex: " + stackIndex + " is out of bounds. Valid indices are 0 to " + (this.discardPile.length - 1) + ".");
		}

		// die Karte auf einen pile legen
		this.discardPile[stackIndex].addCard(card);
		
		// credits ausrechnen
		this.changeCredits(card.calcCredits(this.discardPile));
		
		// Karte aus der Hand entfernen
		this.playerHand.playCard(card);
		//DEBUG System.out.println(card.toString() + " ausgespielt auf STACK: " + stackIndex);
		
		// wenn letzte runde, beenden
		if (currentRound >= maxRounds && this.getHandCardsCount() == 0) {

			this.setMode(Mode.END); // Beende das Spiel
		} else {
			// Wenn noch Karten im Nachziehstapel sind, ziehe nach
			while (!this.isDrawPileEmpty() && this.playerHand.isNotFull()) {

				this.playerHand.addCard(this.drawPile.getTopCard());
			}
			// Wenn keine Karten mehr auf der Hand sind, gehe zurück in den Shopping-Modus
			if (this.getHandCardsCount() == 0) {
				if(currentRound < maxRounds) {

					currentRound++; // Starte die nächste Runde
					for (int i = 0; i < this.discardPile.length; i++) {
			
						this.drawPile.addAllCards(this.discardPile[i].getAllCards());
						this.discardPile[i].clear();
					}
					this.setMode(Mode.SHOPPING);
					refillShop();
				} else {
					this.setMode(Mode.END); // Beende das Spiel, falls maxRounds erreicht sind
				}
			}
		}
	}


	/**
	 * Liefert die Form der auf den Ablagestapeln liegenden Karten. An Index
	 * {@code i} ist die Form für den Stapel mit index {@code i} oder {@code null}.
	 * {@code I} ist aus de Intervall {@code [0, this.getStacksCount())}.
	 */
	public Shape[] getTopShapes() {

		Shape [] topShapes = new Shape[this.discardPile.length];

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

	public enum Mode {

		SHOPPING, PLAYING, END;
	}

	public void addDiscardPile() {

		this.maxDiscardPiles++;
		DiscardPile[] temp = new DiscardPile[this.maxDiscardPiles];
		
		for (int i = 0; i < this.discardPile.length; i++) {

			temp[i] = this.discardPile[i];
		}

		temp[this.maxDiscardPiles - 1] = new DiscardPile();

		this.discardPile = temp;
	}

	public Card getDiscardPileTopCard(int index) {
		if (index < 0 || index >= discardPile.length || discardPile[index].isEmpty()) {

			return null;
		}
		return discardPile[index].getTopCard();
	}

	public int getCurrentRound() {
		
		return this.currentRound;
	}
}
