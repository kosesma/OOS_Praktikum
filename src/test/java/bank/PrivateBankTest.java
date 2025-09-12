package bank;

import bank.*;

import bank.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import static org.junit.jupiter.api.Assertions.*;

class PrivateBankTest {

    PrivateBank bank;

    @BeforeEach
    void init() throws IOException {
        Path testDirectory = Paths.get("testDirectory");
        if (!Files.exists(testDirectory)) {
            Files.createDirectory(testDirectory); // Create the directory if it doesn't exist
        }
        bank = new PrivateBank("TestBank", 0.05, 0.1, "testDirectory");
    }

    @Test
    void testConstructor() {
        assertEquals("TestBank", bank.getName());
        assertEquals(0.05, bank.getIncomingInterest());
        assertEquals(0.1, bank.getOutgoingInterest());
        assertEquals("testDirectory", bank.getDirectoryName());
    }

    @Test
    void testCopyConstructor() throws IOException {
        PrivateBank copy = new PrivateBank(bank);
        assertEquals(bank, copy);
    }

    @Test
    void testEqualsAndHashCode() throws IOException {
        PrivateBank other = new PrivateBank(bank);
        assertEquals(bank, other);
        assertEquals(bank.hashCode(), other.hashCode());
    }

    @Test
    void testRemoveNonExistingAccount() {
        assertThrows(AccountDoesNotExistException.class, () -> bank.removeTransaction("NonExistingAccount", null));
    }

    @Test
    void testReadAccounts() throws IOException, TransactionAlreadyExistException, AccountAlreadyExistsException, AccountDoesNotExistException, TransactionAttributeException {
        PrivateBank bank = new PrivateBank("Sparkasse", 0.3, 0.5, "C:\\Users\\alara\\IdeaProjects\\OOS_Praktikum\\bank-accounts\\");
        Payment payment = new Payment("2024-11-24", 1000.50, "Loan Payment", 0.3, 0.5);
        Transfer transfer = new Transfer("2024-11-24", 300, "Überweisung", "Bob", "Alive");
        List<Transaction> payments = new ArrayList<>();
        payments.add(payment);
        payments.add(transfer);
        bank.createAccount("Adam", payments);
        bank.createAccount("Alice", payments);

        PrivateBank bank1 = new PrivateBank(bank);
        assertEquals(bank1.getTransactions("Adam").size(), bank.getTransactions("Adam").size());
        bank.removeFile("Adam");
        bank.removeFile("Alice");
    }

    @Test
    void testGetTransactionsForNonExistingAccount() {
        List<Transaction> transactions = bank.getTransactions("NonExistent");
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void testEqualsWithModifiedObject() throws IOException {
        PrivateBank modifiedBank = new PrivateBank(bank);
        modifiedBank.setName("ModifiedBank");
        assertNotEquals(bank, modifiedBank, "Banks with different names should not be equal.");
    }

    @Test
    void testEqualsWithNullAndDifferentObject() {
        assertNotEquals(bank, null, "Bank should not be equal to null.");
        assertNotEquals(bank, "StringObject", "Bank should not be equal to an object of a different type.");
    }

    @Test
    void testEmptyAccountBalance() {
        double balance = bank.getAccountBalance("EmptyAccount");
        assertEquals(0.0, balance, "Balance of a non-existing account should be zero.");
    }
    //bis hier

    @Test
    void testSettersAndGetters() {
        bank.setName("NewBank");
        assertEquals("NewBank", bank.getName());

        bank.setIncomingInterest(0.03);
        assertEquals(0.03, bank.getIncomingInterest());

        bank.setOutgoingInterest(0.08);
        assertEquals(0.08, bank.getOutgoingInterest());

        bank.setDirectoryName("newDirectory");
        assertEquals("newDirectory", bank.getDirectoryName());
    }

    @Test
    void testCreateAccount() throws AccountAlreadyExistsException, IOException {
        bank.createAccount("Account Alice");
        assertTrue(bank.getTransactions("Account Alice").isEmpty());
    }

    @Test
    void testCreateAccountWithTransactions() throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Payment("2024-01-01", 100, "Salary", 0.05, 0.1));
        bank.createAccount("Account Alice", transactions);

        assertEquals(1, bank.getTransactions("Account Alice").size());
    }

    @Test
    void testAddTransaction() throws Exception {
        bank.createAccount("Account Alice");
        Transaction transaction = new Payment("2024-01-01", 100, "Salary", 0.05, 0.1);
        bank.addTransaction("Account Alice", transaction);

        assertTrue(bank.containsTransaction("Account Alice", transaction));
    }

    @Test
    void testRemoveTransaction() throws Exception {
        bank.createAccount("Account Alice");
        Transaction transaction = new Payment("2024-01-01", 100, "Salary", 0.05, 0.1);
        bank.addTransaction("Account Alice", transaction);
        bank.removeTransaction("Account Alice", transaction);

        assertFalse(bank.containsTransaction("Account Alice", transaction));
    }

    @Test
    void testGetAccountBalance() throws Exception {
        bank.createAccount("Account Alice");
        bank.addTransaction("Account Alice", new Payment("2024-01-01", 100, "Salary", 0.05, 0.1));
        bank.addTransaction("Account Alice", new Payment("2024-01-02", -50, "Rent", 0.05, 0.1));

        assertEquals(40.0, Math. ceil(bank.getAccountBalance("Account Alice")));
    }

    @Test
    void testGetTransactions() throws Exception {
        bank.createAccount("Account Alice");
        Transaction transaction1 = new Payment("2024-01-01", 100, "Salary", 0.05, 0.1);
        Transaction transaction2 = new Payment("2024-01-02", -50, "Rent", 0.05, 0.1);
        bank.addTransaction("Account Alice", transaction1);
        bank.addTransaction("Account Alice", transaction2);

        List<Transaction> transactions = bank.getTransactions("Account Alice");
        assertEquals(2, transactions.size());
        assertTrue(transactions.contains(transaction1));
        assertTrue(transactions.contains(transaction2));
    }

    @Test
    void testGetTransactionsSorted() throws Exception {
        bank.createAccount("Account Alice");
        bank.addTransaction("Account Alice", new Payment("2024-01-01", 100, "Salary", 0.05, 0.1));
        bank.addTransaction("Account Alice", new Payment("2024-01-02", -50, "Rent", 0.05, 0.1));
        List<Transaction> ascending = bank.getTransactionsSorted("Account Alice", true);
        assertEquals(-55.0, Math. ceil(ascending.get(0).calculate()));
        assertEquals(95.0, Math. ceil(ascending.get(1).calculate()));

        List<Transaction> descending = bank.getTransactionsSorted("Account Alice", false);
        assertEquals(95.0, Math. ceil(descending.get(0).calculate()));
        assertEquals(-55.0, Math. ceil(descending.get(1).calculate()));
    }

    @Test
    void testGetTransactionsByType() throws Exception {
        bank.createAccount("Account Alice");
        bank.addTransaction("Account Alice", new Payment("2024-01-01", 100, "Salary", 0.05, 0.1));
        bank.addTransaction("Account Alice", new Payment("2024-01-02", -50, "Rent", 0.05, 0.1));

        List<Transaction> positive = bank.getTransactionsByType("Account Alice", true);
        List<Transaction> negative = bank.getTransactionsByType("Account Alice", false);

        assertEquals(1, positive.size());
        assertEquals(95.0, positive.get(0).calculate());

        assertEquals(1, negative.size());
       // assertEquals(-55.0, negative.getFirst().calculate());
    }

    @Test
    void testToString() {
        String expected = "PrivateBank{name='TestBank', incomingInterest=0.05, outgoingInterest=0.1}";
        assertEquals(expected, bank.toString());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.5, 1.0})
    void testValidInterestRates(double rate) {
        assertDoesNotThrow(() -> {
            bank.setIncomingInterest(rate);
            bank.setOutgoingInterest(rate);
        });
    }

    @Test
    void testInvalidInterestRates() {
        assertThrows(IllegalArgumentException.class, () -> bank.setIncomingInterest(-0.1));
        assertThrows(IllegalArgumentException.class, () -> bank.setOutgoingInterest(1.1));
    }

    @Test
    void testExceptions() {
        assertThrows(AccountAlreadyExistsException.class, () -> {
            bank.createAccount("Account Alice");
            bank.createAccount("Account Alice");
        });

        assertThrows(AccountDoesNotExistException.class, () -> bank.addTransaction("NonExistent", new Payment("2024-01-01", 100, "Salary", 0.05, 0.1)));

        assertThrows(TransactionAlreadyExistException.class, () -> {
            Transaction transaction = new Payment("2024-01-01", 100, "Salary", 0.05, 0.1);
            bank.addTransaction("Account Alice", transaction);
            bank.addTransaction("Account Alice", transaction);
        });

        assertThrows(TransactionDoesNotExistException.class, () -> {
            Transaction transaction = new Payment("2024-01-01", 90, "Salary", 0.05, 0.1);
            bank.removeTransaction("Account Alice", transaction);
        });
    }






}

