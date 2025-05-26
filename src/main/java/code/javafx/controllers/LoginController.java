package code.javafx.controllers;

import code.api.dto.OrganizerDto;
import code.api.dto.ParticipantDto;
import code.javafx.App;
import code.javafx.models.SessionContext;
import code.store.entities.OrganizerEntity;
import code.javafx.models.RoleContext;
import code.store.entities.ParticipantEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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

public class LoginController implements Initializable {
    @FXML private TextField userName;
    @FXML private PasswordField password;
    @FXML private Label errorLabel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    public void login(MouseEvent mouseEvent) {
        String username = userName.getText().trim();
        String pass = password.getText().trim();

        if (username.isEmpty() || pass.isEmpty()) {
            showError("Введите логин и пароль");
            return;
        }

        String role = RoleContext.selectedRole;
        if (role == null) {
            showError("Ошибка с выбором роли");
            return;
        }

        try {
            // 1. Подготовка запроса
            String jsonInput = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, pass);
            String urlStr = "http://localhost:8080/api/" +
                    ("participant".equals(role) ? "participants" : "organizers") + "/login";

            HttpResponse<String> response = sendLoginRequest(urlStr, jsonInput);

            // 2. Обработка ответа
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                handleSuccessfulLogin(response.body(), role, mouseEvent);
            } else {
                showError(response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED
                        ? "Неверный логин или пароль"
                        : "Ошибка сервера: " + response.statusCode());
            }
        } catch (Exception e) {
            showError("Ошибка подключения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HttpResponse<String> sendLoginRequest(String url, String json) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        return client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private void handleSuccessfulLogin(String responseBody, String role, MouseEvent event) throws IOException {
        System.out.println("Успешный ответ: " + responseBody);

        if ("organizer".equals(role)) {
            OrganizerDto organizerDto = objectMapper.readValue(responseBody, OrganizerDto.class);
            if (organizerDto == null || organizerDto.getId() == null) {
                showError("Неверные данные пользователя");
                return;
            }

            OrganizerEntity organizer = new OrganizerEntity();
            organizer.setId(organizerDto.getId());
            organizer.setUsername(organizerDto.getUsername());
            SessionContext.setCurrentOrganizer(organizer);

            loadView("/fxml/organizer_page/orgPage.fxml", event);
        }
        else if ("participant".equals(role)) {
            ParticipantDto participantDto = objectMapper.readValue(responseBody, ParticipantDto.class);
            if (participantDto == null || participantDto.getId() == null) {
                showError("Неверные данные пользователя");
                return;
            }

            ParticipantEntity participant = new ParticipantEntity();
            participant.setId(participantDto.getId());
            participant.setUsername(participantDto.getUsername());
            SessionContext.setCurrentParticipant(participant);

            loadView("/fxml/participant_page/patPage.fxml", event);
        }

        showSuccess("Вход выполнен успешно");
    }

    private void loadView(String fxmlPath, MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #ff4444;");
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #00C851;");
    }

    public void exit(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void back_page(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/register_page/register.fxml"));
        App.stage.getScene().setRoot(root);

    }
}
