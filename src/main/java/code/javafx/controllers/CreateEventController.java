package code.javafx.controllers;

import code.api.dto.EventDto;
import code.javafx.models.SessionContext;
import code.store.entities.OrganizerEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CreateEventController implements Initializable {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField placeField;
    @FXML private TextField dateField;
    @FXML private TextField seatsField;
    @FXML private TextField formatField;
    @FXML private Label errorLabel;

    private Long organizerId;
    EventDto eventDto;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void createEvent(ActionEvent actionEvent) {

        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String place = placeField.getText().trim();
        String dateTimeStr = dateField.getText().trim();
        String seatsStr = seatsField.getText().trim();
        String format = formatField.getText().trim();

        if (title.isEmpty() || description.isEmpty() || place.isEmpty() ||
                dateTimeStr.isEmpty() || seatsStr.isEmpty() || format.isEmpty()) {
            errorLabel.setText("Заполните все обязательные поля!");
            return;
        }


        if (!format.equalsIgnoreCase("онлайн") && !format.equalsIgnoreCase("офлайн")) {
            errorLabel.setText("Формат должен быть 'Онлайн' или 'Офлайн'");
            return;
        }


        int seats;
        try {
            seats = Integer.parseInt(seatsStr);
            if (seats <= 0) {
                errorLabel.setText("Количество мест должно быть положительным числом");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Количество мест должно быть числом!");
            return;
        }

        OrganizerEntity currentOrganizer = SessionContext.getCurrentOrganizer();
        if (currentOrganizer == null) {
            errorLabel.setText("Организатор не найден в сессии!");
            return;
        }


        EventDto eventDto = new EventDto();
        eventDto.setTitle(title);
        eventDto.setDescription(description);
        eventDto.setLocation(place);
        eventDto.setDateTime(dateTimeStr);
        eventDto.setNumberOfSeats(seats);
        eventDto.setFormat(format.equalsIgnoreCase("онлайн") ? "ONLINE" : "OFFLINE");
        eventDto.setOrganizerId(currentOrganizer.getId());

        try {

            URL url = new URL("http://localhost:8080/api/events");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String jsonInput = mapper.writeValueAsString(eventDto);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                errorLabel.setText("Мероприятие успешно создано");

            } else {

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                    String errorResponse = br.lines().collect(Collectors.joining());
                    errorLabel.setText("Ошибка: " + errorResponse);
                }
            }

        } catch (IOException e) {
            errorLabel.setText("Ошибка подключения к серверу");
            e.printStackTrace();
        }
    }

    public void gotoedit(ActionEvent actionEvent) {
    }

    public void gotoinviteUser(ActionEvent actionEvent) {
    }

    public void toUserMode(ActionEvent actionEvent) {
    }

    public void toExitFromProfile(ActionEvent actionEvent) {
        SessionContext.clear();
    }

    public void closePr(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void back(MouseEvent mouseEvent) {
        try {

            URL fxmlUrl = getClass().getResource("/fxml/organizer_page/orgPage.fxml");
            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке страницы:");
            e.printStackTrace();
        }
    }
}
