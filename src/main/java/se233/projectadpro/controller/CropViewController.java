package se233.projectadpro.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import se233.projectadpro.model.*;

import java.io.File;
import java.io.IOException;
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
    private ArrayList<ImageCropTask> cropTasks = new ArrayList<>();

    private ArrayList<File> imageFilesList = new ArrayList<>();
    private int currentImgIndex = 0;
    private double maxWidth = 700;
    private double maxHeight = 700;
    private double minWidth = 400;
    private double minHeight = 400;
    private ResizableRectangle resizableRectangle;
    private File outputDir;

    private Stage currentStage;

    @FXML
    public void initialize() {
        resizableRectangle = new ResizableRectangle(0, 0, 200, 200, canvas);
        resizableRectangle.drawRectangle();

        confirmButton.setOnAction(event -> {
            int x = (int) Math.ceil(resizableRectangle.getX());
            int y = (int) Math.ceil(resizableRectangle.getY());
            int width = (int) Math.ceil(resizableRectangle.getWidth());
            int height = (int) Math.ceil(resizableRectangle.getHeight());
            int imageViewWidth = (int) Math.ceil(imageView.getFitWidth());
            int imageViewHeight = (int) Math.ceil(imageView.getFitHeight());
            double scaleX = imageView.getImage().getWidth() / imageViewWidth;
            double scaleY = imageView.getImage().getHeight() / imageViewHeight;
            int cropX = (int) Math.ceil(x * scaleX);
            int cropY = (int) Math.ceil(y * scaleY);
            int cropWidth = (int) Math.ceil(width * scaleX);
            int cropHeight = (int) Math.ceil(height * scaleY);

            ImageCropTask imageCropTask = new ImageCropTask(imageFilesList.get(currentImgIndex), cropX, cropY, cropWidth, cropHeight, currentStage);
            cropTasks.add(imageCropTask);

            if (currentImgIndex == imageFilesList.size() - 1) {
                startAllTasks();
                currentStage.close();
            }
            if (currentImgIndex != imageFilesList.size() - 1) {
                currentImgIndex++;
                setImageView(new Image(imageFilesList.get(currentImgIndex).toURI().toString()));
            }
        });
    }

    public ArrayList<File> processFilesList(ArrayList<File> inputFilesList) throws IOException {
        ZipFileManager zipFileManager = new ZipFileManager();

        return zipFileManager.replaceZipWithImages(inputFilesList);
    }

    public void setImageList(ArrayList<File> selectedFilesList) throws IOException {
        this.imageFilesList = processFilesList(selectedFilesList);
        currentImgIndex = 0;
        setImageView(new Image(imageFilesList.get(currentImgIndex).toURI().toString()));
    }

    public void resizeScene(Image image) {
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

        if ((resizableRectangle.getX() + resizableRectangle.getWidth()) >= width || (resizableRectangle.getY() + resizableRectangle.getHeight()) >= height) {
            Platform.runLater(() -> {
                resizableRectangle.setX(0);
                resizableRectangle.setY(0);
            });
        }

        currentStage.setHeight(height + 140);
        currentStage.setWidth(width + 140);
        anchorPane.setPrefHeight(height + 120);
        anchorPane.setPrefWidth(width + 120);
        canvas.setWidth(width + 20);
        canvas.setHeight(height + 20);
        imageView.setFitWidth(width + 20);
        imageView.setFitHeight(height + 20);
        imageView.setPreserveRatio(true);

        Platform.runLater(() -> {
            confirmButton.setLayoutX((currentStage.getWidth() - confirmButton.getWidth()) / 2);
            confirmButton.setLayoutY(currentStage.getHeight() - (70 + (confirmButton.getHeight() / 2)));
        });
    }

    public void startAllTasks() {
        int progressBar1MaxValue = (cropTasks.size() + 1) / 2;
        int progressBar2MaxValue = cropTasks.size() / 2;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Output Directory");
        outputDir = directoryChooser.showDialog(currentStage);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/se233/projectadpro/progress-view.fxml"));
            Parent root = fxmlLoader.load();

            ProgressViewController progressViewController = fxmlLoader.getController();
            progressViewController.setMaxProgressValue1(progressBar1MaxValue);
            progressViewController.setMaxProgressValue2(progressBar2MaxValue);

            Stage stage = new Stage();
            stage.setTitle("Progress");
            stage.setScene(new Scene(root));
            stage.show();

            Platform.runLater(() -> {
                if (cropTasks.size() == 2)  {
                    progressViewController.setLabel1(cropTasks.get(0).getOriginalFileName());
                    progressViewController.setLabel2(cropTasks.get(1).getOriginalFileName());
                } else {
                    progressViewController.setLabel1(cropTasks.get(0).getOriginalFileName());
                }
            });

            for (int i = 0; i < cropTasks.size(); i++) {
                final int index = i;
                ImageCropTask task = cropTasks.get(i);
                task.setOutputDir(outputDir);
                task.setOnSucceeded(e -> {
                    new TaskCompleteHandler(index, progressViewController).run();
                    if (index == (cropTasks.size() - 1)) {
                        return;
                    }
                    ImageCropTask nextTask = cropTasks.get(index + 1);
                    if ((index+1) % 2 == 0)  {
                        progressViewController.setLabel1(nextTask.getOriginalFileName());
                    } else {
                        progressViewController.setLabel2(nextTask.getOriginalFileName());
                    }
                });
                task.setOnFailed(e -> {
                    System.err.println(task.getException());
                });
                executorService.submit(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    public void setImageView(Image image) {
        resizeScene(image);
        imageView.setImage(image);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
