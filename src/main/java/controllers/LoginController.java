package controllers;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.AppUser;
import models.Role;
import org.mindrot.jbcrypt.BCrypt;
import utils.UserSession;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.setDisable(false);
    }

    @FXML
    void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showInlineError("Please fill in all fields.");
            return;
        }

        if (!isValidEmail(email)) {
            showInlineError("Invalid email format. e.g. user@example.com");
            highlightError(emailField);
            return;
        }

        resetStyle(emailField);
        hideError();

        loginButton.setDisable(true);
        loginButton.setText("Signing in...");
        authenticateUser(email, password);
    }

    private void authenticateUser(String email, String password) {
        String sql = """
            SELECT id, name, email, role, password
            FROM users
            WHERE email=? AND status=1
        """;

        try (Connection c = DBConnection.getConnection()) {
            if (c == null) {
                showInlineError("Database connection failed. Please make sure MySQL/XAMPP is running.");
                loginButton.setDisable(false);
                loginButton.setText("Continue");
                return;
            }

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, email);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        if (!passwordMatches(password, storedPassword)) {
                            showInlineError("Invalid email or password.");
                            highlightError(emailField);
                            highlightError(passwordField);
                            loginButton.setDisable(false);
                            loginButton.setText("Continue");
                            return;
                        }

                        String dbRole = rs.getString("role");
                        Role appRole = switch (dbRole) {
                            case "ADMIN" -> Role.ADMIN;
                            case "COACH" -> Role.COACH;
                            case "THERAPIST" -> Role.THERAPIST;
                            default -> Role.CLIENT;
                        };

                        AppUser user = new AppUser(rs.getInt("id"), rs.getString("name"), appRole);
                        UserSession.get().setUser(user);
                        redirectToTherapyModule();
                        return;
                    }
                }
            }

            showInlineError("Invalid email or password.");
            highlightError(emailField);
            highlightError(passwordField);
            loginButton.setDisable(false);
            loginButton.setText("Continue");

        } catch (Exception e) {
            e.printStackTrace();
            showInlineError("Database error: " + e.getMessage());
            loginButton.setDisable(false);
            loginButton.setText("Continue");
        }
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            try {
                String normalizedHash = storedPassword.startsWith("$2y$")
                        ? "$2a$" + storedPassword.substring(4)
                        : storedPassword;
                return BCrypt.checkpw(rawPassword, normalizedHash);
            } catch (Exception e) {
                return false;
            }
        }

        return rawPassword.equals(storedPassword);
    }

    private void redirectToTherapyModule() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TherapyLayout.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 720);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("MindNest");
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void highlightError(TextField field) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 6;");
    }

    private void resetStyle(TextField field) {
        field.setStyle("");
    }

    private void showInlineError(String msg) {
        errorLabel.setText("Error: " + msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    @FXML
    public void goForgotPassword(ActionEvent e) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/forgot_password.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void goSignup(ActionEvent e) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
