package cardmaster;


// java import
import java.util.Scanner;

import cardmaster.Game.Mode;

/**
 * Hilfsklasse zur Interaktion mit dem Spiel.
 */
public class Player {

	/**
	 * Wird aufgerufen, wenn mit dem Shop interagiert wird. Gibt entweder den Index
	 * ({@code 0 .. game.getShopItemCount()} des zu kaufenden Items zurück oder eine
	 * negative Zahl, wenn nichts weiter gekauft werden soll.
	 *
	 * @param game das Spiel.
	 * @return Der index des Items oder eine negative Zahl, um in den Playing-Modus
	 *         zu wechseln.
	 */
	public int shop(Game game) {
		
		game.setMode(Mode.SHOPPING);
		game.generateNewCardInShop();

		// hier wird mit shop interagiert
		
		// welches item willst du kaufen?
		for (int i = 0; i < game.getShopItemCount(); i++) {
			
			System.out.println(i + ": " + game.getShopItemDescription(i) + " - Preis: " + game.getShopItemPrice(i));
		}
		
		// ich will karte 2
		System.out.println("Welche Karte willst du kaufen?");
		Scanner inputScanner = new Scanner(System.in);
		int shopItemIndex = inputScanner.nextInt();
		inputScanner.close();
		
		if (shopItemIndex >= 0 && shopItemIndex <= game.getShopItemCount() && game.getCredits() >= game.getShopItemPrice(shopItemIndex)) {

			return shopItemIndex;
		} else if (shopItemIndex >= game.getShopItemCount()) {
			
			return shop(game); //Rekursiv, falls fa
		}

		return -1;
	}

	/**
	 * Es soll die nächste Karte gelegt werden. Verwenden Sie die Methoden in
	 * {@link Game}, um die Handkarten abzufragen und legen sie dann mit
	 * {@link Game#play(, int)} eine Karte.
	 *
	 * @param game das Spiel.
	 */
	public void playCard(Game game) {

		// ablagestapel müssen leer sein
		game.clearDiscardPile();

		// nachziehstapel mischen
		game.ziehstapelMischen();
		
		// Ausgabe der vorhanden Karten auf dem Nachziehstapel
		
		
		// abfrage, welche karte gespielt werden soll
		System.out.print("Welche Karte willst du spielen?: ");
		Scanner inputScanner = new Scanner(System.in);
		int cardIndex = inputScanner.nextInt();
		
		// abfrage, welcher ablagestapel
		System.out.print("Auf welchen Stapel willst du legen?: ");	
		int pileIndex = inputScanner.nextInt();
		inputScanner.close();
		
		// lege die abgefragte karte auf den gewählten ablegestapel

		if (game.getHandCardsCount() > 0) {
		
			game.play(game.getHandCard(cardIndex), pileIndex);
		}

		// berechne die punkte
		
		// wenn auf ziehstapel noch welche sind, automatisch in die hand
		if (!game.isDrawPileEmpty()) {

			
			// erste karte in die hand

			return;
		}
		
		// wenn letzte runde, beende spiel
		if (game.wasLastRound()) {
				
			game.setMode(Mode.END);
			return;
		}

		// wenn alle karten abgelegt, beende runde	
		game.setMode(Mode.SHOPPING);
	}
			
	/**
	 * Das Spiel ist zu Ende und Sie bekommen den endgültigen Punktestand
	 * mitgeteilt.
	 *
	 * @param credits der endgültige Punktestand.
	 */
	public void end(double credits) {
		
		System.out.println("ENDE >>>> PUNKTE: " + credits);
	}

	/**
	 * Wird mit einem neuen Spiel aufgerufen und spielt dann bis zum Ende.
	 *
	 * @param game das neue Spiel.
	 */
	public void run(Game game) {

		while (true) {
			
			int shopItemIndex;
			do {

				while (!game.isShopEmpty() && (shopItemIndex = shop(game)) >= 0
						&& shopItemIndex < game.getShopItemCount()) {

					game.buy(shopItemIndex);
				}

			} while (game.isDrawPileEmpty() && (shopItemIndex = shop(game)) >= 0); // Mindestens eine Karte ist notwendig

			game.endShopping();

			while (game.getMode() == Mode.PLAYING) {

				playCard(game);
			}

			if (game.getMode() == Mode.END) {

				end(game.getCredits());
				return;
			}
		}
	}

}
