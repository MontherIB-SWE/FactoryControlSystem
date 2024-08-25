package sql.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class TutorialController {
    @FXML
    private StackPane tutorialPane;

    @FXML
    private Node tutorial1; // Reference to the root node of Tutorial 1 FXML
    @FXML
    private Node tutorial2; // Reference to the root node of Tutorial 2 FXML
    @FXML
    private Node tutorial3; // Reference to the root node of Tutorial 3 FXML

    private int currentTutorial = 1;

    @FXML
    public void initialize() {
        // Show the first tutorial window when the controller is initialized
        showTutorial(1);
    }

    private void showTutorial(int tutorialNumber) {
        // Hide all tutorial windows
        tutorial1.setVisible(false);
        tutorial2.setVisible(false);
        tutorial3.setVisible(false);

        // Show the requested tutorial window
        switch (tutorialNumber) {
            case 1:
                tutorial1.setVisible(true);
                break;
            case 2:
                tutorial2.setVisible(true);
                break;
            case 3:
                tutorial3.setVisible(true);
                break;
            default:
                break;
        }
    }

    @FXML
    private void onNextButtonClicked(ActionEvent event) {
        if (currentTutorial < 3) {
            currentTutorial++;
            showTutorial(currentTutorial);
        }
    }

    @FXML
    private void onPreviousButtonClicked(ActionEvent event) {
        if (currentTutorial > 1) {
            currentTutorial--;
            showTutorial(currentTutorial);
        }
    }
}
