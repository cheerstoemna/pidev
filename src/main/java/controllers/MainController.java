package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.Parent;

public class MainController {

    @FXML
    private Button quizBtn;
    @FXML
    private Button journalBtn;
    @FXML
    private StackPane contentPane;

    @FXML
    public void initialize() {
        quizBtn.setOnAction(e -> {
            setActiveTab(quizBtn);
            loadContent("/fxml/quizz.fxml");
        });
        journalBtn.setOnAction(e -> {
            setActiveTab(journalBtn);
            loadContent("/fxml/journal.fxml");
        });

        setActiveTab(quizBtn);
        loadContent("/fxml/quizz.fxml");
    }

    private void setActiveTab(Button activeButton) {
        quizBtn.getStyleClass().remove("journal-tab-btn-active");
        journalBtn.getStyleClass().remove("journal-tab-btn-active");

        if (!activeButton.getStyleClass().contains("journal-tab-btn-active")) {
            activeButton.getStyleClass().add("journal-tab-btn-active");
        }
    }

    private void loadContent(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
