package sql.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static sql.demo.HelloApplication.*;

public class HelloController implements Initializable {

    public static String Schema;
    public static String Table;

    @FXML
    private Tab tab1;
    @FXML
    private Tab tab2;
    @FXML
    private TextField userNameText;
    @FXML
    private PasswordField passwordTest;
    @FXML
    private TextField schemaNameText;
    @FXML
    private TextField tableNameText;
    @FXML
    private TextField size;
    @FXML
    private Label stopLabel;
    @FXML
    private ComboBox one;
    @FXML
    private TableView tableone;
    @FXML
    private ComboBox thread0;
    @FXML
    private ComboBox thread1;
    @FXML
    private ComboBox thread2;
    @FXML
    private ComboBox thread3;
    @FXML
    private ComboBox thread4;
    @FXML
    private ComboBox thread5;

    private PieChart pie = new PieChart();
    private BarChart<String, Number> bar = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private ArrayList<PieChart> pies = new ArrayList<>();
    private ArrayList<BarChart<String, Number>> bars = new ArrayList<>();
    private ArrayList<ComboBox> comboBoxes = new ArrayList<>();
    private String table = "mine";
    ArrayList<Thread> Threads = new ArrayList<>();
    ArrayList<TableView> tables = new ArrayList<>();
    ArrayList<Integer> logIds = new ArrayList<>();
    ArrayList<String> fromDates = new ArrayList<>();
    ArrayList<String> toDates = new ArrayList<>();

    // Initialize method called when the FXML file is loaded
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBoxes.add(thread0);
        comboBoxes.add(thread1);
        comboBoxes.add(thread2);
        comboBoxes.add(thread3);
        comboBoxes.add(thread4);
        comboBoxes.add(thread5);

        for (int i = 0; i < 6; i++) {
            comboBoxes.get(i).setDisable(true);
        }
        // Disable Tabs initially if not connected to the database
        tab1.setDisable(!connected);
        tab2.setDisable(!connected);
        // Populate the dropdown menus
        ObservableList<String> list = FXCollections.observableArrayList(
                "1. Display all logs",
                "2. Show Max LoggedValue",
                "3. Show Sum Of LoggedValue According To LogID",
                "4. Show LineID That Has The Lowest Value",
                "5. Show LineID That Has The Highest Value in date range",
                "6. Show data when LoggedValue is ZERO!"
        );

        one.getItems().addAll(list);
        thread1.getItems().addAll(list);
        thread2.getItems().addAll(list);
        thread3.getItems().addAll(list);
        thread4.getItems().addAll(list);
        thread5.getItems().addAll(list);
        thread0.getItems().addAll(list);
    }

    @FXML
    public void checkNumOfQueries(ActionEvent event) {
        try {
            if (Integer.parseInt(size.getText()) <= 6 && Integer.parseInt(size.getText()) > 0) {
                for (int i = 0; i < Integer.parseInt(size.getText()); i++) {
                    comboBoxes.get(i).setDisable(false);
                }
                for (int i = Integer.parseInt(size.getText()); i < 6; i++) {
                    comboBoxes.get(i).setDisable(true);
                }
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            for (int i = 0; i < 6; i++) {
                comboBoxes.get(i).setDisable(true);
            }
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Input exception (enter only a number between 1-6)");
            alert.showAndWait();
            return;
        }
    }

    @FXML
    public void tutorial(ActionEvent event) throws IOException {
        FXMLLoader fromFxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Tutorial.fxml"));
        Scene Scene = new Scene(fromFxmlLoader.load());
        Stage fromDialogStage = new Stage();
        fromDialogStage.setTitle("Tutorial");
        fromDialogStage.setScene(Scene);
        fromDialogStage.initModality(Modality.APPLICATION_MODAL);
        fromDialogStage.showAndWait();
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
            stopLabel.setText("Connected");
            connected = true;

            // Enable Tabs after successful connection
            tab1.setDisable(!connected);
            tab2.setDisable(!connected);

            // Check if the specified table exists
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, Table, null);
            if (!tables.next()) {
                stopLabel.setTextFill(Color.RED);
                stopLabel.setText("Table '" + Table + "' does not exist.");
                connected = false;

                // Disable tabs after connection failure
                tab1.setDisable(!connected);
                tab2.setDisable(!connected);
            }
        } catch (SQLException e) {
            stopLabel.setTextFill(Color.RED);
            stopLabel.setText("Connection Failed");
            connected = false;

            // Disable tabs after connection failure
            tab1.setDisable(!connected);
            tab2.setDisable(!connected);
        }
    }

    // Method to handle selection from the first dropdown
    @FXML
    private void dropSelect(ActionEvent event) throws IOException {
        if (one.getValue() == null) {
            return;
        }
        switch (one.getValue().toString()) {
            // Handle different selection cases
            case "1. Display all logs":
                populateTableView("select * from " + table, tableone);
                break;
            case "2. Show Max LoggedValue":
                populateTableView("SELECT LogID, LineID, LogTime, LoggedValue FROM " + table + " WHERE LoggedValue = (SELECT MAX(LoggedValue) FROM " + table + " );", tableone);
                break;
            case "3. Show Sum Of LoggedValue According To LogID":
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Enter LogID");
                dialog.setHeaderText(null);
                dialog.setContentText("Enter LogID:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                   try {
                       String logIdInput = result.get();
                       int logId = Integer.parseInt(logIdInput);
                       populateTableView("SELECT LogID, SUM(LoggedValue) AS TotalLoggedValue FROM " + table + " WHERE LogID = " + logId + ";", tableone);
                   }catch (Exception e) {
                       
                   }
                }
                break;
            case "4. Show LineID That Has The Lowest Value":
                populateChart("SELECT LineID, MIN(LoggedValue) AS MinLoggedValue FROM " + table + " GROUP BY LineID;", pie, bar);
                populateTableView("SELECT LineID, MIN(LoggedValue) AS MinLoggedValue FROM " + table + " GROUP BY LineID;", tableone);
                break;
            case "5. Show LineID That Has The Highest Value in date range":
                FXMLLoader fromFxmlLoader = new FXMLLoader(HelloApplication.class.getResource("DatePickerDialog.fxml"));
                Scene fromScene = new Scene(fromFxmlLoader.load());
                Stage fromDialogStage = new Stage();
                fromDialogStage.setTitle("Select From Date");
                fromDialogStage.setScene(fromScene);
                fromDialogStage.initModality(Modality.APPLICATION_MODAL);
                fromDialogStage.showAndWait();

                // Get the selected from date from the controller
                DatePickerController fromController = fromFxmlLoader.getController();
                LocalDate fromDate = fromController.getSelectedDate();
                if (fromDate == null) {
                    // No date selected, return or show error message
                    return;
                }
                String fromDateString = fromDate.toString() + " 00:00:00";

                // Show the date picker dialog for selecting the to date
                FXMLLoader toFxmlLoader = new FXMLLoader(HelloApplication.class.getResource("DatePickerDialog.fxml"));
                Scene toScene = new Scene(toFxmlLoader.load());
                Stage toDialogStage = new Stage();
                toDialogStage.setTitle("Select To Date");
                toDialogStage.setScene(toScene);
                toDialogStage.initModality(Modality.APPLICATION_MODAL);
                toDialogStage.showAndWait();

                // Get the selected to date from the controller
                DatePickerController toController = toFxmlLoader.getController();
                LocalDate toDate = toController.getSelectedDate();
                if (toDate == null) {
                    // No date selected, return or show error message
                    return;
                }
                String toDateString = toDate.toString() + " 23:59:59";

                // Execute the SQL query to retrieve LineID with the highest value within the selected date range
                populateChart("SELECT LineID, MAX(LoggedValue) AS MaxLoggedValue FROM " + table + " WHERE LogTime BETWEEN '" + fromDateString + "' AND '" + toDateString + "' GROUP BY LineID;", pie, bar);
                populateTableView("SELECT LineID, MAX(LoggedValue) AS MaxLoggedValue FROM " + table + " WHERE LogTime BETWEEN '" + fromDateString + "' AND '" + toDateString + "' GROUP BY LineID;", tableone);
                break;
            case "6. Show data when LoggedValue is ZERO!":
                populateTableView("SELECT * FROM " + table + " WHERE LoggedValue = 0;", tableone);
                break;
        }
    }

    // Method to handle selection from the second and third dropdowns
    @FXML
    private void Thread(ActionEvent event) throws IOException {

        // Create threads to handle dropdown selections asynchronously
        logIds.clear();
        fromDates.clear();
        toDates.clear();
        Threads.clear();

        for (int i = 0; i < 6; i++) {
            logIds.add(null);
            fromDates.add(null);
            toDates.add(null);
            pies.add(new PieChart());
            bars.add(new BarChart<>(new CategoryAxis(), new NumberAxis()));
        }

        for (int i = 0; i < Integer.parseInt(size.getText()); i++) {


            tables.add(new TableView());
            int finalI = i;

            Thread thread = new Thread(() -> {
                javafx.application.Platform.runLater(() -> {
                    try {
                        dropSelect2(comboBoxes.get(finalI), tables.get(finalI));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });

            if (comboBoxes.get(finalI).getValue() != null) {
                if (comboBoxes.get(finalI).getValue().equals("3. Show Sum Of LoggedValue According To LogID")) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Enter LogID");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Enter LogID:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        String logIdInput = result.get();
                        if (!logIdInput.isEmpty()) {
                            logIds.set(finalI, Integer.parseInt(logIdInput));
                        }
                    }
                } else if (comboBoxes.get(finalI).getValue().equals("5. Show LineID That Has The Highest Value in date range")) {
                    FXMLLoader fromFxmlLoader = new FXMLLoader(HelloApplication.class.getResource("DatePickerDialog.fxml"));
                    Scene fromScene = new Scene(fromFxmlLoader.load());
                    Stage fromDialogStage = new Stage();
                    fromDialogStage.setTitle("Select From Date");
                    fromDialogStage.setScene(fromScene);
                    fromDialogStage.initModality(Modality.APPLICATION_MODAL);
                    fromDialogStage.showAndWait();

                    // Get the selected from date from the controller
                    DatePickerController fromController = fromFxmlLoader.getController();
                    LocalDate fromDate = fromController.getSelectedDate();
                    if (fromDate == null) {
                        // No date selected, return or show error message
                        return;
                    }
                    fromDates.set(finalI, fromDate.toString() + " 00:00:00");

                    // Show the date picker dialog for selecting the to date
                    FXMLLoader toFxmlLoader = new FXMLLoader(HelloApplication.class.getResource("DatePickerDialog.fxml"));
                    Scene toScene = new Scene(toFxmlLoader.load());
                    Stage toDialogStage = new Stage();
                    toDialogStage.setTitle("Select To Date");
                    toDialogStage.setScene(toScene);
                    toDialogStage.initModality(Modality.APPLICATION_MODAL);
                    toDialogStage.showAndWait();

                    // Get the selected to date from the controller
                    DatePickerController toController = toFxmlLoader.getController();
                    LocalDate toDate = toController.getSelectedDate();
                    if (toDate == null) {
                        // No date selected, return or show error message
                        return;
                    }
                    toDates.set(finalI, toDate.toString() + " 23:59:59");
                }
            }
            Threads.add(thread);
        }

        for (
                int i = 0; i < Integer.parseInt(size.getText()); i++) {
            Threads.get(i).start();
        }
    }

    // Method to handle selection from the second and third dropdowns
    private void dropSelect2(ComboBox selection, TableView tableView) throws IOException {
        if (selection.getValue() == null) {
            return;
        }
        switch (selection.getValue().toString()) {
            // Handle different selection cases
            case "1. Display all logs":
                execute("select * from " + table + ";", tableView);
                break;
            case "2. Show Max LoggedValue":
                execute("SELECT LogID, LineID, LogTime, LoggedValue FROM " + table + " WHERE LoggedValue = (SELECT MAX(LoggedValue) FROM " + table + " );", tableView);
                break;
            case "3. Show Sum Of LoggedValue According To LogID":
                if (logIds.get(Character.getNumericValue(selection.getId().charAt(selection.getId().length() - 1))) != null) {
                    int logId = logIds.get(Character.getNumericValue(selection.getId().charAt(selection.getId().length() - 1)));
                    execute("SELECT LogID, SUM(LoggedValue) AS TotalLoggedValue FROM " + table + " WHERE LogID = " + logId + ";", tableView);
                }
                break;
            case "4. Show LineID That Has The Lowest Value":

                populateChart("SELECT LineID, MIN(LoggedValue) AS MinLoggedValue FROM " + table + " GROUP BY LineID;", pies.get(Character.getNumericValue(selection.getId().charAt(selection.getId().length() - 1))), bars.get(Character.getNumericValue(selection.getId().charAt(selection.getId().length() - 1))));
                break;
            case "5. Show LineID That Has The Highest Value in date range":
                String fromDate = fromDates.get(Character.getNumericValue(selection.getId().charAt(selection.getId().length() - 1)));
                String toDate = toDates.get(Character.getNumericValue(selection.getId().charAt(selection.getId().length() - 1)));
                populateChart("SELECT LineID, MAX(LoggedValue) AS MaxLoggedValue FROM " + table + " WHERE LogTime BETWEEN '" + fromDate + "' AND '" + toDate + "' GROUP BY LineID;", pies.get(Character.getNumericValue(selection.getId().charAt(selection.getId().length() - 1))), bars.get(Character.getNumericValue(selection.getId().charAt(selection.getId().length() - 1))));
                break;
            case "6. Show data when LoggedValue is ZERO!":
                execute("SELECT * FROM " + table + " WHERE LoggedValue = 0;", tableView);
                break;
        }
    }
}
