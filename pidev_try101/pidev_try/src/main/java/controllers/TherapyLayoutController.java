package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import models.Role;
import utils.UserSession;

import java.io.IOException;

public class TherapyLayoutController {

    @FXML private Label userLabel;
    @FXML private Button btnClient;
    @FXML private Button btnAdmin;
    @FXML private StackPane contentHost;

    @FXML
    public void initialize() {
        boolean isAdmin = UserSession.get().role() == Role.ADMIN;

        userLabel.setText(
                (UserSession.get().user() == null ? "Guest" : UserSession.get().user().getFullName())
                        + " • " + UserSession.get().role()
        );

        btnAdmin.setVisible(isAdmin);
        btnAdmin.setManaged(isAdmin);

        if (isAdmin) load("/fxml/TherapyAdminDashboard.fxml");
        else load("/fxml/TherapyClientDashboard.fxml");
    }

    @FXML
    private void openClient() { load("/fxml/TherapyClientDashboard.fxml"); }

    @FXML
    private void openAdmin() { load("/fxml/TherapyAdminDashboard.fxml"); }

    private void load(String path) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(path));
            contentHost.getChildren().setAll(view);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load: " + path, e);
        }
    }
}
