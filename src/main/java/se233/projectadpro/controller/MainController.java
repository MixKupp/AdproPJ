package se233.projectadpro.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
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

                    MenuItem openItem = new MenuItem("Open");
                    openItem.setOnAction(event -> openFile(file));
                    contextMenu.getItems().clear();
                    contextMenu.getItems().add(openItem);
                    setContextMenu(contextMenu);

                    MenuItem deleteItem = new MenuItem("Delete");
                    deleteItem.setOnAction(event -> {listViews.getItems().remove(file);});
                    contextMenu.getItems().add(deleteItem);
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
//                                        file.getName().toLowerCase().endsWith(".pdf") ||
                                        file.getName().toLowerCase().endsWith(".zip"))
                        .forEach(file -> {
                            listViews.getItems().add(file);
                            checkBoxMap.put(file, new CheckBox(file.getPath()));
                        });
            }
            event.setDropCompleted(success);
            event.consume();
        });

        cropBTN.setOnAction(event -> {
            try{
                handleCrop();
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

    private ArrayList<File> getSelectedFiles() {
        ArrayList<File> selectedFiles = new ArrayList<>();

        // Iterate through the checkBoxMap
        for (Map.Entry<File, CheckBox> entry : checkBoxMap.entrySet()) {
            File file = entry.getKey();
            CheckBox checkBox = entry.getValue();

            // If the checkbox is selected, add the file to the list
            if (checkBox.isSelected()) {
                selectedFiles.add(file);
            }
        }

        return selectedFiles;
    }

    @FXML
    private void handleCrop() {
        if (!isAnyCheckboxSelected()) { showError("Please select a file"); return; }

        try {
            cropBTN.setDisable(true);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se233/projectadpro/crop-view.fxml"));
            Parent root = loader.load();

            CropViewController cropViewController = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Crop Pane");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> cropBTN.setDisable(false));
            cropViewController.setCurrentStage(stage);
            cropViewController.setImageList(getSelectedFiles());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDetectEdge() {
        if (!isAnyCheckboxSelected()) { showError("Please select a file"); return; }

        try {
            detectEdgeBTN.setDisable(true);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se233/projectadpro/edge-view.fxml"));
            Parent root = loader.load();

            EdgeViewController edgeViewController = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Detect Edge");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> detectEdgeBTN.setDisable(false));
            stage.setResizable(false);
            edgeViewController.setImageList(getSelectedFiles());
            edgeViewController.setCurrentStage(stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openFile(File file) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se233/projectadpro/image-view.fxml"));
            Parent root = loader.load();

            ImageViewController imageViewController = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Image View");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            imageViewController.setCurrentStage(stage);
            imageViewController.setImageView(new Image(file.toURI().toString()));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
