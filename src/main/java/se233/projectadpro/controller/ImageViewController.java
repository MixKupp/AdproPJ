package se233.projectadpro.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ImageViewController {
    private double maxWidth = 700;
    private double maxHeight = 700;
    private double minWidth = 400;
    private double minHeight = 400;

    private Stage currentStage;

    @FXML
    private ImageView imageView;

    @FXML
    public void initialize() {}


    public void setImageView(Image image) {
        double width = image.getWidth();
        double height = image.getHeight();

        if (width <= minWidth || height <= minHeight) {
            width = minWidth;
            height = minHeight;
        } else {
            if (width > maxWidth || height > maxHeight) {
                double aspectRatio = width / height;
                if (aspectRatio > 1) {
                    width = maxWidth;
                    height = maxWidth / aspectRatio;
                } else {
                    height = maxHeight;
                    width = maxHeight * aspectRatio;
                }
            }
        }

        currentStage.setWidth(width + 120);
        currentStage.setHeight(height + 120);
        imageView.setFitWidth(width + 20);
        imageView.setFitHeight(height + 20);
        imageView.setPreserveRatio(true);

        imageView.setImage(image);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
