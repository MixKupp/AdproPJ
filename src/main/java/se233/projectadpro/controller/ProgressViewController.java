package se233.projectadpro.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProgressViewController {
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private ProgressBar progressBar1;
    @FXML
    private ProgressBar progressBar2;

    private int maxProgressValue1;
    private int maxProgressValue2;

    @FXML
    private void initialize() {
        label1.setText("Thread 1");
        label2.setText("Thread 2");
        progressBar1.setProgress(0);
        progressBar2.setProgress(0);
    }

    public void setMaxProgressValue1(int maxProgressValue1) {
        this.maxProgressValue1 = maxProgressValue1;
    }

    public void setMaxProgressValue2(int maxProgressValue2) {
        this.maxProgressValue2 = maxProgressValue2;
    }

    public void setLabel1(String label) {
        label1.setText(label);
    }

    public void setLabel2(String label) {
        label2.setText(label);
    }

    public void updateProgressValue1() {
        double progress = progressBar1.getProgress() + (1.0 / maxProgressValue1);
        progressBar1.setProgress(progress);
    }

    public void updateProgressValue2() {
        double progress = progressBar2.getProgress() + (1.0 / maxProgressValue2);
        progressBar2.setProgress(progress);
    }
}
