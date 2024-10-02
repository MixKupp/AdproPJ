package se233.projectadpro.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import se233.projectadpro.model.CannyTask;
import se233.projectadpro.model.RobertsTask;
import se233.projectadpro.model.TaskCompleteHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RobertsViewController {
    @FXML
    private CheckBox l1normBox;
    @FXML
    private Button startBtn;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private Boolean l1norm;

    private ArrayList<File> imageFile = new ArrayList<>();
    private ArrayList<RobertsTask> robertsTasks = new ArrayList<>();
    private File outputDir;

    private Stage currentStage;

    public void setImageList(ArrayList<File> image) {
        this.imageFile = image;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void startAllTask() {
        int progressBar1MaxValue = (robertsTasks.size() + 1) / 2;
        int progressBar2MaxValue = robertsTasks.size() / 2;
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
                if (robertsTasks.size() == 2)  {
                    progressViewController.setLabel1(robertsTasks.get(0).getOriginalFileName());
                    progressViewController.setLabel2(robertsTasks.get(1).getOriginalFileName());
                } else {
                    progressViewController.setLabel1(robertsTasks.get(0).getOriginalFileName());
                }
            });

            for (int i = 0; i < robertsTasks.size(); i++) {
                final int index = i;
                RobertsTask task = robertsTasks.get(i);
                task.setOutputDir(outputDir);
                task.setOnSucceeded(e -> {
                    new TaskCompleteHandler(index, progressViewController).run();
                    if (index == (robertsTasks.size() - 1)) {
                        return;
                    }
                    RobertsTask nextTask = robertsTasks.get(index + 1);
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
                RobertsTask task = new RobertsTask(l1norm, file);

                robertsTasks.add(task);
            }

            currentStage.close();
            startAllTask();
        });
    }
}
