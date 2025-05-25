package code.javafx.controllers;

import code.api.dto.OrganizerDto;
import code.javafx.App;
import code.javafx.models.SessionContext;
import code.store.entities.OrganizerEntity;
import code.javafx.models.RoleContext;
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
import java.net.URL;
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
        String username = userName.getText();
        String pass = password.getText();

        if (username.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Введите логин и пароль.");
            return;
        }

        String role = RoleContext.selectedRole;
        if (role == null) {
            errorLabel.setText("Ошибка с выбором роли.");
            return;
        }

        String urlStr = "";
        if ("participant".equals(role)) {
            urlStr = "http://localhost:8080/api/participants/login";
        } else if ("organizer".equals(role)) {
            urlStr = "http://localhost:8080/api/organizers/login";
        }

        try {
            // 1. Формируем и отправляем запрос
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);

            String jsonInput = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, pass);

            try (OutputStream os = con.getOutputStream()) {
                os.write(jsonInput.getBytes("utf-8"));
            }

            // 2. Обрабатываем ответ
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {

                    String response = br.lines().collect(Collectors.joining());
                    System.out.println("Response from server: " + response); // Логируем ответ

                    if ("organizer".equals(role)) {
                        OrganizerDto organizerDto = objectMapper.readValue(response, OrganizerDto.class);
                        if (organizerDto == null || organizerDto.getId() == null) {
                            errorLabel.setText("Неверные данные пользователя");
                            return;
                        }

                        OrganizerEntity organizer = new OrganizerEntity();
                        organizer.setId(organizerDto.getId());
                        organizer.setUsername(organizerDto.getUsername());

                        SessionContext.setCurrentOrganizer(organizer); // сохраняем в сессию


                        Parent root = FXMLLoader.load(getClass().getResource("/fxml/organizer_page/orgPage.fxml"));

                        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    }


                    errorLabel.setText("Вход выполнен успешно");

                }
            } else {
                errorLabel.setText(con.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED
                        ? "Неверный логин или пароль"
                        : "Ошибка сервера");
            }
        } catch (IOException e) {
            errorLabel.setText("Ошибка подключения: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void exit(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void back_page(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/register_page/register.fxml"));
        App.stage.getScene().setRoot(root);

    }
}
