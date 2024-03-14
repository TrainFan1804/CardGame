/**
 * @author g.ary, o.le
 * @since 13.03.2024
 */
public class CreditScore {

    private int credits;

    public CreditScore(int score) {

        this.credits = score;
    }

    public int getCredits() {

        return this.credits;
    }

    public void addCredits(int credits) {

        if (credits < 0) {

            return;
        }

        this.credits += credits;
    }

    public void removeCredits(int credits) {

        if (this.credits - credits >= 0) {

            this.credits -= credits;
        }
    }
    
}
