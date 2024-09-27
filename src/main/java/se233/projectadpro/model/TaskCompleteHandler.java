package se233.projectadpro.model;

import se233.projectadpro.controller.ProgressViewController;

public class TaskCompleteHandler implements Runnable {
    private int index;
    private ProgressViewController progressViewController;

    public TaskCompleteHandler(int index, ProgressViewController controller) {
        this.index = index;
        this.progressViewController = controller;
    }

    @Override
    public void run() {
        if (index % 2 == 0) {
            progressViewController.updateProgressValue1();
        } else {
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
