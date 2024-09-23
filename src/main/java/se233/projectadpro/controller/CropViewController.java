package se233.projectadpro.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import se233.projectadpro.Launcher;
import se233.projectadpro.model.ImageCropTask;
import se233.projectadpro.model.ResizableRectangle;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CropViewController {
    @FXML
    private ImageView imageView;
    @FXML
    private Button confirmButton;
    @FXML
    private Canvas canvas;
    @FXML
    private AnchorPane anchorPane;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private ArrayList<File> selectedFilesList = new ArrayList<>();
    private int currentImgIndex = 0;
    private double maxWidth = 600;
    private double maxHeight = 500;
    private double minWidth = 400;
    private double minHeight = 400;

    private Stage currentStage;

    @FXML
    public void initialize() {
        ResizableRectangle resizableRectangle = new ResizableRectangle(0, 0, 200, 200, canvas);
        resizableRectangle.drawRectangle();

        confirmButton.setOnAction(event -> {
            if (currentImgIndex == selectedFilesList.size() - 1) {
                System.out.println("Final Image Reach");
                ImageCropTask imageCropTask = new ImageCropTask(selectedFilesList.get(currentImgIndex), (int) resizableRectangle.getX(), (int) resizableRectangle.getY(), (int) resizableRectangle.getWidth(), (int) resizableRectangle.getHeight(), currentStage);
                executorService.submit(imageCropTask);
                currentStage.close();
            }
            if (currentImgIndex != selectedFilesList.size() - 1) {
                currentImgIndex++;
                setImageView(new Image(selectedFilesList.get(currentImgIndex).toURI().toString()));
            }
        });
    }

    public void setImageList(ArrayList<File> selectedFilesList) {
        this.selectedFilesList = selectedFilesList;
        currentImgIndex = 0;
        setImageView(new Image(selectedFilesList.get(currentImgIndex).toURI().toString()));
    }

    public void setImageView(Image image) {
        double width = image.getWidth();
        double height = image.getHeight();

        // Check if the image is smaller than the minimum size
        if (width < minWidth || height < minHeight) {
            double aspectRatio = width / height;
            if (aspectRatio > 1) { // Wider than tall
                width = minWidth;
                height = minWidth / aspectRatio;
            } else { // Taller than wide
                height = minHeight;
                width = minHeight * aspectRatio;
            }
        } else {
            // Scale down if necessary
            if (width > maxWidth || height > maxHeight) {
                double aspectRatio = width / height;
                if (aspectRatio > 1) { // Wider than tall
                    width = maxWidth;
                    height = maxWidth / aspectRatio;
                } else { // Taller than wide
                    height = maxHeight;
                    width = maxHeight * aspectRatio;
                }
            }
        }

        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        anchorPane.setPrefHeight(height+100);
        anchorPane.setPrefWidth(width+100);
        canvas.setWidth(width);
        canvas.setHeight(height);

        confirmButton.setLayoutX((width - confirmButton.getWidth()) / 1.75);
        confirmButton.setLayoutY(height + 50);

        imageView.setImage(image);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
