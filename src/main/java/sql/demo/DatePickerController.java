package sql.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.time.LocalDate;

public class DatePickerController {
    @FXML
    private DatePicker datePicker;

    private LocalDate selectedDate;

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    @FXML
    private void okButtonClicked(ActionEvent event) {
        selectedDate = datePicker.getValue();
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }
}
