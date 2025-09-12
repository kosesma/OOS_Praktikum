package bank;

import bank.*;
import bank.exceptions.InvalidAttributeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TransferTest {

    Transfer transfer;

    @BeforeEach
    void init() throws InvalidAttributeException {
        transfer = new Transfer("01.01.2024", 500, "Salary Payment", "Alice", "Bob");
    }

    @Test
    void testConstructorWithBasicAttributes() throws InvalidAttributeException {
        Transfer basicTransfer = new Transfer("02.02.2024", 50, "Bonus");
        assertEquals("02.02.2024", basicTransfer.getDate());
        assertEquals(50, basicTransfer.getAmount());
        assertEquals("Bonus", basicTransfer.getDescription());
    }

    @Test
    void testConstructor() {
        assertEquals("01.01.2024", transfer.getDate());
        assertEquals(500, transfer.getAmount());
        assertEquals("Salary Payment", transfer.getDescription());
        assertEquals("Alice", transfer.getSender());
        assertEquals("Bob", transfer.getRecipient());
    }

    @Test
    void testCopyConstructor() {
        Transfer copy = new Transfer(transfer);
        assertEquals(transfer, copy);
        assertNotSame(transfer, copy); // Sicherstellen, dass es sich um ein anderes Objekt handelt
    }

    @Test
    void testCalculate() {
        assertEquals(500, transfer.calculate());
    }

    @Test
    void testEquals() throws InvalidAttributeException {
        Transfer identical = new Transfer("01.01.2024", 500, "Salary Payment", "Alice", "Bob");
        Transfer differentAmount = new Transfer("01.01.2024", 400, "Salary Payment", "Alice", "Bob");
        Transfer differentSender = new Transfer("01.01.2024", 500, "Salary Payment", "Charlie", "Bob");

        assertEquals(transfer, identical);
        assertNotEquals(transfer, differentAmount);
        assertNotEquals(transfer, differentSender);
    }

    @Test
    void testToString() {
        String expected = "Transfer {Sender: Alice, Recipient: Bob, date=01.01.2024, amount=500.0, description=Salary Payment}";
        assertEquals(expected, transfer.toString());
    }

    @Test
    void testInvalidAmountThrowsException() {
        assertThrows(IllegalStateException.class, () -> new Transfer("01.01.2024", -100, "Invalid Payment", "Alice", "Bob"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {100, 0, 9999.99})
    void testValidAmounts(double amount) throws InvalidAttributeException {
        Transfer validTransfer = new Transfer("01.01.2024", amount, "Payment", "Alice", "Bob");
        assertEquals(amount, validTransfer.getAmount());
    }

    @Test
    void testSetSenderAndRecipient() {
        transfer.setSender("Charlie");
        transfer.setRecipient("Dave");
        assertEquals("Charlie", transfer.getSender());
        assertEquals("Dave", transfer.getRecipient());
    }

    @Test
    void testIncomingTransferCalculate() throws InvalidAttributeException {
        IncomingTransfer incomingTransfer = new IncomingTransfer("01.01.2024", 500, "Gift", "Alice", "Bob");
        assertEquals(500, incomingTransfer.calculate());
    }

    @Test
    void testOutgoingTransferCalculate() throws InvalidAttributeException {
        OutgoingTransfer outgoingTransfer = new OutgoingTransfer("01.01.2024", 300, "Rent", "Alice", "Bob");
        assertEquals(-300, outgoingTransfer.calculate());
    }

    @Test
    void testAttributeValid() throws IllegalStateException {
        assertTrue(transfer.isValid());

        // Ungültigen Betrag setzen
        assertThrows(IllegalStateException.class, () -> transfer.setAmount(-200));
        assertFalse(transfer.isValid());
    }
}