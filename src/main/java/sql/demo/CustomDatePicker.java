package sql.demo;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CustomDatePicker extends DatePicker {

    private LocalDate fromDate;
    private LocalDate toDate;

    public CustomDatePicker(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        setTooltip(new Tooltip("Select a date within the specified range"));
        setDayCellFactory(this::createDayCell);
    }

    private DateCell createDayCell(DatePicker datePicker) {
        return new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(fromDate) || item.isAfter(toDate)) {
                    setDisable(true); // Disable dates outside the specified range
                }
            }
        };
    }
}
