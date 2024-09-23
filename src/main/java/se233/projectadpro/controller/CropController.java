package se233.projectadpro.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CropController {

    @FXML
    private Button closeButton;

    @FXML
    private void handleClose() {
        // Close the crop window
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
