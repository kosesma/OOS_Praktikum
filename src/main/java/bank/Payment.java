package bank;

import java.util.Objects;

/**
 * Diese Klasse handelt die ein- und ausgehende Zahlungen
 * @author Alaeddin Bahrouni
 */
public class Payment extends Transaction implements CalculateBill {

    /**
     * speichert die eingehenden Zinsen (Positiv und zwischen 0 und 1 für die Prozentzahl)
     */
    private double incomingInterest;
    /**
     * speichert die eingehenden Zinsen (Positiv und zwischen 0 und 1 für die Prozentzahl)
     */
    private double outgoingInterest;

    public Payment(String date, double amount, String description){
        super(date, amount, description);
    }
    /**
     * Konstruktor für alle Attribute
     *
     * @param date             Datum der Ein- / Auszahlung
     * @param amount           Der Betrag
     * @param description      Beschreibung
     * @param incomingInterest eingehende Zinsen
     * @param outgoingInterest ausgehende Zinsen
     */
    public Payment(String date, double amount, String description, double incomingInterest, double outgoingInterest) {
        super(date, amount, description); //verwendet das vorherige Konstruktor von date, description und amount
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
    }

    /**
     * default Konstruktor
     */
    public Payment() {
    }

    /**
     * Kopie Konstruktor
     *
     * @param payment das Objekt, das wir kopieren möchten
     */
    public Payment(Payment payment) {
        this(payment.date, payment.amount, payment.description, payment.incomingInterest, payment.outgoingInterest);
    }


    /**
     * Getter für incomingInterest
     *
     * @return eingehende Zinsen als double
     */
    public double getIncomingInterest() {
        return incomingInterest;
    }

    /**
     * speichert -1 in incomingInterest, wenn der Wert außer von [0:1] ist
     * ansonsten speichert den Wert in incomingInterest
     *
     * @param incomingInterest der Wert der eingehenden Zinsen. Muss zwischen 0 und 1 sein
     */
    public void setIncomingInterest(double incomingInterest) {
        if (incomingInterest >= 0 && incomingInterest <= 1) {
            this.incomingInterest = incomingInterest;
        } else {
            this.incomingInterest = -1;
            throw new IllegalStateException("Interest must be between 0 and 1");
        }
    }

    /**
     * Getter für outgoingInterest
     *
     * @return ausgehende Zinsen als double
     */
    public double getOutgoingInterest() {
        return outgoingInterest;
    }


    /**
     * speichert -1 in outgoingInterest, wenn der Wert außer von [0:1] ist
     * ansonsten speichert den Wert in outgoingInterest
     *
     * @param outgoingInterest der Wert der ausgehenden Zinsen. Muss zwischen 0 und 1 sein
     */
    public void setOutgoingInterest(double outgoingInterest) {
        if (outgoingInterest >= 0 && outgoingInterest <= 1) {
            this.outgoingInterest = outgoingInterest;
        } else {
            this.outgoingInterest = -1;
            throw new IllegalStateException("Interest must be between 0 and 1");
        }
    }

    /**
     * überprüft ob incomingInterest und/oder outgoingInterest korrekt sind
     * @return true, wenn Zinsen korrekt gesetzt sind, ansonsten false
     */
    @Override
    public boolean isValid() {
        return incomingInterest != -1 && outgoingInterest != -1;
    }

    /**
     * Gibt alle Attribute aus, außer wenn amount falsch eingetragen ist, dann wir ein error gezeigt
     *
     * @return incomingInterest, outgoingInterest, date, amount und description als String
     */
    @Override
    public String toString() {
        if (!isValid()) {
            throw new IllegalStateException("Payment is invalid");
        }

        return "Payment{Incoming interest: " + incomingInterest + ", Outgoing interest: " + outgoingInterest + super.toString();
    }

    /**
     * berechnet den Wert von amount nach den Zinsen
     *
     * @return (amount - prozentual Wert von incomingInterest von amount) wenn amount Positiv ODER (amount + prozentual Wert von outgoingInterest von amount) wenn amount Negativ
     */
    @Override
    public double calculate() {
        if (amount >= 0) {
            return amount * (1.0 - incomingInterest);
        } else if (amount < 0) {
            return amount * (1.0 + outgoingInterest);
        } else {
            return amount;
        }
    }


    /**
     * vergleicht beide Objekte
     * @param obj das Objekt zu vergleichen
     * @return true wenn gleich und false wenn nicht
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;  // Same object reference
        }

        if (!(obj instanceof Payment)) {
            return false;  // obj must be of type Payment
        }

        Payment payment = (Payment) obj; // Now safely cast obj to Payment

        // Compare fields in the superclass first (Transaction)
        boolean superEquals = super.equals(obj);

        // Compare the specific fields in Payment class
        return superEquals &&
                Objects.equals(this.incomingInterest, payment.incomingInterest) &&
                Objects.equals(this.outgoingInterest, payment.outgoingInterest);
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), incomingInterest, outgoingInterest);
    }
}