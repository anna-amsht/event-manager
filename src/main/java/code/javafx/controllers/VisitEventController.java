package code.javafx.controllers;

import code.api.dto.EventDto;
import code.api.dto.ReservationDto;
import code.javafx.models.SessionContext;
import code.store.entities.ParticipantEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VisitEventController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label locationLabel;
    @FXML private Label seatsLabel;
    @FXML private Label formatLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Button backButton;
    @FXML private Button registerButton;

    private EventDto event;
    private final String API_URL = "http://localhost:8080/api/reservations";
    private final ObjectMapper mapper = new ObjectMapper();

    public void setEvent(EventDto event) {
        this.event = event;
        updateUI();
    }

    private void updateUI() {
        titleLabel.setText(event.getTitle());
        dateLabel.setText("Дата и время: " + event.getDateTime());
        locationLabel.setText("Место: " + event.getLocation());
        seatsLabel.setText("Количество мест: " + event.getNumberOfSeats());
        formatLabel.setText("Формат: " +
                (event.getFormat().equalsIgnoreCase("ONLINE") ? "Онлайн" : "Офлайн"));
        descriptionArea.setText(event.getDescription());

        if (isAlreadyRegistered()) {
            registerButton.setDisable(true);
            registerButton.setText("Вы уже записаны");
        } else if (event.getNumberOfSeats() <= 0) {
            registerButton.setDisable(true);
            registerButton.setText("Нет мест");
        }
    }

    private boolean isAlreadyRegistered() {
        try {
            ParticipantEntity participant = SessionContext.getCurrentParticipant();
            if (participant == null) return false;

            String url = API_URL + "?participantId=" + participant.getId();
            String response = sendGetRequest(url);
            List<ReservationDto> reservations = mapper.readValue(response,
                    mapper.getTypeFactory().constructCollectionType(List.class, ReservationDto.class));

            return reservations.stream()
                    .anyMatch(r -> r.getEventId().equals(event.getId()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    public void handleBackButton(javafx.scene.input.MouseEvent mouseEvent) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleRegisterButton() {
        try {
            // Получаем текущего участника из сессии
            ParticipantEntity currentParticipant = SessionContext.getCurrentParticipant();
            if (currentParticipant == null) {
                showAlert("Ошибка", "Участник не авторизован");
                return;
            }

            // Создаем запрос на запись
            String url = API_URL + "?participantId=" + currentParticipant.getId() +
                    "&eventId=" + event.getId();

            String response = sendPostRequest(url);
            ReservationDto reservation = mapper.readValue(response, ReservationDto.class);

            showAlert("Успех", "Вы успешно записаны на мероприятие!");
            registerButton.setDisable(true);
            registerButton.setText("Вы записаны");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось записаться на мероприятие: " + e.getMessage());
        }
    }

    private String sendPostRequest(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        if (con.getResponseCode() == 200) {
            return new BufferedReader(new InputStreamReader(con.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
        }
        throw new IOException("Server returned: " + con.getResponseCode());
    }

    private String sendGetRequest(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");

        if (con.getResponseCode() == 200) {
            return new BufferedReader(new InputStreamReader(con.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
        }
        throw new IOException("Server returned: " + con.getResponseCode());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
