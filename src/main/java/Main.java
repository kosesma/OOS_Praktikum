import bank.*;
import bank.exceptions.*;
import com.google.gson.*;

import java.util.*;


public class Main {
    public static void main(String[] args) {
        // Create a Payment object
        Payment payment = new Payment("2024-11-24", 1000.50, "Loan Payment", 0.3, 0.5);
        //OutgoingTransfer transfer = new OutgoingTransfer("2024-11-24", 200, "my gift for you", "Me", "you");

        // Register the custom serializer with Gson
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Payment.class, new TransactionJsonAdapter())
                .create();

        // Serialize the Payment object
        String json = gson.toJson(payment);

        // Output the JSON string
        System.out.println(json);

        // Erstellen von PrivateBank und PrivateBankAlt
        PrivateBank privateBank = new PrivateBank("MeinePrivateBank", 0.05, 0.02,"");
        PrivateBankAlt privateBankAlt = new PrivateBankAlt("MeinePrivateBankAlt", 0.05, 0.02);

        try {
            System.out.println("========== Starte Tests für die Bank-Klassen ==========");

            // 1. Kontoerstellung
            System.out.println("\n--- Test 1: Konto erstellen ---");
            System.out.println("Erstelle Konto 'TestKonto' in PrivateBank...");
            privateBank.createAccount("TestKonto");
            System.out.println("Konto 'TestKonto' erfolgreich erstellt.");

            System.out.println("Erstelle Konto 'TestKontoAlt' in PrivateBankAlt...");
            privateBankAlt.createAccount("TestKontoAlt");
            System.out.println("Konto 'TestKontoAlt' erfolgreich erstellt.");

            // 2. Konto mit Anfangstransaktionen erstellen
            System.out.println("\n--- Test 2: Konto mit Anfangstransaktionen erstellen ---");
            List<Transaction> startTransaktionen = new ArrayList<>();
            startTransaktionen.add(new Payment("10.11.2024", 500.0, "Anfangseinzahlung", 0.03, 0.01));
            System.out.println("Erstelle Konto 'TransKonto' mit einer Anfangstransaktion...");
            privateBank.createAccount("TransKonto", startTransaktionen);
            System.out.println("Konto 'TransKonto' erfolgreich mit Anfangstransaktion erstellt.");

            // 3. Doppelte Kontoerstellung testen
            System.out.println("\n--- Test 3: Doppelte Kontoerstellung ---");
            try {
                privateBank.createAccount("TestKonto");
            } catch (AccountAlreadyExistsException e) {
                System.out.println(e.getMessage());
            }

            // 4. Transaktion hinzufügen und Kontostand prüfen
            System.out.println("\n--- Test 4: Transaktionen hinzufügen und Kontostand überprüfen ---");
            Transaction einzahlung1 = new Payment("12.11.2024", 300.0, "Monatliche Einzahlung", 0.02, 0.01);
            Transaction auszahlung1 = new Payment("13.11.2024", -100.0, "Stromrechnung", 0.02, 0.01);
            Transaction einzahlung2 = new Payment("14.11.2024", 200.0, "Bonuszahlung", 0.02, 0.01);
            Transaction auszahlung2 = new Payment("15.11.2024", -50.0, "Lebensmittel", 0.02, 0.01);
            Transaction auszahlung3 = new Payment("16.11.2024", -200.0, "Miete", 0.02, 0.01);

            // Richtig instanziierte Transfer-Objekte
            Transaction incomingTransfer1 = new IncomingTransfer("17.11.2024", 150.0, "Überweisung von Max", "Max", "TestKonto");
            Transaction outgoingTransfer1 = new OutgoingTransfer("18.11.2024", 80.0, "Überweisung an Sarah", "TestKonto", "Sarah");

            // Füge Transaktionen zum TestKonto hinzu
            privateBank.addTransaction("TestKonto", einzahlung1);
            privateBank.addTransaction("TestKonto", auszahlung1);
            privateBank.addTransaction("TestKonto", einzahlung2);
            privateBank.addTransaction("TestKonto", auszahlung2);
            privateBank.addTransaction("TestKonto", auszahlung3);
            privateBank.addTransaction("TestKonto", incomingTransfer1);
            privateBank.addTransaction("TestKonto", outgoingTransfer1);

            System.out.println("Transaktionen zu 'TestKonto' hinzugefügt.");

            double kontostand = privateBank.getAccountBalance("TestKonto");
            System.out.println("Der Kontostand von 'TestKonto' nach allen Transaktionen sollte 188.0 betragen: " + kontostand);

            // 5. Nicht vorhandenes Konto bei Transaktionshinzufügung testen
            System.out.println("\n--- Test 5: Transaktion zu nicht vorhandenem Konto hinzufügen ---");
            try {
                privateBank.addTransaction("NichtExistiertKonto", einzahlung1);
            } catch (AccountDoesNotExistException e) {
                System.out.println(e.getMessage());
            }

            // 6. Überprüfung von containsTransaction
            System.out.println("\n--- Test 6: Überprüfung, ob Transaktion existiert ---");
            boolean enthaeltEinzahlung = privateBank.containsTransaction("TestKonto", einzahlung1);
            boolean enthaeltAuszahlung = privateBank.containsTransaction("TestKonto", auszahlung1);
            System.out.println("Konto 'TestKonto' enthält einzahlung1: " + enthaeltEinzahlung);
            System.out.println("Konto 'TestKonto' enthält auszahlung1: " + enthaeltAuszahlung);

            // 7. Transaktion entfernen und Kontostand erneut überprüfen
            System.out.println("\n--- Test 7: Transaktion entfernen und Kontostand überprüfen ---");
            privateBank.removeTransaction("TestKonto", einzahlung1);
            System.out.println("Einzahlung wurde aus 'TestKonto' entfernt.");

            kontostand = privateBank.getAccountBalance("TestKonto");
            System.out.println("Kontostand nach Entfernung von 'einzahlung1' soll -97.0 sein: " + kontostand);

            boolean enthaeltEinzahlung1 = privateBank.containsTransaction("TestKonto", einzahlung1);
            System.out.println("Konto 'TestKonto' enthält einzahlung1: " + enthaeltEinzahlung1);

            // 8. Abrufen aller Transaktionen
            System.out.println("\n--- Test 8: Abrufen aller Transaktionen ---");
            List<Transaction> alleTransaktionen = privateBank.getTransactions("TestKonto");
            System.out.println("Transaktionen in 'TestKonto':");
            alleTransaktionen.forEach(System.out::println);

            // 9. Sortierte Transaktionen abrufen
            System.out.println("\n--- Test 9: Sortierte Transaktionen abrufen ---");
            List<Transaction> sortierteTransaktionenAufsteigend = privateBank.getTransactionsSorted("TestKonto", true);
            System.out.println("Sortierte Transaktionen (aufsteigend) in 'TestKonto':");
            sortierteTransaktionenAufsteigend.forEach(System.out::println);


            List<Transaction> sortierteTransaktionenAbsteigend = privateBank.getTransactionsSorted("TestKonto", false);
            System.out.println("Sortierte Transaktionen (absteigend) in 'TestKonto':");
            sortierteTransaktionenAbsteigend.forEach(System.out::println);

            // 10. Filter nach Transaktionstyp
            try {
                System.out.println("\n--- Test 10: Filter nach Transaktionstyp ---");
                List<Transaction> positiveTransaktionen = privateBank.getTransactionsByType("TestKonto", true);
                List<Transaction> negativeTransaktionen = privateBank.getTransactionsByType("TestKonto", false);

                System.out.println("Positive Transaktionen in 'TestKonto':");
                positiveTransaktionen.forEach(System.out::println);

                System.out.println("\nNegative Transaktionen in 'TestKonto':");
                negativeTransaktionen.forEach(System.out::println);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            // 11. Entfernen einer nicht vorhandenen Transaktion testen
            System.out.println("\n--- Test 11: Entfernen einer nicht vorhandenen Transaktion ---");
            try {
                privateBank.removeTransaction("TestKonto", new Payment("14.11.2024", 150.0, "Nicht existierende Transaktion"));
            } catch (TransactionDoesNotExistException e) {
                System.out.println(e.getMessage());
            }
            // 12. Ungültige Transaktion testen
            System.out.println("\n--- Test 12: Hinzufügen einer ungültigen Transaktion ---");
            try {
                Transaction ungueltigeTransaktion = new Payment("15.11.2024", -500.0, "Ungültige Transaktion", 1.2, -0.5);
                privateBank.addTransaction("TestKonto", ungueltigeTransaktion);
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }

            // 13. PrivateBankAlt testen
            System.out.println("\n--- Test 13: PrivateBankAlt testen ---");

            Transaction incomingTransfer2 = new Transfer("17.11.2024", 150.0, "Überweisung von Max", "Max", "TestKontoAlt");
            Transaction outgoingTransfer2 = new Transfer("18.11.2024", 80.0, "Überweisung an Sarah", "TestKontoAlt", "Sarah");

            privateBankAlt.addTransaction("TestKontoAlt", einzahlung1);
            privateBankAlt.addTransaction("TestKontoAlt", auszahlung1);
            privateBankAlt.addTransaction("TestKontoAlt", einzahlung2);
            privateBankAlt.addTransaction("TestKontoAlt", auszahlung2);
            privateBankAlt.addTransaction("TestKontoAlt", auszahlung3);
            privateBankAlt.addTransaction("TestKontoAlt", incomingTransfer2);
            privateBankAlt.addTransaction("TestKontoAlt", outgoingTransfer2);

            // Kontostand in PrivateBankAlt prüfen
            double kontostandAlt = privateBankAlt.getAccountBalance("TestKontoAlt");
            System.out.println("\"Der Kontostand von 'TestKontoAlt' nach allen Transaktionen sollte 188.0 betragen: " + kontostandAlt);

        } catch (Exception e) {
            System.out.println("Allgemeiner Fehler: " + e.getMessage());
        }


        // Test 14: Exception-Test für invalides Amount in Transfer
        try {
            System.out.println("\n--- Test 14: Exception-Test für invalides Amount in Transfer ---");
            System.out.println("Erstelle Transfer mit negativem Amount:");
            Transfer invalidTransfer = new Transfer("12.10.2024", -100.0, "Ungültige Überweisung");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        // Test 15: Exception-Test für invalides Incoming Interest in Payment
        try {
            System.out.println("\n--- Test 15: Exception-Test für invalides Incoming Interest in Payment ---");
            System.out.println("Erstelle Payment mit incomingInterest über 1:");
            Payment invalidPaymentIncoming = new Payment("10.10.2024", 500.0, "Ungültige Zahlung", 1.5, 0.05);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        // Test 16: Exception-Test für invalides Outgoing Interest in Payment
        try {
            System.out.println("\n--- Test 16: Exception-Test für invalides Outgoing Interest in Payment ---");
            System.out.println("Erstelle Payment mit outgoingInterest unter 0:");
            Payment invalidPaymentOutgoing = new Payment("10.10.2024", 500.0, "Ungültige Zahlung", 0.05, -0.1);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        // Test 17: Equality-Test für PrivateBank-Objekte
        System.out.println("\n--- Test 17: Equality-Test für PrivateBank-Objekte ---");
        PrivateBank bank1 = new PrivateBank("Bank 1", 0.5, 0.1, "");
        PrivateBank bank2 = new PrivateBank("Bank 1", 0.5, 0.1, "");
        PrivateBank bank3 = new PrivateBank("Bank 3", 0.6, 0.15, "");

        System.out.println("Vergleiche zwei identische PrivateBank-Objekte:");
        System.out.println("bank1.equals(bank2): " + bank1.equals(bank2)); // Erwartet: true

        System.out.println("Vergleiche zwei unterschiedliche PrivateBank-Objekte:");
        System.out.println("bank1.equals(bank3): " + bank1.equals(bank3)); // Erwartet: false


        System.out.println("\n========== Alle Tests erfolgreich abgeschlossen ==========");
    }
}