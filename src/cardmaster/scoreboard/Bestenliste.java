package cardmaster.scoreboard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import cardmaster.ScoreBoard;
import cardmaster.collections.AlgoArrayList;

/**
 * Die Bestenliste des Spiels
 */
public class Bestenliste implements ScoreBoard {

    private static Bestenliste bestenliste;
    private AlgoArrayList<Eintrag> entries;

    /**
     * Erstellt eine Bestenliste
     */
    private Bestenliste() {

        this.entries = new AlgoArrayList<Eintrag>();
    }

    /**
     * Liefert eine Bestenliste zurück
     * 
     * @return Die Bestenliste
     */
    public static ScoreBoard getInstance() {

        if (bestenliste == null) {

            bestenliste = new Bestenliste();
        }
        return bestenliste;
    }

    @Override
    public void save(OutputStream out) throws Exception {

        try (out;DataOutputStream dataOut = new DataOutputStream(out)) {

            dataOut.writeInt(entries.size());
            for (Eintrag entry : entries) {

                dataOut.writeDouble(entry.getPoint());
                dataOut.writeInt(entry.getPlace());

                dataOut.writeLong(entry.getTimeStamp().getEpochSecond());
                dataOut.writeLong(entry.getTimeStamp().getNano());
                
            }
        }
    }

    @Override
    public void load(InputStream in) throws Exception {

        try (in;DataInputStream dataIn = new DataInputStream(in)) {
            

            int size = dataIn.readInt();
            
            List<Eintrag> tempEntries = new ArrayList<>();
            
            System.out.println("Loading " + size + " entries");
            
            for (int i = 0; i < size; i++) {
                
                
                    double point = dataIn.readDouble();
                    int place = dataIn.readInt();
                    long milli = dataIn.readLong();
                    long nano = dataIn.readLong();
                    
                    Instant timeStamp = Instant.ofEpochSecond(milli).plusNanos(nano);
                    
                    tempEntries.add(new Eintrag(point, timeStamp, place));
                }
                
            entries.clear();
            entries.addAll(tempEntries);

        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int add(double score) {

        if (score < 0 || Double.isNaN(score)) {

            throw new IllegalArgumentException("Illegal score value: " + score);
        }

        int newPlace = 1; // Beginne mit dem ersten Platz
        List<Eintrag> entriesToUpdate = new ArrayList<>();

        for (Eintrag entry : entries) {
            if (entry.getPoint() > score) {

                // Wenn der existierende Score höher ist, sollte unser neuer Score dahinter
                // kommen.
                newPlace++;
            } else if (entry.getPoint() == score) {

                // Wenn der Score gleich ist, teilen sie denselben Platz, aber unser neuer
                // Eintrag wird danach eingereiht.
                newPlace = entry.getPlace();
            } else {
                // Wenn der existierende Score niedriger ist, müssen diese Einträge ihren Platz
                // erhöhen.
                if (entry.getPlace() >= newPlace) {

                    entriesToUpdate.add(entry);
                }
            }
        }

        Eintrag newEntry = new Eintrag(score, Instant.now(), newPlace);
        entries.add(newEntry); // Füge den neuen Eintrag hinzu
        entries.sort();

        // Aktualisiere die Plätze der alten Einträge
        for (Eintrag entry : entriesToUpdate) {
            entry.increasePlace();
        }

        return newPlace; // Rückgabe des Platzes des neuen Scores
    }

    @Override
    public int size() {

        return this.entries.size();
    }

    @Override
    public Instant getInstant(int n) {

        checkIndex(n);
        return ((Eintrag) this.entries.getItemAtIndex(n)).getTimeStamp();
    }

    @Override
    public double getScore(int n) {

        checkIndex(n);
        return ((Eintrag) this.entries.getItemAtIndex(n)).getPoint();
    }

    @Override
    public int getPlace(int n) {

        checkIndex(n);
        return ((Eintrag) this.entries.getItemAtIndex(n)).getPlace();
    }

    @Override
    public void clear() {

        this.entries.clear();
    }

    /**
     * Überprüft ob der gegeben Index ein valider Index ist
     * 
     * @param n Der geprüfte Index
     */
    private void checkIndex(int n) {

        if (bestenliste.size() == 0) {
            throw new IndexOutOfBoundsException("Wrong index: " + n);
        }
        if (n < 0 || n > entries.size()) {
            throw new IndexOutOfBoundsException("Wrong index: " + n);
        }
    }

}
