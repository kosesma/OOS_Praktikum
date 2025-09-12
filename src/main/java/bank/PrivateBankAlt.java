package bank;

import bank.exceptions.*;

import java.io.IOException;
import java.util.*;

public class PrivateBankAlt implements Bank {
    private String name;
    private double incomingInterest;
    private double outgoingInterest;
    private Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();


    /**
     * default konstruktor
     */
    public PrivateBankAlt() {
    }

    /**
     * Konstruktor: nimmt die 3 ersten Parametern und fügt ein neues Hashmap hinzu
     *
     * @param name             name des Kontoinhabers
     * @param incomingInterest eingehende Zinsen zwischen 0 und 1
     * @param outgoingInterest ausgehende Zinsen zwischen 0 und 1
     */
    public PrivateBankAlt(String name, double incomingInterest, double outgoingInterest) {
        this.name = name;
        this.incomingInterest = incomingInterest;
        this.outgoingInterest = outgoingInterest;
    }

    /**
     * kopy konstruktor
     *
     * @param privateBankAlt das zu kopierende Objekt
     */
    public PrivateBankAlt(PrivateBankAlt privateBankAlt) {
        this(privateBankAlt.name, privateBankAlt.incomingInterest, privateBankAlt.outgoingInterest);
    }

    /**
     * Getter für name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * setzt den Namen
     *
     * @param name der zu setzenden Namen
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter für die eingehenden Zinsen
     *
     * @return incomingInterest
     */
    public double getIncomingInterest() {
        return incomingInterest;
    }

    /**
     * setzt die eingehenden Zinsen
     *
     * @param incomingInterest die zu setzenden Zinsen
     */
    public void setIncomingInterest(double incomingInterest) {
        this.incomingInterest = incomingInterest;
    }

    /**
     * Getter für ausgehende Zinsen
     *
     * @return outgoingInterest
     */
    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    /**
     * setzt die ausgehenden Zinsen
     *
     * @param outgoingInterest die zu setzenden Zinsen
     */
    public void setOutgoingInterest(double outgoingInterest) {
        this.outgoingInterest = outgoingInterest;
    }

    /**
     * gibt alle Klassenattribute aus
     *
     * @return die auszugebende String mit allen Attributen
     */
    @Override
    public String toString() {
        return "PrivateBank{" +
                "name='" + name + '\'' +
                ", incomingInterest=" + incomingInterest +
                ", outgoingInterest=" + outgoingInterest +
                ", accountsToTransactions=" + accountsToTransactions +
                '}';
    }

    /**
     * vergleicht zwei Kontos zusammen
     *
     * @param obj der zu vergleichendem Konto
     * @return true, wenn beide Objekte gleich sind, ansonsten false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof PrivateBankAlt bank))
            return false;

        return Objects.equals(this.name, bank.name) &&
                Objects.equals(this.incomingInterest, bank.incomingInterest) &&
                Objects.equals(this.outgoingInterest, bank.outgoingInterest) &&
                Objects.equals(this.accountsToTransactions, bank.accountsToTransactions);
    }


    /**
     * Adds an account to the bank.
     *
     * @param account the account to be added
     * @throws AccountAlreadyExistsException if the account already exists
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException {
        if (!this.accountsToTransactions.containsKey(account)) {
            accountsToTransactions.put(account, new ArrayList<>());
        } else {
            throw new AccountAlreadyExistsException(("Account already exists: " + account));
        }
    }

    /**
     * Adds an account (with specified transactions) to the bank.
     * Important: duplicate transactions must not be added to the account!
     *
     * @param account      the account to be added
     * @param transactions a list of already existing transactions which should be added to the newly created account
     * @throws AccountAlreadyExistsException    if the account already exists
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void createAccount(String account, List<Transaction> transactions) throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException {
        if (this.accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException(("Account already exists: " + account));
        } else {
            List<Transaction> uniqueTransactions = new ArrayList<>();
            for (Transaction transaction : transactions) {
                if (!transaction.isValid()) {
                    throw new TransactionAttributeException("Invalid transaction attributes.");
                } else if (uniqueTransactions.contains(transaction)) {
                    throw new TransactionAlreadyExistException("Transaction already exists: " + transaction);
                } else {
                    uniqueTransactions.add(transaction);
                }
            }
        }
    }

    /**
     * Adds a transaction to an already existing account.
     *
     * @param account     the account to which the transaction is added
     * @param transaction the transaction which should be added to the specified account
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws AccountDoesNotExistException     if the specified account does not exist
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void addTransaction(String account, Transaction transaction) throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        if (!this.accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException(("Account does not exist: " + account));
        } else if (!this.accountsToTransactions.get(account).contains(transaction)) {
            throw new TransactionAlreadyExistException("Transaction already exists: " + transaction);
        } else if (!transaction.isValid()) {
            throw new TransactionAttributeException("Invalid transaction attributes.");
        } else {
            if (transaction instanceof Payment payment) {
                payment.setIncomingInterest(this.incomingInterest);
                payment.setOutgoingInterest(this.outgoingInterest);
            }
            accountsToTransactions.get(account).add(transaction);
        }
    }

    /**
     * Removes a transaction from an account. If the transaction does not exist, an exception is
     * thrown.
     *
     * @param account     the account from which the transaction is removed
     * @param transaction the transaction which is removed from the specified account
     * @throws AccountDoesNotExistException     if the specified account does not exist
     * @throws TransactionDoesNotExistException if the transaction cannot be found
     */
    @Override
    public void removeTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException {
        if (!this.accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException(("Account does not exist: " + account));
        } else if (!this.accountsToTransactions.get(account).contains(transaction)) {
            throw new TransactionDoesNotExistException("Transaction does not exist: " + transaction);
        } else {
            accountsToTransactions.get(account).remove(transaction);
        }
    }

    /**
     * Checks whether the specified transaction for a given account exists.
     *
     * @param account     the account from which the transaction is checked
     * @param transaction the transaction to search/look for
     */
    @Override
    public boolean containsTransaction(String account, Transaction transaction) {
        return accountsToTransactions.get(account).contains(transaction);
    }

    /**
     * Calculates and returns the current account balance.
     *
     * @param account the selected account
     * @return the current account balance
     */
    @Override
    public double getAccountBalance(String account) {
        double amount = 0;
        if (accountsToTransactions.get(account).isEmpty()) {
            return amount;
        } else {
            for (Transaction transaction : accountsToTransactions.get(account)) {
                if (transaction instanceof Transfer transfer) {
                    if (Objects.equals(transfer.getSender(), account)) {
                        amount -= transfer.getAmount();
                    }else {
                        amount += transfer.getAmount();
                    }
                }
                else {
                    amount += transaction.getAmount();
                }
            }
        }
        return amount;
    }

    /**
     * Returns a list of transactions for an account.
     *
     * @param account the selected account
     * @return the list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactions(String account) {
        return accountsToTransactions.get(account);
    }

    /**
     * Returns a sorted list (-> calculated amounts) of transactions for a specific account. Sorts the list either in ascending or descending order
     * (or empty).
     *
     * @param account the selected account
     * @param asc     selects if the transaction list is sorted in ascending or descending order
     * @return the sorted list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        List<Transaction> transactions = getTransactions(account);
        for (Transaction transaction : transactions) {
            transaction.setAmount(transaction.calculate());

        }
        if (asc) {
            transactions.sort(Comparator.comparing(Transaction::getAmount));
        } else {
            transactions.sort(Comparator.comparing(Transaction::getAmount).reversed());
        }

        return transactions;
    }

    /**
     * Returns a list of either positive or negative transactions (-> calculated amounts).
     *
     * @param account  the selected account
     * @param positive selects if positive or negative transactions are listed
     * @return the list of all transactions by type
     */
    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> transactions = getTransactions(account);
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (positive) {
                if (transaction instanceof Payment payment && payment.getAmount() > 0) {
                    transaction.setAmount(transaction.calculate());
                    filteredTransactions.add(transaction);
                } else if (transaction instanceof Transfer) {
                    transaction.setAmount(transaction.calculate());
                    filteredTransactions.add(transaction);
                }
            } else {
                if (transaction instanceof Payment payment && payment.getAmount() < 0) {
                    transaction.setAmount(transaction.calculate());
                    filteredTransactions.add(transaction);
                }
            }
        }
        return filteredTransactions;
    }

    /**
     * deletes an account from the bank
     *
     * @param account the selected account
     * @throws AccountDoesNotExistException if account doesn't exist
     * @throws IOException                  if something goes wrong with reading from or writing to a file
     */
    @Override
    public void deleteAccount(String account) throws AccountDoesNotExistException, IOException {

    }

    /**
     * returns a list of all existing accounts in the bank
     *
     * @return the list of all accounts
     */
    @Override
    public List<String> getAllAccounts() {
        return List.of();
    }
}
