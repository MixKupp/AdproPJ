package se233.projectadpro.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EdgeViewController {
    @FXML
    private Button cannyBtn;
    @FXML
    private Button sobelBtn;
    @FXML
    private Button robertsBtn;

    private ArrayList<File> imageList = new ArrayList<>();
    private Stage currentStage;

    public void setImageList(ArrayList<File> imageList) {
        this.imageList = imageList;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    @FXML
    private void initialize() {
        cannyBtn.setOnAction(event -> {
            cannyBtn.setDisable(true);

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/se233/projectadpro/canny-view.fxml"));
                Parent root = fxmlLoader.load();

                CannyViewController cannyViewController = fxmlLoader.getController();

                Stage stage = new Stage();
                stage.setTitle("Canny");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.setOnHidden(e -> cannyBtn.setDisable(false));
                cannyViewController.setCurrentStage(stage);
                cannyViewController.setImageList(imageList);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        sobelBtn.setOnAction(event -> {
            sobelBtn.setDisable(true);

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/se233/projectadpro/sobel-view.fxml"));
                Parent root = fxmlLoader.load();

                SobelViewController sobelViewController = fxmlLoader.getController();

                Stage stage = new Stage();
                stage.setTitle("Sobel");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.setOnHidden(e -> sobelBtn.setDisable(false));
                sobelViewController.setCurrentStage(stage);
                sobelViewController.setImageList(imageList);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

       robertsBtn.setOnAction(event -> {
           robertsBtn.setDisable(true);

           try {
               FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/se233/projectadpro/roberts-view.fxml"));
               Parent root = fxmlLoader.load();

               RobertsViewController robertsViewController = fxmlLoader.getController();

               Stage stage = new Stage();
               stage.setTitle("Roberts");
               stage.setScene(new Scene(root));
               stage.setResizable(false);
               stage.setOnHidden(e -> robertsBtn.setDisable(false));
               robertsViewController.setCurrentStage(stage);
               robertsViewController.setImageList(imageList);
               stage.show();
           } catch (IOException e) {
               e.printStackTrace();
           }
       });
    }
}
