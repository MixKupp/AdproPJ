package se233.projectadpro.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
    @FXML
    private Button cropBTN;
    @FXML
    private Button detectEdgeBTN;
    @FXML
    private ListView<File> listViews;

    private Map<File, CheckBox> checkBoxMap = new HashMap<>();

    @FXML
    private void initialize() {

        listViews.setCellFactory(listViews -> new ListCell<File>() {

            //set checkbox in listview
            private final CheckBox checkBox = new CheckBox();
            private final ContextMenu contextMenu = new ContextMenu();

            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) {
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    checkBox.setText(file.getName());
                    setGraphic(checkBox);
                    checkBoxMap.put(file, checkBox);

                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            listViews.getSelectionModel().select(file);
                        } else {
                            listViews.getSelectionModel().clearSelection(getIndex());
                        }
                    });

                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && !checkBox.isSelected() ) {
                            openFile(file);
                        }
                    });

                    MenuItem openItem = new MenuItem("Open");
                    openItem.setOnAction(event -> openFile(file));
                    contextMenu.getItems().clear();
                    contextMenu.getItems().add(openItem);
                    setContextMenu(contextMenu);
                }
            }
        });

        //Drag and drop
        listViews.setOnDragOver(event -> {
            if (event.getGestureSource() != listViews && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        listViews.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                List<File> files = db.getFiles();

                // Filter files to allow only .png, .jpg, .pdf
                files.stream()
                        .filter(file -> file.getName().toLowerCase().endsWith(".png") ||
                                        file.getName().toLowerCase().endsWith(".jpg") ||
                                        file.getName().toLowerCase().endsWith(".pdf") ||
                                        file.getName().toLowerCase().endsWith(".zip"))
                        .forEach(file -> {
                            listViews.getItems().add(file);
                            checkBoxMap.put(file, new CheckBox(file.getName()));
                        });
            }
            event.setDropCompleted(success);
            event.consume();
        });

        cropBTN.setOnAction(event -> {
            try{
                if(isAnyCheckboxSelected()){
                    handleCrop();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        detectEdgeBTN.setOnAction(event -> {
            try{
                handleDetectEdge();
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private boolean isAnyCheckboxSelected() {
        for (CheckBox checkBox : checkBoxMap.values()) {
            if (checkBox.isSelected()) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void handleCrop() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/se233/projectadpro/crop-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage cropStage = new Stage();
            cropStage.initModality(Modality.APPLICATION_MODAL);
            cropStage.initStyle(StageStyle.DECORATED);
            cropStage.setTitle("Crop Image");

            Scene scene = new Scene(root);
            cropStage.setScene(scene);
            cropStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDetectEdge() throws Exception {
        if (isAnyCheckboxSelected()) {
            openNewWindow("/se233/projectadpro/views/detectEdge-view.fxml", "Detect Edge");
        } else {
            showError("Please select a file");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openNewWindow(String fxmlFile, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    private void openFile(File file) {
        try {
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                openNewWindow("/se233/projectadpro/views/image-view.fxml", "Image Viewer");
            } else if (fileName.endsWith(".pdf")) {
                openNewWindow("/se233/projectadpro/views/pdf-view.fxml", "PDF Viewer");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open the file.");
        }
    }
}
