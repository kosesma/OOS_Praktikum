package bank;

import bank.exceptions.AccountDoesNotExistException;
import bank.exceptions.TransactionAlreadyExistException;
import bank.exceptions.TransactionAttributeException;
import bank.exceptions.TransactionDoesNotExistException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class Accountview {
    @FXML
    private Text balance;
    @FXML
    private Text accountName;
    @FXML
    private Button backButton;
    @FXML
    private ListView<Transaction> listView;
    private PrivateBank bank;

    public void setBank(PrivateBank bank) {
        this.bank = bank;
    }

    public void setAccountName(String accountName) {
        this.accountName.setText(accountName);
    }

    public void setBalance(String balance) {
        this.balance.setText(balance);
    }

    private void populateList(List<Transaction> transactionList) {
        listView.getItems().clear();
        for (Transaction transaction : transactionList) {
            listView.getItems().add(transaction);
        }
        ObservableList<Transaction> transactionsObservableList = FXCollections.observableArrayList(listView.getItems());
        listView.setItems(transactionsObservableList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void showTransactions(PrivateBank bank, String accountName) {
        List<Transaction> transactionList = bank.getTransactions(accountName);
        populateList(transactionList);
    }

    @FXML
    private void aufsteigendSortieren(ActionEvent event) {
        List<Transaction> transactionList = bank.getTransactionsSorted(accountName.getText(), true);
        populateList(transactionList);
    }

    @FXML
    private void absteigendSortieren(ActionEvent event) {
        List<Transaction> transactionList = bank.getTransactionsSorted(accountName.getText(), false);
        populateList(transactionList);
    }

    @FXML
    private void positiveTransaktionen(ActionEvent event) {
        List<Transaction> transactionList = bank.getTransactionsByType(accountName.getText(), true);
        populateList(transactionList);
    }

    @FXML
    private void negativeTransaktionen(ActionEvent event) {
        List<Transaction> transactionList = bank.getTransactionsByType(accountName.getText(), false);
        populateList(transactionList);
    }

    @FXML
    private void loeschenPopup(ActionEvent event) throws AccountDoesNotExistException, TransactionDoesNotExistException, IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Löschen");
        alert.setHeaderText(null);
        alert.setContentText("Möchten Sie diese Transaktion wirklich löschen?");
        alert.initOwner(accountName.getScene().getWindow());
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Transaction transaction = (listView.getSelectionModel().getSelectedItem());
            bank.removeTransaction(accountName.getText(), transaction);
            populateList(bank.getTransactions(accountName.getText()));
            setBalance(String.valueOf(bank.getAccountBalance(accountName.getText())));

        } else {
            // Cancel the deletion
        }

    }

    @FXML
    private void transaktionAddieren(ActionEvent event) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add Transaction");

        VBox vbox = new VBox(10);

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton payment = new RadioButton("Payment");
        RadioButton transfer = new RadioButton("Transfer");

        payment.setToggleGroup(toggleGroup);
        transfer.setToggleGroup(toggleGroup);

        payment.setSelected(true);

        vbox.getChildren().addAll(payment, transfer);

        HBox transferBox = new HBox(10);
        /*
        HBox paymentBox = new HBox(10);
        TextField incomingInterest = new TextField("Incoming Interest");
        TextField outgoingInterest = new TextField("Outgoing Interest");
        paymentBox.getChildren().addAll(incomingInterest, outgoingInterest);
        */
        TextField sender = new TextField("Sender");
        TextField receiver = new TextField("Receiver");
        transferBox.getChildren().addAll(sender, receiver);

        disableFields(transferBox);
        toggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == payment) {
                disableFields(transferBox);
//            enableFields(paymentBox);
            }
            else {
//            disableFields(paymentBox);
                enableFields(transferBox);
            }
        });

        TextField amount = new TextField("Amount");

        // force the field to be numeric only
        amount.addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, actionevent -> {

            if (payment.isSelected() && !actionevent.getCharacter().matches("[0-9]") && !actionevent.getCharacter().equals(".") && !actionevent.getCharacter().equals("-")) {
                actionevent.consume();  // Consume the event, blocking invalid characters
            }
            if (transfer.isSelected() && !actionevent.getCharacter().matches("[0-9]") && !actionevent.getCharacter().equals(".")) {
                actionevent.consume();  // Consume the event, blocking invalid characters
            }
        });
        TextField beschreibung = new TextField("Beschreibung");

        vbox.getChildren().addAll(/*paymentBox,*/ transferBox, amount, beschreibung);

        // Add a Submit Button to confirm the transaction
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            RadioButton selectedOption = (RadioButton) toggleGroup.getSelectedToggle();
            if (Objects.equals(amount.getText(), "Amount") || Objects.equals(receiver.getText(), "") || Objects.equals(sender.getText(), "") || Objects.equals(amount.getText(), "") || (selectedOption == transfer && (Objects.equals(sender.getText(), accountName.getText()) || Objects.equals(receiver.getText(), accountName.getText()))))
            {
                popupStage.close();
                return;
            }
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            String dateTimeString = currentDateTime.format(formatter);
            if (selectedOption == payment) {
                System.out.println("Selected option: " + selectedOption.getText());
                // Process the selected option here

                try {
                    bank.addTransaction(accountName.getText(), new Payment(dateTimeString, Double.parseDouble(amount.getText()), beschreibung.getText(), 0,0));
                    setBalance(String.valueOf(bank.getAccountBalance(accountName.getText())));
                    populateList(bank.getTransactions(accountName.getText()));
                } catch (TransactionAlreadyExistException ex) {
                    throw new RuntimeException(ex);
                } catch (AccountDoesNotExistException ex) {
                    throw new RuntimeException(ex);
                } catch (TransactionAttributeException ex) {
                    throw new RuntimeException(ex);
                }

            } else if (selectedOption == transfer) {
                if (sender.getText().equals(accountName.getText())) {



                    try {
                        bank.addTransaction(accountName.getText(), new OutgoingTransfer(dateTimeString, Double.parseDouble(amount.getText()), beschreibung.getText(), sender.getText(), receiver.getText()));
                        setBalance(String.valueOf(bank.getAccountBalance(accountName.getText())));
                        populateList(bank.getTransactions(accountName.getText()));
                    } catch (TransactionAlreadyExistException ex) {
                        throw new RuntimeException(ex);
                    } catch (AccountDoesNotExistException ex) {
                        throw new RuntimeException(ex);
                    } catch (TransactionAttributeException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {

                    try {
                        bank.addTransaction(accountName.getText(), new IncomingTransfer(dateTimeString, Double.parseDouble(amount.getText()), beschreibung.getText(), sender.getText(), receiver.getText()));
                        setBalance(String.valueOf(bank.getAccountBalance(accountName.getText())));
                        populateList(bank.getTransactions(accountName.getText()));
                    } catch (TransactionAlreadyExistException ex) {
                        throw new RuntimeException(ex);
                    } catch (AccountDoesNotExistException ex) {
                        throw new RuntimeException(ex);
                    } catch (TransactionAttributeException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            popupStage.close();
        });

        vbox.getChildren().add(submitButton);

        Scene scene = new Scene(vbox, 300, 200);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void disableFields(HBox hbox) {
        for (javafx.scene.Node node : hbox.getChildren()) {
            node.setDisable(true);  // Disable the TextField
        }
    }
    private void enableFields(HBox hbox) {
        for (javafx.scene.Node node : hbox.getChildren()) {
            node.setDisable(false);
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Mainview.fxml"));
        Parent root = loader.load();


        Mainview controller = loader.getController();
        controller.initialize(bank);

        Scene scene = new Scene(root);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
