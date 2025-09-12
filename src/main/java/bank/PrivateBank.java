package bank;

import bank.exceptions.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.util.*;

public class PrivateBank implements Bank {
    private String name;
    private double incomingInterest;
    private double outgoingInterest;
    private Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();
    private String directoryName = "Bank Directory";


    /**
     * default konstruktor
     */
    public PrivateBank() {
    }

    /**
     * Konstruktor: nimmt alle Parametern außer von accountsToTransactions und fügt ein neues Hashmap hinzu
     *
     * @param name             name des Kontoinhabers
     * @param incomingInterest eingehende Zinsen zwischen 0 und 1
     * @param outgoingInterest ausgehende Zinsen zwischen 0 und 1
     * @param directoryName    Pfad der Speicherort
     */
    public PrivateBank(String name, double incomingInterest, double outgoingInterest, String directoryName) {
        this.name = name;
        this.incomingInterest = incomingInterest;
        this.outgoingInterest = outgoingInterest;
        this.directoryName = directoryName;
        readAccounts();
    }

    /**
     * kopy konstruktor
     *
     * @param privateBank das zu kopierende Objekt
     */
    public PrivateBank(PrivateBank privateBank) throws IOException {
        this(privateBank.name, privateBank.incomingInterest, privateBank.outgoingInterest, privateBank.directoryName);
        this.readAccounts();
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
    public void setIncomingInterest(double incomingInterest) throws IllegalArgumentException {
        if (incomingInterest <= 1 && incomingInterest >= 0) {
            this.incomingInterest = incomingInterest;
        } else {
            throw new IllegalArgumentException();
        }
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
        if (outgoingInterest <= 1 && outgoingInterest >= 0) {
            this.outgoingInterest = outgoingInterest;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * getter für directoryName
     *
     * @return den Pfad der Speicherort der Dateien
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * setzt den Speicherortspfad
     *
     * @param directoryName der Pfad
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
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

        if (!(obj instanceof PrivateBank bank))
            return false;

        return Objects.equals(this.name, bank.name) &&
                Objects.equals(this.incomingInterest, bank.incomingInterest) &&
                Objects.equals(this.outgoingInterest, bank.outgoingInterest) &&
                Objects.equals(this.accountsToTransactions, bank.accountsToTransactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, incomingInterest, outgoingInterest, accountsToTransactions);
    }


    /**
     * Adds an account to the bank.
     *
     * @param account the account to be added
     * @throws AccountAlreadyExistsException if the account already exists
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException, IOException {
        readAccounts();
        if (!this.accountsToTransactions.containsKey(account)) {
            accountsToTransactions.put(account, new ArrayList<>());
            writeAccount(account);
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
    public void createAccount(String account, List<Transaction> transactions) throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException, AccountDoesNotExistException {

        try {
            createAccount(account);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Transaction transaction : transactions) {
            addTransaction(account, transaction);
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

        readAccounts();
        if (!this.accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException(("Account does not exist: " + account));
        } else if (this.accountsToTransactions.get(account).contains(transaction)) {
            throw new TransactionAlreadyExistException("Transaction already exists: " + transaction);
        } else if (!transaction.isValid()) {
            throw new TransactionAttributeException("Invalid transaction attributes.");
        } else {
            if (transaction instanceof Payment payment) {
                payment.setIncomingInterest(this.incomingInterest);
                payment.setOutgoingInterest(this.outgoingInterest);
            }
            accountsToTransactions.get(account).add(transaction);

            try {
                writeAccount(account);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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
    public void removeTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException, IOException {
        readAccounts();
        if (!this.accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException(("Account does not exist: " + account));
        } else if (!this.accountsToTransactions.get(account).contains(transaction)) {
            throw new TransactionDoesNotExistException("Transaction does not exist: " + transaction);
        } else {
            accountsToTransactions.get(account).remove(transaction);

            writeAccount(account);

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
        readAccounts();
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
        readAccounts();
        // Standardmäßig eine leere Liste, falls das Konto nicht existiert
        List<Transaction> transactions = accountsToTransactions.getOrDefault(account, Collections.emptyList());
        double amount = 0.0;

        for (Transaction transaction : transactions) {
            amount += transaction.calculate();
        }
        BigDecimal bigDecimalValue = new BigDecimal(amount);
        bigDecimalValue = bigDecimalValue.setScale(3, RoundingMode.HALF_UP);
        return bigDecimalValue.doubleValue();
    }

    /**
     * Returns a list of transactions for an account.
     *
     * @param account the selected account
     * @return the list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactions(String account) {
        if (!this.accountsToTransactions.containsKey(account)) {
            return Collections.emptyList();
        }
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
        readAccounts();
        List<Transaction> transactions = getTransactions(account);

        if (asc) {
            transactions.sort(Comparator.comparingDouble(Transaction::calculate));
        } else {
            transactions.sort(Comparator.comparing(Transaction::calculate).reversed());
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
        readAccounts();
        List<Transaction> transactions = getTransactions(account);
        List<Transaction> filteredTransactions = new ArrayList<>();
        Transaction transaction;
        for (Transaction TKopie : transactions) {
            transaction = TKopie;
            double calculatedAmount = transaction.calculate();

            if (positive) {
                // Positive transactions: incoming payments or incoming transfers
                if ((transaction instanceof Payment payment && payment.calculate() > 0) ||
                        (transaction instanceof Transfer && calculatedAmount > 0)) {
                    filteredTransactions.add(transaction);
                }
            } else {
                // Negative transactions: outgoing payments or outgoing transfers
                if ((transaction instanceof Payment payment && payment.calculate() < 0) ||
                        (transaction instanceof Transfer && calculatedAmount < 0)) {
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
        readAccounts();

        if (!this.accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException(("Account does not exist: " + account));
        } else removeFile(account);
        readAccounts();
    }

    /**
     * returns a list of all existing accounts in the bank
     *
     * @return the list of all accounts
     */
    @Override
    public List<String> getAllAccounts() {
        readAccounts();
        return new ArrayList<>(this.accountsToTransactions.keySet());
    }


    /**
     * liest alle Dateien von einem Ordner und ordnet jede Transaktion dem betroffenen Konto zu.
     *
     * @throws IOException if an input-related problem occurs.
     */
    private void readAccounts() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Transaction.class, new TransactionJsonAdapter())
                .create();

        Path path = Paths.get(directoryName);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            Map<String, List<Transaction>> newAccountsToTransactions = new HashMap<String, List<Transaction>>();
            for (Path file : stream) {


                if (Files.isRegularFile(file)) {
                    String filename = file.getFileName().toString();


                    String content = Files.readString(file);


                    if (!filename.contains(" ") || !filename.endsWith(".json")) {

                        continue;
                    }

                    String name = filename.split(" ")[1].replace(".json", "");

                    newAccountsToTransactions.put(name, new ArrayList<>());
                    Type transactionListType = new TypeToken<List<Transaction>>() {
                    }.getType();
                    List<Transaction> transactions = gson.fromJson(content, transactionListType);
                    if (transactions != null) {
                        for (Transaction transaction : transactions) {

                            if (transaction instanceof Payment payment) {
                                payment.setIncomingInterest(this.incomingInterest);
                                payment.setOutgoingInterest(this.outgoingInterest);
                            }
                            newAccountsToTransactions.get(name).add(transaction);
                        }
                    }

                }
                accountsToTransactions = newAccountsToTransactions;
            }
        } catch (IOException e) {
            System.out.println("Failed to read directory: " + e.getMessage());
        }

    }


    /**
     * nimmt den Namen eines Accounts und speichert alle seiner Transaktionen in einer JSON-geeigneten Format und dann
     * überschreibt die alte JSON-Datei
     *
     * @param account Name des Kontos
     * @throws IOException if an output-related problem occurs
     */
    private void writeAccount(String account) throws IOException {


        File directory = new File(directoryName);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + directory.getAbsolutePath());
            }
        }
        String filename = "Konto " + account + ".json";
        File file = new File(directory, filename);

        try (FileWriter writer = new FileWriter(file)) {
            List<Transaction> transactions = getTransactions(account);
            if (transactions == null || transactions.isEmpty()) {
                System.out.println("Keine Transaktionen für Konto: " + account);
                return;
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Transaction.class, new TransactionJsonAdapter())
                    .create();

            JsonArray jsonArray = new JsonArray();

            for (Transaction transaction : transactions) {
                // Use the custom serializer to get the desired format
                JsonObject serializedTransaction = gson.toJsonTree(transaction, Transaction.class).getAsJsonObject();
                jsonArray.add(serializedTransaction);
            }

            writer.write(jsonArray.toString());
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file: " + e.getMessage());
        }


    }

    /**
     * Entfernt eine Datei nach einem gegebenen Namen. Das genaue Dateiformat ist "Konto + 'name' + .json"
     *
     * @param name der Name der zu entfernenden Datei
     */
    public void removeFile(String name) {
        readAccounts();
        Path path = Paths.get(directoryName + "/Konto " + name + ".json");

        try {
            Files.delete(path);  // Deletes the file
            System.out.println("File deleted successfully.");
        } catch (NoSuchFileException e) {
            System.err.println("File not found: " + e);
        } catch (DirectoryNotEmptyException e) {
            System.err.println("Directory not empty: " + e);
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }
        readAccounts();
    }

    /**
     * überprüft, ob ein account in der Bank schon existiert oder nicht
     *
     * @param account der name, den wir finden wollen
     * @return true if account exists, otherwise fasle
     */
    public boolean accountExists(String account) {
        List<String> accounts = getAllAccounts();
        boolean exists = false;
        for (String ac : accounts) {
            if (ac.equals(account)) {
                exists = true;
                break;
            }
        }
        return exists;
    }
}
