package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("Quiz & Journal Dashboard");
        stage.setScene(scene);
        stage.show();
        scene.getStylesheets().add(getClass().getResource("/css.css").toExternalForm());


    }

    public static void main(String[] args) {
        launch(args);
    }
}
