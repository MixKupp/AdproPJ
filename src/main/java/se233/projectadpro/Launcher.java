package se233.projectadpro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Launcher extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("main-view.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Project Adpro!");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {launch(args);}

}
