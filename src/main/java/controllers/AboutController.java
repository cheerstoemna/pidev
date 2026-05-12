package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class AboutController {

    @FXML
    private StackPane rootPane;

    @FXML
    private StackPane heroImageShell;

    @FXML
    private ImageView heroImageView;

    @FXML
    public void initialize() {
        applyHeroClip();
        playIntroAnimation();
    }

    @FXML
    private void openContent() {
        setSidebarActive("btnClientContent");
        loadIntoHost("/fxml/ContentDashboard.fxml");
    }

    @FXML
    private void openJournalQuiz() {
        setSidebarActive("btnJournal");
        loadIntoHost("/fxml/main.fxml");
    }

    @FXML
    private void openCoaching() {
        setSidebarActive("btnCoaching");
        loadIntoHost("/fxml/dashboard.fxml");
    }

    @FXML
    private void openTherapy() {
        setSidebarActive("btnClient");
        loadIntoHost("/fxml/TherapyClientDashboard.fxml");
    }

    private void loadIntoHost(String fxmlPath) {
        StackPane host = findContentHost();
        if (host == null) {
            return;
        }

        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            host.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StackPane findContentHost() {
        Parent current = rootPane;
        while (current != null) {
            if (current instanceof StackPane stackPane && "contentHost".equals(stackPane.getId())) {
                return stackPane;
            }
            current = current.getParent();
        }
        return null;
    }

    private void setSidebarActive(String activeButtonId) {
        if (rootPane == null || rootPane.getScene() == null) {
            return;
        }

        List<String> sidebarIds = List.of(
                "btnAbout",
                "btnClient",
                "btnJournal",
                "btnClientContent",
                "btnCoaching"
        );

        for (String buttonId : sidebarIds) {
            var node = rootPane.getScene().lookup("#" + buttonId);
            if (node instanceof Button button) {
                button.getStyleClass().remove("sidebar-nav-btn-active");
            }
        }

        var activeNode = rootPane.getScene().lookup("#" + activeButtonId);
        if (activeNode instanceof Button activeButton
                && !activeButton.getStyleClass().contains("sidebar-nav-btn-active")) {
            activeButton.getStyleClass().add("sidebar-nav-btn-active");
        }
    }

    private void applyHeroClip() {
        if (heroImageView == null) {
            return;
        }

        Rectangle clip = new Rectangle(920, 270);
        clip.setArcWidth(34);
        clip.setArcHeight(34);
        heroImageView.setClip(clip);
    }

    private void playIntroAnimation() {
        if (rootPane == null) {
            return;
        }

        rootPane.setOpacity(0);
        rootPane.setTranslateY(12);

        FadeTransition fade = new FadeTransition(Duration.millis(420), rootPane);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition lift = new TranslateTransition(Duration.millis(420), rootPane);
        lift.setFromY(12);
        lift.setToY(0);

        fade.play();
        lift.play();
    }
}
