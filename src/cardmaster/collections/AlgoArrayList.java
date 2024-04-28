package cardmaster.collections;

import java.util.Random;
import cardmaster.interfaces.Item;

/**
 * Diese Klasse liefert eine dynamische ArrayList mit der Standartgroesse 4. Die Klasse wird von {@link cardmaster.Hand}
 * und der unterklassen von {@link cardmaster.Pile} verwendet.
 * 
 * @author ole
 * @since 12.03.2024
 */
public class AlgoArrayList {
	
	private int currentSize;
	private Item[] items;
	
	/**
	 * Erstelle eine neue AlgoArrayList mit der groesse 4.
	 */
	public AlgoArrayList(int size) {
		
		this.items = new Item[size];
	}
	
	/**
	 * Fuegt ein neues Item in die ArrayList hinzu. Falls das Array bereits voll ist, wird das Array um 1 erweitert
	 * 
	 * @param newItem Das Item, das neu in die ArrayList hinzugefuegt wird.
	 */
	public void add(Item newItem) {
		
		if (this.currentSize == this.items.length) {
			
			Item[] temp = new Item[this.items.length + 1];
			
			for (int i = 0; i < this.items.length; i++) {
				
				temp[i] = this.items[i];
			}

			this.items = temp;
		}
		
		this.items[this.currentSize++] = newItem;
	}

	/**
	 * Gibt ein Item an dem uebergebenen Index zurueck.
	 * 
	 * @param index Der uebergebene Index.
	 * @return Das Item an dem uebergebenen Index. Falls der Index nicht in der ArrayList liegt, wird {@code null} zurueck gegeben.
	 */
	public Item getItemAtIndex(int index) {
		
		if (index >= this.currentSize) {

			return null;
		}

		return this.items[index];
	}
	
	/**
	 * Gibt den aktuellen Fuellstand zurueck.
	 */
	public int size() {
		
		return this.currentSize;
	}

	/**
	 * Loescht ein Item aus der ArrayList.
	 * 
	 * @param item Das Item, das geloescht werden soll.
	 * @return Gibt {@code true} zurueck, wenn das Item erfolgreich geloescht wurde. 
	 * 			Gibt {@code false} zurueck, wenn das Item nicht geloescht wurden konnte. Dies tritt auf, wenn
	 * 			das Item nicht in der ArrayList ist.
	 */
	public boolean delete(Item item) {

		int index = this.getIndexFromItem(item);

		return this.delete(index);
	} 

	/**
	 * Loescht ein Item aus der ArrayList mithilfe eines uebergebenen Index.
	 * 
	 * @param index Der Index des Item, das geloescht werden soll.
	 * @return Gibt {@code true} zurueck, wenn das Item erfolgreich geloescht wurde. 
	 * 			Gibt {@code false} zurueck, wenn das Item nicht geloescht wurden konnte. Dies tritt auf, wenn
	 * 			das Item nicht in der ArrayList ist.
	 */
	public boolean delete(int index) {
		
		if (index < 0) {
			
			return false;
		}
		
		for (int i = index; i < this.items.length - 1; i++) {
			
			this.items[i] = this.items[i + 1];
		}
		this.items[this.items.length - 1] = null;
		
		this.currentSize--;
		return true;
	}
	
	/**
	 * Liefert den Index ein Item in der ArrayList zuruck. Wird intern von der {@link #delete(Item)} genutzt.
	 * 
	 * @param item Das Item, das auf ihren Index geprueft wird.
	 * @return Den Index des uebergebenen Item. Falls die Karte nicht in der ArrayList vorhanden ist, wird {@code -1} zurueck gegeben.
	 */
	public int getIndexFromItem(Item item) {

		for (int i = 0; i < this.currentSize; i++) {

			if (this.items[i].equals(item)) {

				return i;
			}
		}

		return -1;
	}

	/**
	 * Loescht alle Elemente aus der ArrayList
	 */
	public void clear() {

		this.items = new Item[this.items.length];
		this.currentSize = 0;
	}

	public Item[] toArray() {

		Item[] temp = new Item[this.currentSize];
		
		for (int i = 0; i < this.items.length; i++) {

			if (this.items[i] != null) {

				temp[i] = this.items[i];
			}
		}

		return temp;
	}

	void shuffle() {

		int index;
        Item temp;
        Random random = new Random();
        for (int i = this.currentSize - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = this.items[index];
            this.items[index] = this.items[i];
            this.items[i] = temp;
        }

	}

}
