package code.javafx.controllers;

import code.api.dto.EventDto;
import code.api.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ParticipantEventsController implements Initializable {
    @FXML
    private VBox eventsContainer;
    private final String API_URL = "http://localhost:8080/api/events";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAllEvents();
    }

    private void loadAllEvents() {
        try {
            String jsonResponse = sendGetRequest(API_URL);
            List<EventDto> events = mapper.readValue(
                    jsonResponse,
                    mapper.getTypeFactory().constructCollectionType(List.class, EventDto.class)
            );

            for (EventDto event : events) {
                VBox eventBox = createEventBox(event);
                eventsContainer.getChildren().add(eventBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить мероприятия");
        }
    }

    private VBox createEventBox(EventDto event) {
        VBox vbox = new VBox(5);
        vbox.getStyleClass().add("event-box"); // Добавляем CSS класс

        // Название мероприятия
        Label titleLabel = new Label(event.getTitle());
        titleLabel.getStyleClass().add("event-title");

        // Кнопка "подробнее"
        Button detailsButton = new Button("подробнее");
        detailsButton.getStyleClass().add("details-button");
        detailsButton.setOnAction(e -> showEventDetails(event));


        vbox.getChildren().addAll(titleLabel, detailsButton);
        return vbox;
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

    private void showEventDetails(EventDto event) {
        // Реализация перехода на страницу с деталями
        System.out.println("Показать детали: " + event.getTitle());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void gotoVisitedEvents(ActionEvent actionEvent) {
    }

    public void gotoInvitations(ActionEvent actionEvent) {
    }

    public void toExitFromProfile(ActionEvent actionEvent) {
    }

    public void closePr(MouseEvent mouseEvent) {
        System.exit(0);
    }

}
