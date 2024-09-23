package se233.projectadpro.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;

public class ImageViewController {
    private double scaleValue = 1.0; // Initial scale value
    private final double scaleFactor = 1.1;
    private double maxWidth = 600;
    private double maxHeight = 600;

    @FXML
    private ImageView imageView;

    @FXML
    public void initialize() {
        Scale scale = new Scale(scaleValue, scaleValue, 0, 0);
        imageView.getTransforms().add(scale);

        imageView.setOnScroll(event -> {
            if (event.getDeltaY() > 0) {
                scaleValue *= scaleFactor; // Zoom in
            } else {
                scaleValue /= scaleFactor; // Zoom out
            }
            scale.setX(scaleValue);
            scale.setY(scaleValue);
            event.consume();
        });
    }

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
