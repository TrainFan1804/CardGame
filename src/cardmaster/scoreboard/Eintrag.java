package cardmaster.scoreboard;

import java.time.Instant;

/**
 * Einen Eintrag für die {@link Bestenliste}
 */
public class Eintrag implements Comparable<Eintrag>{

    private final double point;
    private final Instant timeStamp;
    private int place;

    /**
     * Erstellt einen Eintrag
     * 
     * @param point Die Punkte des letzten Spiels
     * @param timeStamp Der Zeitstempel des letzten Spiels
     * @param place Der Platz des letzten Spiels
     */
    public Eintrag(double point, Instant timeStamp, int place) {

        this.point = point;
        this.timeStamp = timeStamp;
        this.place = place;
    }

    /**
     * Liefert die Punkte des Eintrag
     * 
     * @return Die Punkte des Eintrag
     */
    public double getPoint() {

        return this.point;
    }

    /**
     * Liefert den Zeitstempel des Eintrag
     * 
     * @return Die den Zeitstempel des Eintrag
     */
    public Instant getTimeStamp() {

        return this.timeStamp;
    }

    /**
     * Liefert den Platz des Eintrag
     * 
     * @return Die den Platz des Eintrag
     */
    public int getPlace() {

        return this.place;
    }

    /**
     * Erhöht den Eintrag des Platzes um 1
     */
    public void increasePlace() {

        this.place++;
    }

    @Override
    public int compareTo(Eintrag other) {

        if (this.place != other.place) {

            return Integer.compare(this.place, other.place);
        } else {

            return Double.compare(other.point, this.point);
        }
    }
}
