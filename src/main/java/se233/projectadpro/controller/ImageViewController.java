package se233.projectadpro.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewController {
    private double maxWidth = 600;
    private double maxHeight = 600;

    @FXML
    private ImageView imageView;

    @FXML
    public void initialize() {}

    public void setImageView(Image image) {
        double width = image.getWidth();
        double height = image.getHeight();

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

        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);

        imageView.setImage(image);
    }
}
