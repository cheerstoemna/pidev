package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        boolean openAdmin = getParameters().getRaw().stream().anyMatch(a -> a.equalsIgnoreCase("admin"));

        String fxml = openAdmin ? "/fxml/AdminDashboard.fxml" : "/fxml/ContentDashboard.fxml";
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml), "Missing " + fxml));

        Scene scene = new Scene(root);
        var css = getClass().getResource("/css/dashboard.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle(openAdmin ? "MindNest - Admin" : "MindNest");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
