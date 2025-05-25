package code.javafx.controllers;

import code.javafx.App;
import code.javafx.models.RoleContext;
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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
        String username = userName.getText();
        String pass = password.getText();


        if (username.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Поля не могут быть пустыми!");
            return;
        }

        String role = RoleContext.selectedRole;
        if (role == null) {
            errorLabel.setText("Ошибка с выбором роли.");
            return;
        }

        String urlStr = "";
        if ("participant".equals(role)) {
            urlStr = "http://localhost:8080/api/participants";
        } else if ("organizer".equals(role)) {
            urlStr = "http://localhost:8080/api/organizers";
        }

        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);

            String jsonInput = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, pass);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                errorLabel.setText("Регистрация прошла успешно.");
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/organizer_page/orgPage.fxml"));
                ((Stage)((Node) mouseEvent.getSource()).getScene().getWindow()).getScene().setRoot(root);
            } else {
                InputStream stream = responseCode < 400 ? con.getInputStream() : con.getErrorStream();

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                    String responseText = br.lines().collect(Collectors.joining());

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        errorLabel.setText("Регистрация успешна!");
                    } else {
                        errorLabel.setText(responseText);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Ошибка подключения.");
        }


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
