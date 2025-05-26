package code.javafx.controllers;

import code.javafx.App;
import code.javafx.models.RoleContext;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RegisterController implements Initializable {
    @FXML private TextField userName;
    @FXML private PasswordField password;
    @FXML private Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    public void register(MouseEvent mouseEvent) {
        String username = userName.getText().trim();
        String pass = password.getText().trim();


        if (username.isEmpty() || pass.isEmpty()) {
            showError("Поля не могут быть пустыми!");
            return;
        }

        String role = RoleContext.selectedRole;
        if (role == null) {
            showError("Ошибка с выбором роли.");
            return;
        }

        try {

            String jsonInput;
            if ("participant".equals(role)) {
                jsonInput = createParticipantJson(username, pass);
            } else if ("organizer".equals(role)) {
                jsonInput = createOrganizerJson(username, pass);
            } else {
                showError("Неизвестная роль");
                return;
            }


            String urlStr = "http://localhost:8080/api/" + ("participant".equals(role) ? "participants" : "organizers");
            HttpResponse<String> response = sendPostRequest(urlStr, jsonInput);


            if (response.statusCode() == 200 || response.statusCode() == 201) {
                showSuccessAndRedirect(mouseEvent);
            } else {
                showError(response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка подключения: " + e.getMessage());
        }
    }

    private String createParticipantJson(String username, String password) {
        return String.format("{\"username\":\"%s\", \"password\":\"%s\"}",
                username, password);
    }

    private String createOrganizerJson(String username, String password) {
        return String.format("{\"username\":\"%s\", \"password\":\"%s\"}",
                username, password);
    }

    private HttpResponse<String> sendPostRequest(String url, String json) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #ff4444;");
    }

    private void showSuccessAndRedirect(MouseEvent event) throws IOException {
        errorLabel.setText("Регистрация прошла успешно!");
        errorLabel.setStyle("-fx-text-fill: #00C851;");

        // Задержка перед редиректом
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/login_page/login.fxml"));
                ((Stage)((Node) event.getSource()).getScene().getWindow()).getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        delay.play();
    }

    public void exit(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void back_page(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/choice_page/choice.fxml"));
        App.stage.getScene().setRoot(root);
    }

    @FXML
    public void signIn(MouseEvent mouseEvent) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/fxml/login_page/login.fxml"));
        ((Stage)((Node)mouseEvent.getSource()).getScene().getWindow()).getScene().setRoot(fxml);
    }
}
