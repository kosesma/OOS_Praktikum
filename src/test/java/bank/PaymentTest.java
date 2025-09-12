package bank;

import bank.*;
import bank.exceptions.InvalidAttributeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {
    private Payment payment;

    @BeforeEach
    void init() throws InvalidAttributeException {
        payment = new Payment("01.01.2024", 100, "Salary; 100", 0.05, 0.1);
    }

    // Test für den Standardkonstruktor
    @Test
    void testConstructorWithBasicAttributes() throws InvalidAttributeException {
        Payment basicPayment = new Payment("02.02.2024", 50, "Bonus");
        assertEquals("02.02.2024", basicPayment.getDate());
        assertEquals(50, basicPayment.getAmount());
        assertEquals("Bonus", basicPayment.getDescription());
    }

    // Test für den vollständigen Konstruktor
    @Test
    void testConstructorWithAllAttributes() {
        assertEquals("01.01.2024", payment.getDate());
        assertEquals(100, payment.getAmount());
        assertEquals("Salary; 100", payment.getDescription());
        assertEquals(0.05, payment.getIncomingInterest());
        assertEquals(0.1, payment.getOutgoingInterest());
    }

    // Test für den Copy-Konstruktor
    @Test
    void testCopyConstructor() throws InvalidAttributeException {
        Payment copy = new Payment(payment);
        assertEquals(payment, copy);
        assertNotSame(payment, copy); // Sicherstellen, dass die Objekte unterschiedlich sind
    }

    // Tests für die Methode calculate()
    @Test
    void testCalculatePositiveAmount() {
        assertEquals(95, payment.calculate(), 0.01); // Betrag abzüglich incomingInterest
    }

    @Test
    void testCalculateNegativeAmount() throws InvalidAttributeException {
        payment.setAmount(-100);
        assertEquals(-110, payment.calculate(), 0.01); // Betrag zuzüglich outgoingInterest
    }

    // Tests für setAmount()
    @Test
    void testSetAmount() {
        payment.setAmount(200);
        assertEquals(200, payment.getAmount());
    }

    // Tests für setIncomingInterest()
    @ParameterizedTest
    @ValueSource(doubles = {0, 0.25, 0.5, 0.75, 1})
    void testValidIncomingInterest(double interest) {
        assertDoesNotThrow(() -> payment.setIncomingInterest(interest));
        assertEquals(interest, payment.getIncomingInterest());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1, -1, 2})
    void testInvalidIncomingInterest(double interest) {
        assertThrows(IllegalStateException.class, () -> payment.setIncomingInterest(interest));
    }

    // Tests für setOutgoingInterest()
    @ParameterizedTest
    @ValueSource(doubles = {0, 0.25, 0.5, 0.75, 1})
    void testValidOutgoingInterest(double interest) {
        assertDoesNotThrow(() -> payment.setOutgoingInterest(interest));
        assertEquals(interest, payment.getOutgoingInterest());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1, -1, 2})
    void testInvalidOutgoingInterest(double interest) {
        assertThrows(IllegalStateException.class, () -> payment.setOutgoingInterest(interest));
    }

    // Test für die Methode attributeValid()
    @Test
    void testAttributeValidWhenValid() {
        assertTrue(payment.isValid());
    }

    @Test
    void testAttributeValidWhenInvalidIncomingInterest() {
        assertThrows(IllegalStateException.class, () -> payment.setIncomingInterest(-1));
        assertFalse(payment.isValid()); // Zahlung ist jetzt im ungültigen Zustand
    }

    @Test
    void testAttributeValidWhenInvalidOutgoingInterest() {
        assertThrows(IllegalStateException.class, () -> payment.setOutgoingInterest(-1));
        assertFalse(payment.isValid()); // Zahlung ist jetzt im ungültigen Zustand
    }

    // Tests für equals()
    @Test
    void testEquals() throws InvalidAttributeException {
        Payment other = new Payment("01.01.2024", 100, "Salary; 100", 0.05, 0.1);
        assertEquals(payment, other);
    }

    @Test
    void testNotEqualsDifferentAmount() throws InvalidAttributeException {
        Payment other = new Payment("01.01.2024", 200, "Salary; 200", 0.05, 0.1);
        assertNotEquals(payment, other);
    }

    @Test
    void testNotEqualsDifferentInterest() throws InvalidAttributeException {
        Payment other = new Payment("01.01.2024", 100, "Salary; 100", 0.1, 0.2);
        assertNotEquals(payment, other);
    }

    @Test
    void testNotEqualsDifferentDescription() throws InvalidAttributeException {
        Payment other = new Payment("01.01.2024", 100, "Other Description", 0.05, 0.1);
        assertNotEquals(payment, other);
    }

    // Test für toString()
    @Test
    void testToString() {
        String expected = "Payment{Incoming interest: 0.05, Outgoing interest: 0.1, date=01.01.2024, amount=95.0, description=Salary; 100}";
        assertEquals(expected, payment.toString());
    }
}
