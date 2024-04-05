package cardmaster;

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
		return -1;
	}

	/**
	 * Es soll die nächste Karte gelegt werden. Verwenden Sie die Methoden in
	 * {@link Game}, um die Handkarten abzufragen und legen sie dann mit
	 * {@link Game#play(Card, int)} eine Karte.
	 *
	 * @param game das Spiel.
	 */
	public void playCard(Game game) {
	}

	/**
	 * Das Spiel ist zu Ende und Sie bekommen den endgültigen Punktestand
	 * mitgeteilt.
	 *
	 * @param credits der endgültige Punktestand.
	 */
	public void end(double credits) {
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
			} while (game.isDrawPileEmpty()); // Mindestens eine Karte ist notwendig
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
