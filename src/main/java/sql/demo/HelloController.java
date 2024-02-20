package sql.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import static sql.demo.HelloApplication.*;

public class HelloController implements Initializable {

    public static String Schema;
    public static String Table;

    @FXML
    private TextField userNameText;

    @FXML
    private PasswordField passwordTest;

    @FXML
    private TextField schemaNameText;

    @FXML
    private TextField tableNameText;
    @FXML
    private Label stopLabel;

    @FXML
    private ComboBox one;
    @FXML
    private ComboBox oneone;
    @FXML
    private ComboBox onetwo;
    @FXML
    private TableView tableone;
    @FXML
    private TableView tableoneone;
    @FXML
    private TableView tableonetwo;
    private String table = "mine";
    private Optional<String> fromResult1 = Optional.empty();
    private Optional<String> fromResult2 = Optional.empty();
    private Optional<String> toResult1 = Optional.empty();
    private Optional<String> toResult2 = Optional.empty();
    private Optional<String> result2 = Optional.empty();
    private Optional<String> result1 = Optional.empty();

    // Initialize method called when the FXML file is loaded
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Disable dropdowns initially if not connected to the database

        one.setDisable(!HelloApplication.connected);
        oneone.setDisable(!HelloApplication.connected);
        onetwo.setDisable(!HelloApplication.connected);
        // Populate the dropdown menus

        ObservableList<String> list = FXCollections.observableArrayList("1. Display all logs", "2. Show Max LoggedValue"
                , "3. Show Sum Of LoggedValue According To LogID", "4. Show LineID That Has The Lowest Value"
                , "5. Show LineID That Has The Highest Value in date range", "6. Show data when LoggedValue is ZERO!");

        if (one != null) {
            one.getItems().addAll(list);
        }
        if (oneone != null) {

            oneone.getItems().addAll(list);
        }
        if (onetwo != null) {

            onetwo.getItems().addAll(list);
        }


    }

    // Method to handle database connection
    @FXML
    public void connectToDataBase(ActionEvent event) {
        Duration duration = Duration.seconds(3);
        try {
            String user, password;
            user = userNameText.getText().toString();
            password = passwordTest.getText().toString();
            Schema = schemaNameText.getText().toString();
            Table = tableNameText.getText().toString();

            String url = "jdbc:mysql://localhost:3306/" + Schema;

            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            stopLabel.setTextFill(Color.GREEN);
            stopLabel.setText("Connected successfully");
            connected = true;

            // Enable dropdowns after successful connection
            one.setDisable(!HelloApplication.connected);
            oneone.setDisable(!HelloApplication.connected);
            onetwo.setDisable(!HelloApplication.connected);

            // Check if the specified table exists
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, Table, null);
            if (!tables.next()) {
                stopLabel.setTextFill(Color.RED);
                stopLabel.setText("Table '" + Table + "' does not exist.");
                connected = false;

                // Disable dropdowns after connection failure
                one.setDisable(!HelloApplication.connected);
                oneone.setDisable(!HelloApplication.connected);
                onetwo.setDisable(!HelloApplication.connected);
            }
        } catch (SQLException var6) {
            stopLabel.setTextFill(Color.RED);
            stopLabel.setText("A communication error has occurred. Try again");
            connected = false;

            // Disable dropdowns after connection failure

            one.setDisable(!HelloApplication.connected);
            oneone.setDisable(!HelloApplication.connected);
            onetwo.setDisable(!HelloApplication.connected);


        }
    }

    // Method to handle selection from the first dropdown
    @FXML
    private void dropSelect(ActionEvent event) {
        switch (one.getValue().toString()) {

            // Handle different selection cases
            case "1. Display all logs":
                HelloApplication.execute("select * from " + table, tableone);

                break;
            case "2. Show Max LoggedValue":
                HelloApplication.execute("SELECT LogID, LineID, LogTime, LoggedValue FROM " + table + " WHERE LoggedValue = (SELECT MAX(LoggedValue) FROM " + table + " );", tableone);
                break;
            case "3. Show Sum Of LoggedValue According To LogID":
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Enter LogID");
                dialog.setHeaderText(null);
                dialog.setContentText("Enter LogID:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String logIdInput = result.get();
                    int logId = Integer.parseInt(logIdInput);
                    HelloApplication.execute("SELECT LogID, SUM(LoggedValue) AS TotalLoggedValue FROM " + table + " WHERE LogID = " + logId + ";", tableone);
                }
                break;
            case "4. Show LineID That Has The Lowest Value":
                HelloApplication.execute("SELECT LineID, MIN(LoggedValue) AS MinLoggedValue FROM " + table + " GROUP BY LineID;", tableone);
                break;
            case "5. Show LineID That Has The Highest Value in date range":
                TextInputDialog fromDateDialog = new TextInputDialog();
                fromDateDialog.setTitle("Enter From Date");
                fromDateDialog.setHeaderText(null);
                fromDateDialog.setContentText("Enter From Date (YYYY-MM-DD HH:mm:ss):");

                TextInputDialog toDateDialog = new TextInputDialog();
                toDateDialog.setTitle("Enter To Date");
                toDateDialog.setHeaderText(null);
                toDateDialog.setContentText("Enter To Date (YYYY-MM-DD HH:mm:ss):");

                Optional<String> fromResult = fromDateDialog.showAndWait();
                Optional<String> toResult = toDateDialog.showAndWait();

                if (fromResult.isPresent() && toResult.isPresent()) {
                    String fromDate = fromResult.get();
                    String toDate = toResult.get();

                    HelloApplication.populateTableView("SELECT LineID, MAX(LoggedValue) AS MaxLoggedValue FROM " + table + " WHERE LogTime BETWEEN '" + fromDate + "' AND '" + toDate + "' GROUP BY LineID;", tableone);

                }
                break;
            case "6. Show data when LoggedValue is ZERO!":
                HelloApplication.execute("SELECT * FROM " + table + " WHERE LoggedValue = 0;", tableone);
                break;
        }
    }
    // Method to handle selection from the second and third dropdowns

    @FXML
    private void dropSelect1(ActionEvent event) {
        // Create threads to handle dropdown selections asynchronously
        Thread thread1 = new Thread(() -> dropSelect2(oneone, tableoneone));
        thread1.setName("1");
        thread1.setDaemon(true);


        Thread thread2 = new Thread(() -> dropSelect2(onetwo, tableonetwo));
        thread2.setName("2");
        thread2.setDaemon(true);

        // Handle special cases for dropdown selections
        if (oneone.getValue().toString().equals("3. Show Sum Of LoggedValue According To LogID")) {
            // Show dialog for LogID input
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Enter LogID");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter LogID:");

            result1 = dialog.showAndWait();

        } else if (oneone.getValue().toString().equals("5. Show LineID That Has The Highest Value in date range")) {
            // Show dialogs for date range input
            TextInputDialog fromDateDialog = new TextInputDialog();
            fromDateDialog.setTitle("Enter From Date");
            fromDateDialog.setHeaderText(null);
            fromDateDialog.setContentText("Enter From Date (YYYY-MM-DD HH:mm:ss):");

            TextInputDialog toDateDialog = new TextInputDialog();
            toDateDialog.setTitle("Enter To Date");
            toDateDialog.setHeaderText(null);
            toDateDialog.setContentText("Enter To Date (YYYY-MM-DD HH:mm:ss):");

            fromResult1 = fromDateDialog.showAndWait();
            toResult1 = toDateDialog.showAndWait();

        }
        // Similar handling for the second dropdown
        if (onetwo.getValue().toString().equals("3. Show Sum Of LoggedValue According To LogID")) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Enter LogID");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter LogID:");

            result2 = dialog.showAndWait();

        } else if (onetwo.getValue().toString().equals("5. Show LineID That Has The Highest Value in date range")) {
            TextInputDialog fromDateDialog = new TextInputDialog();
            fromDateDialog.setTitle("Enter From Date");
            fromDateDialog.setHeaderText(null);
            fromDateDialog.setContentText("Enter From Date (YYYY-MM-DD HH:mm:ss):");

            TextInputDialog toDateDialog = new TextInputDialog();
            toDateDialog.setTitle("Enter To Date");
            toDateDialog.setHeaderText(null);
            toDateDialog.setContentText("Enter To Date (YYYY-MM-DD HH:mm:ss):");

            fromResult2 = fromDateDialog.showAndWait();
            toResult2 = toDateDialog.showAndWait();

        }

        // Start threads for handling dropdown selections
        thread1.start();
        thread2.start();

        try {
            // Wait for threads to finish execution
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    // Method to handle selection from the second and third dropdowns
    private void dropSelect2(ComboBox selection, TableView tableView) {
        if (selection.getValue() == null) {
            return;
        }
        switch (selection.getValue().toString()) {
            // Handle different selection cases
            case "1. Display all logs":
                HelloApplication.execute("select * from " + table + ";", tableView);
                break;
            case "2. Show Max LoggedValue":
                HelloApplication.execute("SELECT LogID, LineID, LogTime, LoggedValue FROM " + table + " WHERE LoggedValue = (SELECT MAX(LoggedValue) FROM " + table + " );", tableView);
                break;
            case "3. Show Sum Of LoggedValue According To LogID":

                if (result1.isPresent() && result2.isPresent()) {
                    String logIdInput1 = result2.get();
                    String logIdInput = result1.get();
                    int logId;
                    int logId1;
                    if (logIdInput.equals("") || logIdInput1.equals("")) {
                        return;
                    }

                    try {
                        logId = Integer.parseInt(logIdInput);
                        logId1 = Integer.parseInt(logIdInput1);
                    }catch (NumberFormatException e){
                        return;
                    }

                    HelloApplication.execute("SELECT LogID, SUM(LoggedValue) AS TotalLoggedValue FROM " + table + " WHERE LogID = " + logId + ";", tableView);

                    HelloApplication.execute("SELECT LogID, SUM(LoggedValue) AS TotalLoggedValue FROM " + table + " WHERE LogID = " + logId1 + ";", tableonetwo);


                } else if (result2.isPresent()) {
                    String logIdInput = result2.get();
                    int logId;
                    if (logIdInput.equals("")) {
                        return;
                    }
                    try {
                        logId = Integer.parseInt(logIdInput);
                    } catch (NumberFormatException e) {
                        return;
                    }
                    HelloApplication.execute("SELECT LogID, SUM(LoggedValue) AS TotalLoggedValue FROM " + table + " WHERE LogID = " + logId + ";", tableView);

                } else if (result1.isPresent()) {
                    String logIdInput = result1.get();
                    int logId;
                    if (logIdInput.equals("")) {
                        return;
                    }
                    try {
                        logId = Integer.parseInt(logIdInput);
                    } catch (NumberFormatException e) {
                        return;
                    }
                    HelloApplication.execute("SELECT LogID, SUM(LoggedValue) AS TotalLoggedValue FROM " + table + " WHERE LogID = " + logId + ";", tableView);

                }
                break;
            case "4. Show LineID That Has The Lowest Value":
                HelloApplication.execute("SELECT LineID, MIN(LoggedValue) AS MinLoggedValue FROM " + table + " GROUP BY LineID;", tableView);
                break;
            case "5. Show LineID That Has The Highest Value in date range":

                if ((fromResult1.isPresent() && toResult1.isPresent()) && (fromResult2.isPresent() && toResult2.isPresent())) {
                    String fromDate1 = fromResult2.get();
                    String toDate1 = toResult2.get();
                    String fromDate = fromResult1.get();
                    String toDate = toResult1.get();

                    HelloApplication.execute("SELECT LineID, MAX(LoggedValue) AS MaxLoggedValue FROM " + table + " WHERE LogTime BETWEEN '" + fromDate + "' AND '" + toDate + "' GROUP BY LineID;", tableView);
                    HelloApplication.execute("SELECT LineID, MAX(LoggedValue) AS MaxLoggedValue FROM " + table + " WHERE LogTime BETWEEN '" + fromDate1 + "' AND '" + toDate1 + "' GROUP BY LineID;", tableonetwo);

                } else if (fromResult2.isPresent() && toResult2.isPresent()) {
                    String fromDate = fromResult2.get();
                    String toDate = toResult2.get();
                    HelloApplication.execute("SELECT LineID, MAX(LoggedValue) AS MaxLoggedValue FROM " + table + " WHERE LogTime BETWEEN '" + fromDate + "' AND '" + toDate + "' GROUP BY LineID;", tableView);

                } else if (fromResult1.isPresent() && toResult1.isPresent()) {
                    String fromDate = fromResult1.get();
                    String toDate = toResult1.get();
                    HelloApplication.execute("SELECT LineID, MAX(LoggedValue) AS MaxLoggedValue FROM " + table + " WHERE LogTime BETWEEN '" + fromDate + "' AND '" + toDate + "' GROUP BY LineID;", tableView);

                }
                break;
            case "6. Show data when LoggedValue is ZERO!":
                HelloApplication.execute("SELECT * FROM " + table + " WHERE LoggedValue = 0;", tableView);
                break;
        }
    }

}