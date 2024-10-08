package se233.projectadpro.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import se233.projectadpro.model.CannyTask;
import se233.projectadpro.model.SobelTask;
import se233.projectadpro.model.TaskCompleteHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SobelViewController {
    @FXML
    private CheckBox l1normBox;
    @FXML
    private Button startBtn;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private Boolean l1norm;

    private ArrayList<File> imageFile = new ArrayList<>();
    private ArrayList<SobelTask> sobelTasks = new ArrayList<>();
    private File outputDir;

    private Stage currentStage;

    public void setImageList(ArrayList<File> image) {
        this.imageFile = image;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void startAllTask() {
        int progressBar1MaxValue = (sobelTasks.size() + 1) / 2;
        int progressBar2MaxValue = sobelTasks.size() / 2;
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
                if (sobelTasks.size() == 2)  {
                    progressViewController.setLabel1(sobelTasks.get(0).getOriginalFileName());
                    progressViewController.setLabel2(sobelTasks.get(1).getOriginalFileName());
                } else {
                    progressViewController.setLabel1(sobelTasks.get(0).getOriginalFileName());
                }
            });

            for (int i = 0; i < sobelTasks.size(); i++) {
                final int index = i;
                SobelTask task = sobelTasks.get(i);
                task.setOutputDir(outputDir);
                task.setOnSucceeded(e -> {
                    new TaskCompleteHandler(index, progressViewController).run();
                    if (index == (sobelTasks.size() - 1)) {
                        return;
                    }
                    SobelTask nextTask = sobelTasks.get(index + 1);
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

    @FXML
    public void initialize() {
        startBtn.setOnAction(e -> {
            l1norm = l1normBox.isSelected();

            for (File file : imageFile) {
                SobelTask task = new SobelTask(l1norm, file);

                sobelTasks.add(task);
            }

            currentStage.close();
            startAllTask();
        });
    }
}
