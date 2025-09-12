package bank;

import bank.exceptions.InvalidAttributeException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Diese Klasse handelt die Überweisungen
 *
 * @author Alaeddin Bahrouni
 */
public class Transfer extends Transaction implements CalculateBill {

    /**
     * speichert den Namen des Senders
     */
    private String sender;
    /**
     * speichert den Namen des Empfängers
     */
    private String recipient;

    /**
     * default Konstruktor
     */
    public Transfer() {
    }

    public Transfer(String date, double amount, String description) {
        super(date, amount, description);
    }

    /**
     * Konstruktor für alle Attribute
     *
     * @param date        Datum der Überweisung
     * @param amount      Menge des Gelds
     * @param description zusätzliche Beschreibung
     * @param sender      der Sender
     * @param recipient   der Empfänger
     */
    public Transfer(String date, double amount, String description, String sender, String recipient) {
        super(date, amount, description); //verwendet das vorherige Konstruktor von date, description und amount
        this.sender = sender;
        this.recipient = recipient;
    }

    /**
     * Kopie Konstruktor
     *
     * @param t das Objekt, das wir kopieren möchten
     */
    public Transfer(Transfer t) {
        this(t.date, t.amount, t.description, t.sender, t.recipient);
    }


    /**
     * überprüft ob amount positiv ist. Wenn nein, dann wird den Wert zu -1 gesetzt.
     *
     * @param amount muss positiv sein.
     */
    @Override
    public void setAmount(double amount) throws IllegalStateException {
        if (amount >= 0) {
            this.amount = amount;
        }else {
            this.amount = -1;
            throw new IllegalStateException("Fehler: Amount muss positiv sein.");
        }
    }


    /**
     * Getter für sender
     *
     * @return sender als string
     */
    public String getSender() {
        return sender;
    }

    /**
     * Setter für sender
     *
     * @param sender der Name des Senders
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Getter für recipient
     *
     * @return Empfänger als String
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Setter für recipient
     *
     * @param recipient der Name des Empfängers
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * überprüft ob amount korrekt ist
     *
     * @return true, wenn der Betrag korrekt gesetzt ist, ansonsten false
     */
    @Override
    public boolean isValid() {
        boolean valid = true;
        if (amount == -1) {
            valid = false;
        }
        return valid;
    }

    /**
     * Gibt alle Attribute aus, außer wenn amount falsch eingetragen ist, dann wir ein error gezeigt
     *
     * @return sender, empfänger, date, amount und description als String
     */
    @Override
    public String toString() {
        if (amount == -1) {
            throw new IllegalStateException("Amount sollte positiv sein");
        }

        return "Transfer {Sender: " + sender + ", Recipient: " + recipient + super.toString();
    }

    /**
     * gibt den Wert von amount zurück, wie es ist, weil es keine Zinsen geben
     *
     * @return den Wert von amount
     */
    @Override
    public double calculate() {
        BigDecimal bigDecimalValue = new BigDecimal(amount);
        bigDecimalValue = bigDecimalValue.setScale(3, RoundingMode.HALF_UP);
        return bigDecimalValue.doubleValue();

    }


    /**
     * vergleicht beide Objekte
     *
     * @param obj das Objekt zu vergleichen
     * @return true wenn gleich und false wenn nicht
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Transfer transfer))
            return false;

        // Cast to Payment
        return super.equals(obj) && // Compare fields in the superclass
                Objects.equals(this.recipient, transfer.recipient) &&
                Objects.equals(this.sender, transfer.sender);
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), recipient, sender);
    }
}
