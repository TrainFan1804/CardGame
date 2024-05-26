package cardmaster.collections;

import java.util.Random;
import java.util.Iterator;
import java.util.NoSuchElementException;

import cardmaster.interfaces.Item;

/**
 * Diese Klasse liefert eine dynamische ArrayList mit der Standartgroesse 4. Die Klasse wird von {@link cardmaster.Hand}
 * und der unterklassen von {@link cardmaster.Pile} verwendet.
 * 
 * @author ole
 * @since 12.03.2024
 */

public class AlgoArrayList<T> implements Iterable<T> {
	
	private int currentSize;
	private Object[] data;
	
	public AlgoArrayList() {

		this(100);
	}
	
	/**
	 * Erstelle eine neue AlgoArrayList mit der groesse 4.
	 */
	public AlgoArrayList(int size) {
		
		this.data = new Object[size];
	}
	
	/**
	 * Fuegt ein neues Item in die ArrayList hinzu. Falls das Array bereits voll ist, wird das Array um 1 erweitert
	 * 
	 * @param newItem Das Item, das neu in die ArrayList hinzugefuegt wird.
	 */
	public void add(Object newItem) {
		
		if (this.currentSize == this.data.length) {
			
			Object[] temp = new Object[this.data.length + 1];
			
			for (int i = 0; i < this.data.length; i++) {
				
				temp[i] = this.data[i];
			}

			this.data = temp;
		}
		
		this.data[this.currentSize++] = newItem;
	}

	/**
	 * Gibt ein Item an dem uebergebenen Index zurueck.
	 * 
	 * @param index Der uebergebene Index.
	 * @return Das Item an dem uebergebenen Index. Falls der Index nicht in der ArrayList liegt, wird {@code null} zurueck gegeben.
	 */
	public Object getItemAtIndex(int index) {

		if (index >= this.currentSize || index < 0) {

			throw new IndexOutOfBoundsException();
		}

		return this.data[index];
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
	public boolean delete(Object item) {

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
		
		for (int i = index; i < this.data.length - 1; i++) {
			
			this.data[i] = this.data[i + 1];
		}
		this.data[this.data.length - 1] = null;
		
		this.currentSize--;
		return true;
	}
	
	/**
	 * Liefert den Index ein Item in der ArrayList zuruck. Wird intern von der {@link #delete(Item)} genutzt.
	 * 
	 * @param item Das Item, das auf ihren Index geprueft wird.
	 * @return Den Index des uebergebenen Item. Falls die Karte nicht in der ArrayList vorhanden ist, wird {@code -1} zurueck gegeben.
	 */
	public int getIndexFromItem(Object item) {

		for (int i = 0; i < this.currentSize; i++) {

			if (this.data[i].equals(item)) {

				return i;
			}
		}

		return -1;
	}

	/**
	 * Loescht alle Elemente aus der ArrayList
	 */
	public void clear() {

		this.data = new Object[this.data.length];
		this.currentSize = 0;
	}

	/**
	 * Erstellt ein Object Array. Das geliferte Array ist eine Kopie
	 * 
	 * @return Eine Array representation von dem internen array
	 */
	public Object[] toArray() {

		Object[] temp = new Object[this.currentSize];
		
		for (int i = 0; i < this.data.length; i++) {

			if (this.data[i] != null) {

				temp[i] = this.data[i];
			}
		}

		return temp;
	}

	/**
	 * Sortiert das interne Array mit dem Bubblesort algortimus
	 */
	@SuppressWarnings("unchecked")
	public void sort() {
		boolean swapped;

		for (int i = 0; i < currentSize - 1; i++) {

			swapped = false;
			for (int j = 0; j < currentSize - 1 - i; j++) {

				Comparable<T> current = (Comparable<T>) this.data[j];
				T next = (T) this.data[j + 1];
			
				if (current.compareTo(next) > 0) {


					T temp = (T) this.data[j];
					this.data[j] = this.data[j + 1];
					this.data[j + 1] = temp;
					swapped = true;
				}
			}

			if (!swapped) break; // Keine Änderungen, also abbruch der Schleife
		}
	}
	
	/**
	 * Mischt das interne Array in einer zufälligen Reihenfolge
	 */
	void shuffle() {

		int index;
        Object temp;
        Random random = new Random();
        for (int i = this.currentSize - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = this.data[index];
            this.data[index] = this.data[i];
            this.data[i] = temp;
        }

	}

	@SuppressWarnings("unchecked")
	@Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < currentSize && data[currentIndex] != null;
            }

            
			@Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (T)data[currentIndex++];
            }
        };
	}
}
