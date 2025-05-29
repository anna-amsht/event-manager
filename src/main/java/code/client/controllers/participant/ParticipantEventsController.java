package code.client.controllers.participant;

import code.api.dto.EventDto;
import code.api.dto.ParticipantDto;
import code.client.App;
import code.client.models.SessionContext;
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
        vbox.getStyleClass().add("event-box");


        Label titleLabel = new Label(event.getTitle());
        titleLabel.getStyleClass().add("event-title");

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
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/participant_page/visit_event.fxml"));
                Parent root = loader.load();

                VisitEventController controller = loader.getController();
                controller.setEvent(event);

                Stage stage = new Stage();
                stage.setTitle("Детали мероприятия");
                stage.setScene(new Scene(root, 600, 500));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Ошибка", "Не удалось загрузить детали мероприятия");
            }

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void gotoVisitedEvents(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/participant_page/registered_events.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить страницу мероприятий");
        }
    }

    public void gotoInvitations(ActionEvent actionEvent) {
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
        showAlert("Критическая ошибка",
                "Не удалось загрузить интерфейс: " + e.getMessage());
    }
}

    public void toExitFromProfile(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/choice_page/choice.fxml"));
            Parent root = loader.load();
            App.stage.getScene().setRoot(root);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closePr(MouseEvent mouseEvent) {
        System.exit(0);
    }

}
