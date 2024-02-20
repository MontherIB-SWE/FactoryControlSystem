package sql.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class HelloApplication extends Application {


    static Connection connection;
    static Statement statement;
    static boolean connected = false;



    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    // Method to execute SQL queries and populate TableView
    public static void execute(String str, TableView table) {
        if (table.getId().equals("tableone")) {
            populateTableView(str, table);
        }else {
            populateTableView1(table, str);
        }
        table.refresh();

    }
    // Method to populate TableView with data from ResultSet
    public static void populateTableView(String query, TableView tableView) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try {
            ResultSet resultSet = statement.executeQuery(query);

            int columnCount = resultSet.getMetaData().getColumnCount();

            tableView.getColumns().clear();

            for (int i = 0; i < columnCount; i++) {
                final int j = i;
                javafx.scene.control.TableColumn<ObservableList<String>, String> column = new javafx.scene.control.TableColumn<>(resultSet.getMetaData().getColumnName(i + 1));
                column.setCellValueFactory(param -> {
                    return new javafx.beans.property.SimpleStringProperty(param.getValue().get(j));
                });
                tableView.getColumns().add(column);
            }

            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getString(i));
                }
                data.add(row);
            }

            tableView.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Method to populate TableView using Object array
    public static void populateTableView1(TableView tableView, String str) {
    Platform.runLater(() -> {
        try {
            ResultSet resultSet = statement.executeQuery(str);

            tableView.getItems().clear();
            tableView.getColumns().clear();
            ResultSetMetaData metaData;
            int columnCount;


            if (resultSet.isClosed()) {
                return;
            }
            metaData = resultSet.getMetaData();
            columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                final int columnIndex = i;
                TableColumn<Object[], Object> column = new TableColumn<>(metaData.getColumnName(i));
                column.setCellValueFactory(cellData ->
                        new SimpleObjectProperty<>(cellData.getValue()[columnIndex - 1])
                );
                tableView.getColumns().add(column);
            }


            List<Object[]> data = new ArrayList<>();
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                data.add(rowData);
            }
            tableView.setItems(FXCollections.observableArrayList(data));


            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}

public static void main(String[] args) {
    launch();
}
}