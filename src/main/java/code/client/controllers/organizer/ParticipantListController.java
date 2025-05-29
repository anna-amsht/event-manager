package code.client.controllers.organizer;

import code.api.dto.EventDto;
import code.api.dto.ParticipantDto;
import code.client.App;
import code.client.models.SessionContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ParticipantListController {
    @FXML private TableView<ParticipantDto> participantsTableView;
    @FXML private TableColumn<ParticipantDto, String> usernameColumn;
    @FXML private Label titleLabel;

    private final String participantsUrl = "http://localhost:8080/api/participants/secure";
    private final String invitationsUrl = "http://localhost:8080/api/invitations/send";
    private final String eventsUrl = "http://localhost:8080/api/events/by-organizer";

    @FXML
    public void initialize() {
        setupTableColumns();
        loadParticipants();
    }

    private void setupTableColumns() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setStyle("-fx-text-fill: #661fbc; -fx-font-size: 14;");

        TableColumn<ParticipantDto, Void> actionColumn = new TableColumn<>("Действие");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button inviteButton = new Button("Пригласить");

            {
                inviteButton.setStyle("-fx-background-color: #9281D3; -fx-text-fill: white; -fx-font-size: 14; -fx-pref-width: 140px");
                inviteButton.setOnAction(event -> {
                    ParticipantDto participant = getTableView().getItems().get(getIndex());
                    showInvitationDialog(participant);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(inviteButton);
                }
            }
        });

        participantsTableView.getColumns().add(actionColumn);
        participantsTableView.setStyle("-fx-background-color: transparent;");
    }

    private void loadParticipants() {
        try {
            URL url = new URL(participantsUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == 200) {
                String json = new BufferedReader(new InputStreamReader(con.getInputStream()))
                        .lines().collect(Collectors.joining("\n"));

                List<ParticipantDto> participants = new ObjectMapper()
                        .readValue(json, new TypeReference<List<ParticipantDto>>() {});

                ObservableList<ParticipantDto> observableList = FXCollections.observableArrayList(participants);
                participantsTableView.setItems(observableList);
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка загрузки", "Не удалось загрузить список участников");
        }
    }

    private void showInvitationDialog(ParticipantDto participant) {
        Long organizerId = SessionContext.getCurrentOrganizer().getId();
        String eventsUrl = "http://localhost:8080/api/events/by-organizer?organizerId=" + organizerId;

        try {
            URL url = new URL(eventsUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == 200) {
                String json = new BufferedReader(new InputStreamReader(con.getInputStream()))
                        .lines().collect(Collectors.joining("\n"));

                List<EventDto> events = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<EventDto>>() {});

                if (events.isEmpty()) {
                    showErrorAlert("Нет мероприятий", "У вас нет мероприятий для приглашения");
                    return;
                }

                List<String> eventTitles = new ArrayList<>();
                Map<String, EventDto> titleToEventMap = new HashMap<>();
                for (EventDto event : events) {
                    eventTitles.add(event.getTitle());
                    titleToEventMap.put(event.getTitle(), event);
                }

                ChoiceDialog<String> dialog = new ChoiceDialog<>(eventTitles.get(0), eventTitles);
                dialog.setTitle("Приглашение участника");
                dialog.setHeaderText("Выберите мероприятие для приглашения");
                dialog.setContentText("Мероприятие:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(title -> {
                    EventDto selectedEvent = titleToEventMap.get(title);
                    sendInvitation(organizerId, participant.getId(), selectedEvent.getId());
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Ошибка", "Не удалось загрузить список мероприятий");
        }
    }

    private void sendInvitation(Long organizerId, Long participantId, Long eventId) {
        try {
            String params = String.format("organizerId=%d&participantId=%d&eventId=%d",
                    organizerId, participantId, eventId);
            URL url = new URL(invitationsUrl + "?" + params);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            if (con.getResponseCode() == 200) {
                showInfoAlert("Успех", "Приглашение успешно отправлено");
            } else {
                showErrorAlert("Ошибка", "Не удалось отправить приглашение");
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка", "Ошибка при отправке приглашения");
        }
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organizer_page/orgPage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Ошибка", "Не удалось вернуться на главную страницу");
        }
    }

    @FXML
    public void closePr() {
        System.exit(0);
    }

    @FXML
    public void toExitFromProfile(ActionEvent event) {
        SessionContext.clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/choice_page/choice.fxml"));
            Parent root = loader.load();
            App.stage.getScene().setRoot(root);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
