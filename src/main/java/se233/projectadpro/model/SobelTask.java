package se233.projectadpro.model;

import javafx.concurrent.Task;
import se233.projectadpro.model.detectors.SobelEdgeDetector;
import se233.projectadpro.model.util.Grayscale;
import se233.projectadpro.model.util.Threshold;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SobelTask extends Task<Void> {
    private Boolean l1norm;

    private File imageFile;
    private File outputDir;

    public SobelTask(Boolean l1norm, File imageFile) {
        this.l1norm = l1norm;
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
            SobelEdgeDetector sobel = new SobelEdgeDetector(pixels, l1norm);

            boolean[][] edges = sobel.getEdges();

            BufferedImage edges_image = Threshold.applyThreshold(edges);

            String outputFileName = imageFile.getName();
            String baseName = outputFileName.substring(0, outputFileName.lastIndexOf('.'));
            String extension = outputFileName.substring(outputFileName.lastIndexOf('.') + 1);
            File outputImageFile = new File(outputDir, baseName + "-sobel." + extension);

            ImageIO.write(edges_image, extension, outputImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
