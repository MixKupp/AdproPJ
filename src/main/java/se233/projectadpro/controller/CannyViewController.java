package se233.projectadpro.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import se233.projectadpro.model.CannyTask;
import se233.projectadpro.model.TaskCompleteHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CannyViewController {
    @FXML
    private Label highThresholdLabel;
    @FXML
    private Slider highThresholdSlider;
    @FXML
    private Label lowThresholdLabel;
    @FXML
    private Slider lowThresholdSlider;
    @FXML
    private Label minEdgeLabel;
    @FXML
    private Slider minEdgeSlider;
    @FXML
    private CheckBox l1normBox;
    @FXML
    private Button startBtn;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private Boolean l1norm;
    private int highThreshold;
    private int lowThreshold;
    private int minEdge;

    private ArrayList<File> imageFile = new ArrayList<>();
    private ArrayList<CannyTask> cannyTasks = new ArrayList<>();
    private File outputDir;

    private Stage currentStage;

    public void setImageList(ArrayList<File> image) {
        this.imageFile = image;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void initilizeSlider(Slider slider) {
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setBlockIncrement(1);
    }

    public void sliderDraggedHandler(Slider slider, Label label, Number newVal) {
        slider.setValue(newVal.doubleValue());
        label.setText(String.valueOf(newVal.intValue()));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void startAllTask() {
        int progressBar1MaxValue = (cannyTasks.size() + 1) / 2;
        int progressBar2MaxValue = cannyTasks.size() / 2;
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

            for (int i = 0; i < cannyTasks.size(); i++) {
                final int index = i;
                CannyTask task = cannyTasks.get(i);
                task.setOutputDir(outputDir);
                task.setOnSucceeded(e -> {
                    new TaskCompleteHandler(index, progressViewController).run();
                    if (index == (cannyTasks.size() - 1)) {
                        return;
                    }
                    CannyTask nextTask = cannyTasks.get(index + 1);
                    if (index % 2 == 0)  {
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

    @FXML
    public void initialize() {
        initilizeSlider(highThresholdSlider);
        initilizeSlider(lowThresholdSlider);
        initilizeSlider(minEdgeSlider);

        highThresholdSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sliderDraggedHandler(highThresholdSlider, highThresholdLabel, newVal);
        });

        lowThresholdSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sliderDraggedHandler(lowThresholdSlider, lowThresholdLabel, newVal);
        });

        minEdgeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sliderDraggedHandler(minEdgeSlider, minEdgeLabel, newVal);
        });

        startBtn.setOnAction(e -> {
            l1norm = l1normBox.isSelected();
            highThreshold = (int) highThresholdSlider.getValue();
            lowThreshold = (int) lowThresholdSlider.getValue();
            minEdge = (int) minEdgeSlider.getValue();

            if (lowThreshold > highThreshold) {
                showError("low threshold can't be greater than high threshold");
                return;
            }

            for (File file : imageFile) {
                CannyTask task = new CannyTask(l1norm, highThreshold, lowThreshold, minEdge, file);

                cannyTasks.add(task);
            }

            currentStage.close();
            startAllTask();
        });
    }
}
