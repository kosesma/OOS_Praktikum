package bank;

import java.util.Objects;

/**
 * Steht als Oberklasse für die anderen Bankfunktionen wie z.B. Transfer und Payment
 */
public abstract class Transaction implements CalculateBill {

    /**
     * speichert den Zeitpunkt der Ein- oder Auszahlung in DD.MM.YYYY
     */
    protected String date;
    /**
     * speichert die Geldmenge der Überweisung
     */
    protected double amount;
    /**
     * speichert zusätzliche Beschreibung für den Vorgang
     */
    protected String description;

    /**
     * Konstruktor für alle Attribute und benutzt das Setter von Amount, für Wertkontrolle
     *
     * @param date        Datum der Überweisung
     * @param amount      der Betrag
     * @param description Beschreibung
     */
    public Transaction(String date, double amount, String description) {
        this.date = date;
        setAmount(amount);
        this.description = description;
    }

    /**
     * default konstruktor
     */
    public Transaction() {
    }

    /**
     * getter für Amount
     *
     * @return den Betrag als Double
     */
    public double getAmount() {
        return amount;
    }

    /**
     * setter für Amount
     *
     * @param amount der Wert des Betrags
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * getter von DAte
     *
     * @return Datum als String TT.MM.YYYY
     */
    public String getDate() {
        return date;
    }

    /**
     * setter für Date
     *
     * @param date Datum als TT.MM.YYYY
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Beschreibung setter
     *
     * @return Beschreibung als String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Beschreibung getter
     *
     * @param description Beschreibung
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * überprüft die Korrektheit der Angaben
     * @return true, wenn alles richtig eingegeben ist, ansonsten false
     */
    public abstract boolean isValid();

    /**
     * gibt das Datum, Beschreibung der Überweisung zurück und die Geldmenge nach der Berechnung der Zinsen, wenn es gebraucht ist
     *
     * @return das Datum, die Beschreibung und die Geldmenge nach der Berechnung
     */
    @Override
    public String toString() {
        return ", date=" + date + ", amount=" + calculate() + ", description=" + description + '}';
    }

    /**
     * vergleicht beide Objekte
     *
     * @param o das Objekt zu vergleichen
     * @return true wenn gleich und false wenn nicht
     */
    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;

        if (!(o instanceof Transaction))
            return false;

        Transaction that = (Transaction) o;
        return Objects.equals(this.date, that.date) &&
                this.amount == that.amount &&
                Objects.equals(this.description, that.description);
    }
    @Override
    public int hashCode() {
        return Objects.hash(date, amount, description);
    }

}











