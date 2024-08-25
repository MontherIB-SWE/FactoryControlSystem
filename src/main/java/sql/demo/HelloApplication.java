package sql.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    public static void execute(String query, TableView table) {

        populateTableView1(table, query);
        table.refresh();
    }

    public static void populateChart(String query, PieChart pieChart, BarChart barChart) throws IOException {
        // Clear existing data in the pie chart
        pieChart.getData().clear();
        barChart.getData().clear();

        // Retrieve data from the database and populate the pie chart
        try {

            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            pieChart.setTitle(rsmd.getColumnName(2));
            // Populate the pie chart with data from the result set
            while (rs.next()) {
                String category = rs.getString(1);
                double value = rs.getDouble(2);
                pieChart.getData().add(new PieChart.Data(category, value));
            }


            rs = statement.executeQuery(query);
            rsmd = rs.getMetaData();
            barChart.setTitle(rsmd.getColumnName(2));
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (rs.next()) {
                String category = rs.getString(1);
                double value = rs.getDouble(2);
                series.getData().add(new XYChart.Data<>(category, value));
            }
            barChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        VBox root = new VBox();
        root.getChildren().addAll(new HBox(barChart, pieChart));
        Stage stage = new Stage();
        stage.setTitle("Charts");
        stage.setScene(new Scene(root));
        stage.show();

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
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(resultSet.getMetaData().getColumnName(i + 1));
                column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
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
    public static void populateTableView1(TableView tableView, String query) {
        Platform.runLater(() -> {
            try {
                ResultSet resultSet = statement.executeQuery(query);

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
                showTableInNewWindow(tableView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void showTableInNewWindow(TableView tableView) {
        // Create a new stage
        Stage stage = new Stage();
        stage.setTitle("Table View");

        // Create a new VBox to hold the table
        VBox root = new VBox();
        root.getChildren().add(tableView);

        // Set the scene
        Scene scene = new Scene(root);

        // Set the scene and show the stage
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
