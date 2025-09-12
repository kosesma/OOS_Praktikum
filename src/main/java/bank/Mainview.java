package bank;

import bank.exceptions.AccountAlreadyExistsException;
import bank.exceptions.AccountDoesNotExistException;
import bank.exceptions.TransactionAlreadyExistException;
import bank.exceptions.TransactionAttributeException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mainview {

    Stage window;
    PrivateBank bank = new PrivateBank("Test Bank", 0.1, 0.2, "Bank Directory");
    @FXML
    private ListView<String> listView;
    @FXML
    private MenuItem auswahlButton;
    @FXML
    private ContextMenu contextMenu;


/*
    @Override
    public void start(Stage primaryStage) throws IOException, AccountAlreadyExistsException {


        window = primaryStage;
        window.setTitle("OOS Praktikum 5");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Mainview.fxml"));
        Parent root = loader.load();
        Mainview controller = loader.getController();

        //test eingaben

        { // Create and pass a PrivateBank instance to the controller
            Transaction einzahlung1 = new Payment("12.11.2024", 300.0, "Monatliche Einzahlung", 0.02, 0.01);
            Transaction auszahlung1 = new Payment("13.11.2024", -100.0, "Stromrechnung", 0.02, 0.01);
            Transaction einzahlung2 = new Payment("14.11.2024", 200.0, "Bonuszahlung", 0.02, 0.01);
            Transaction auszahlung2 = new Payment("15.11.2024", -50.0, "Lebensmittel", 0.02, 0.01);
            Transaction auszahlung3 = new Payment("16.11.2024", -200.0, "Miete", 0.02, 0.01);

            // Richtig instanziierte Transfer-Objekte
            Transaction incomingTransfer1 = new IncomingTransfer("17.11.2024", 150.0, "Überweisung von Max", "Max", "TestKonto");
            Transaction outgoingTransfer1 = new OutgoingTransfer("18.11.2024", 80.0, "Überweisung an Sarah", "TestKonto", "Sarah");
            if (!bank.accountExists("John"))
                bank.createAccount("John");
            if (!bank.accountExists("Alice"))
                bank.createAccount("Alice");
            if (!bank.accountExists("David"))
                bank.createAccount("David");
            try {
                bank.addTransaction("David", einzahlung1);
                bank.addTransaction("David", einzahlung2);
                bank.addTransaction("David", auszahlung3);
                bank.addTransaction("David", auszahlung2);
                bank.addTransaction("David", auszahlung1);
                bank.addTransaction("David", incomingTransfer1);
                bank.addTransaction("David", outgoingTransfer1);

                bank.addTransaction("Alice", einzahlung1);
                bank.addTransaction("Alice", einzahlung2);
                bank.addTransaction("Alice", auszahlung3);
                bank.addTransaction("Alice", auszahlung2);
                bank.addTransaction("Alice", auszahlung1);
                bank.addTransaction("Alice", incomingTransfer1);
                bank.addTransaction("Alice", outgoingTransfer1);

                bank.addTransaction("John", einzahlung1);
                bank.addTransaction("John", einzahlung2);
                bank.addTransaction("John", auszahlung3);
                bank.addTransaction("John", auszahlung2);
                bank.addTransaction("John", auszahlung1);
                bank.addTransaction("John", incomingTransfer1);
                bank.addTransaction("John", outgoingTransfer1);

            } catch (TransactionAlreadyExistException | AccountDoesNotExistException |
                     TransactionAttributeException e) {
                throw new RuntimeException(e);
            }
        }

        //////////////

        controller.initialize(bank);  // Explicitly call initialize with the bank instance


        // Set up the stage and scene
        Scene scene = new Scene(root);


        window.setScene(scene);
        window.show();
    }
*/

    public void initialize(PrivateBank bank) {
        listView.getItems().clear();
        List<String> accounts = bank.getAllAccounts();
        ObservableList<String> accountsObservableList = FXCollections.observableArrayList(accounts);
        listView.setItems(accountsObservableList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    public void auswahlAction(ActionEvent event) throws IOException {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accountview.fxml"));
        Parent root = loader.load();

        String accountName = listView.getSelectionModel().getSelectedItem();

        Accountview controller = loader.getController();
        controller.setBalance(String.valueOf(bank.getAccountBalance(accountName)));
        controller.setAccountName(accountName);
        controller.setBank(bank);
        controller.showTransactions(bank, accountName);
        Scene scene = new Scene(root);

        Stage stage = (Stage) contextMenu.getOwnerWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void loescheAction(ActionEvent event) throws AccountDoesNotExistException, IOException {
        String accountName = (listView.getSelectionModel().getSelectedItem());

        //creates errors because readAccount inside deleteAccount is getting a null directoryName
        //!!!! make readAccount get a directoryName because global Bank variable in here is useless F U chatGPT!!

        bank.deleteAccount(accountName);
        initialize(bank);
    }
    @FXML
    public void addAccount(ActionEvent event){
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add Account");

        VBox vbox = new VBox(10);
        Text text = new Text("Konto Inhaber:");
        vbox.getChildren().add(text);
        TextField accountName = new TextField();
        accountName.setPromptText("Enter Account Name");
        vbox.getChildren().add(accountName);
        Button okButton = new Button("OK");
        okButton.setOnAction((ActionEvent event1) -> {
            if(accountName.getText().isEmpty()){
                popupStage.close();
                return;
            }
            if (bank.accountExists(accountName.getText())) {
                popupStage.close();
                return;
            }
            try {
                bank.createAccount(accountName.getText());
                initialize(bank);
            } catch (AccountAlreadyExistsException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            popupStage.close();
        });
        vbox.getChildren().add(okButton);
        Scene scene = new Scene(vbox, 300, 100);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

}
