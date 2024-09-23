package se233.projectadpro.model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import net.coobird.thumbnailator.Thumbnails;

public class ImageCropTask extends Task<File> {
    private File originalImage;
    private int x, y;
    private int width, height;
    private Stage stage;
    private File outputImage;

    public ImageCropTask(File originalImage, int x, int y, int width, int height, Stage stage) {
        this.originalImage = originalImage;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.stage = stage;
    }

    @Override
    protected File call() {
        // Show the DirectoryChooser on the JavaFX Application Thread
        Platform.runLater(() -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Output Directory");
            File outputDir = directoryChooser.showDialog(stage);
            saveCroppedImage(outputDir);
        });

        return null; // You may want to return something relevant
    }

    private void saveCroppedImage(File outputDir) {
        String outputFileName = originalImage.getName();
        String baseName = outputFileName.substring(0, outputFileName.lastIndexOf('.'));
        String extension = outputFileName.substring(outputFileName.lastIndexOf('.'));
        outputImage = new File(outputDir, baseName + "-crop" + extension);

        try {
            Thumbnails.of(originalImage)
                    .sourceRegion(x, y, width, height) // Crop region (x, y, width, height)
                    .scale(1)
                    .toFile(outputImage);
            System.out.println("Image cropped and saved to: " + outputImage.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
