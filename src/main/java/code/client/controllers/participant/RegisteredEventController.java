package code.client.controllers.participant;

import code.api.dto.EventDto;
import code.api.dto.ParticipantDto;
import code.api.dto.ReservationDto;
import code.client.App;
import code.client.models.SessionContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RegisteredEventController implements Initializable {
    @FXML
    private VBox eventsContainer;

    private final String RESERVATIONS_API = "http://localhost:8080/api/reservations";
    private final String EVENTS_API = "http://localhost:8080/api/events";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRegisteredEvents();
    }

    private void loadRegisteredEvents() {
        eventsContainer.getChildren().clear();

        try {

            Long participantId = SessionContext.getCurrentParticipant().getId();

            String reservationsUrl = RESERVATIONS_API + "?participantId=" + participantId;
            String reservationsJson = sendGetRequest(reservationsUrl);

            List<ReservationDto> reservations = mapper.readValue(
                    reservationsJson,
                    new TypeReference<List<ReservationDto>>() {}
            );

            if (reservations.isEmpty()) {
                Label emptyLabel = new Label("У вас нет зарегистрированных мероприятий");
                emptyLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #666;");
                eventsContainer.getChildren().add(emptyLabel);
                return;
            }

            for (ReservationDto reservation : reservations) {
                String eventUrl = EVENTS_API + "/" + reservation.getEventId();
                String eventJson = sendGetRequest(eventUrl);

                EventDto event = mapper.readValue(eventJson, EventDto.class);
                createEventBox(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить мероприятия");
        }
    }

    private void createEventBox(EventDto event) {

        VBox eventBox = new VBox();
        eventBox.getStyleClass().add("event-box");
        eventBox.setSpacing(5);
        eventBox.setPadding(new javafx.geometry.Insets(10));

        Label titleLabel = new Label(event.getTitle());
        titleLabel.getStyleClass().add("event-title");
        titleLabel.setWrapText(true);


        HBox infoBox = new HBox(15);
        Label dateLabel = new Label(event.getDateTime());
        Label formatLabel = new Label(event.getFormat().equalsIgnoreCase("ONLINE") ? "Онлайн" : "Офлайн");
        infoBox.getChildren().addAll(dateLabel, formatLabel);


        Button cancelButton = new Button("Отменить запись");
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(e -> cancelRegistration(event.getId()));


        eventBox.getChildren().addAll(
                titleLabel,
                infoBox,
                new Label("Место: " + event.getLocation()),
                new Label("Доступно мест: " + event.getNumberOfSeats()),
                cancelButton
        );

        eventsContainer.getChildren().add(eventBox);
    }

    private void showEventDetails(EventDto event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/participant_page/patEvents.fxml"));
            Parent root = loader.load();

            VisitEventController controller = loader.getController();
            controller.setEvent(event);

            Stage stage = new Stage();
            stage.setTitle("Детали мероприятия");
            stage.setScene(new Scene(root, 700, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить детали мероприятия");
        }
    }

    private void cancelRegistration(Long eventId) {
        try {
            Long participantId = SessionContext.getCurrentParticipant().getId();
            String url = RESERVATIONS_API + "/by-participant-and-event?participantId=" +
                    participantId + "&eventId=" + eventId;


            String reservationJson = sendGetRequest(url);
            ReservationDto reservation = mapper.readValue(reservationJson, ReservationDto.class);

            String deleteUrl = RESERVATIONS_API + "/" + reservation.getId();
            sendDeleteRequest(deleteUrl);

            showAlert("Успех", "Регистрация отменена");
            loadRegisteredEvents(); // Обновляем список
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось отменить регистрацию");
        }
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

    private void sendDeleteRequest(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("DELETE");

        if (con.getResponseCode() != 200) {
            throw new IOException("Server returned: " + con.getResponseCode());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void closePr(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void toExitFromProfile(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/choice_page/choice.fxml"));
            Parent root = loader.load();
            App.stage.getScene().setRoot(root);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void gotoInvitations(javafx.event.ActionEvent actionEvent) {
        try {
            ParticipantDto currentParticipant = SessionContext.getCurrentParticipant();
            if (currentParticipant == null || currentParticipant.getId() == null) {
                showAlert("Ошибка авторизации", "Необходимо войти в систему");
                return;
            }

            System.out.println("Переход на страницу приглашений. ID участника: " +
                    currentParticipant.getId());


            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/participant_page/invitations.fxml")
            );
            Parent root = loader.load();

            InvitationsController controller = loader.getController();
            controller.initData(currentParticipant.getId());

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке страницы приглашений:");
            e.printStackTrace();
            showAlert("Ошибка",
                    "Не удалось загрузить интерфейс: " + e.getMessage());
        }

    }

    public void togoRegisterIvent(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/participant_page/patEvents.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
