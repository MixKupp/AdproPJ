package se233.projectadpro.model;

import javafx.concurrent.Task;
import se233.projectadpro.model.detectors.CannyEdgeDetector;
import se233.projectadpro.model.util.Grayscale;
import se233.projectadpro.model.util.Threshold;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CannyTask extends Task<Void> {
    private Boolean l1norm;
    private int highThresholdValue;
    private int lowThresholdValue;
    private int minEdge;

    private File imageFile;
    private File outputDir;

    public CannyTask(Boolean l1norm, int highThresholdValue, int lowThresholdValue, int minEdge, File imageFile) {
        this.l1norm = l1norm;
        this.highThresholdValue = highThresholdValue;
        this.lowThresholdValue = lowThresholdValue;
        this.minEdge = minEdge;
        this.imageFile = imageFile;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public String getOriginalFileName() {
        return imageFile.getName();
    }

    @Override
    protected Void call() {
        try {
            BufferedImage originalImage = ImageIO.read(imageFile);
            int[][] pixels = Grayscale.imgToGrayPixels(originalImage);

            CannyEdgeDetector canny = new CannyEdgeDetector.Builder(pixels)
                            .minEdgeSize(minEdge)
                            .thresholds(lowThresholdValue, highThresholdValue)
                            .L1norm(l1norm)
                            .build();

            boolean[][] edges = canny.getEdges();

            BufferedImage cannyImage = Threshold.applyThreshold(edges);

            String outputFileName = imageFile.getName();
            String baseName = outputFileName.substring(0, outputFileName.lastIndexOf('.'));
            String extension = outputFileName.substring(outputFileName.lastIndexOf('.') + 1);
            File outputImageFile = new File(outputDir, baseName + "-canny." + extension);

            ImageIO.write(cannyImage, extension, outputImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
