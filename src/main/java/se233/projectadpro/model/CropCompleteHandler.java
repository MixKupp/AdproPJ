package se233.projectadpro.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import se233.projectadpro.controller.ProgressViewController;

import java.io.IOException;

public class CropCompleteHandler implements Runnable {
    private int index;
    private ProgressViewController progressViewController;
    private ImageCropTask task;

    public CropCompleteHandler(int index, ProgressViewController controller, ImageCropTask task) {
        this.index = index;
        this.progressViewController = controller;
        this.task = task;
    }

    @Override
    public void run() {
        if (index % 2 == 0) {
            progressViewController.setLabel1(task.getOriginalFileName());
            progressViewController.updateProgressValue1();
        } else {
            progressViewController.setLabel2(task.getOriginalFileName());
            progressViewController.updateProgressValue2();
        }
    }

    private void updateProgress1() {
        progressViewController.updateProgressValue1();
    }

    private void updateProgress2() {
        progressViewController.updateProgressValue2();
    }
}
