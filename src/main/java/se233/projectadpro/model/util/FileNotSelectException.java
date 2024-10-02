package se233.projectadpro.model.util;

import javafx.scene.control.Alert;

public class FileNotSelectException extends Exception{

    public FileNotSelectException(String message){
        super(message);
        showError(message);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

