package code.client.controllers.organizer;

import code.api.dto.EventDto;
import code.api.dto.InvitationDto;
import code.api.dto.OrganizerDto;
import code.api.dto.ParticipantDto;
import code.api.factories.ParticipantDtoFactory;
import code.api.services.ParticipantService;
import code.client.App;
import code.client.models.EventModel;
import code.client.models.RoleContext;
import code.client.models.SessionContext;
import code.store.entities.InvitationStatus;
import code.store.entities.ParticipantEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OrganizerController implements Initializable {
    private Long organizerId;
    private final String baseUrl = "http://localhost:8080/api/events";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @FXML
    private Label organizerNameLabel;
    @FXML
    private TableView<EventModel> eventTableView;
    @FXML
    private TableColumn<EventModel, String> titleColumn;
    @FXML
    private TableColumn<EventModel, Integer> seatsColumn;
    @FXML
    private TableColumn<EventModel, String> dateColumn;
    @FXML
    private TableColumn<EventModel, String> placeColumn;
    @FXML
    private TableColumn<EventModel, String> formatColumn;

    @FXML
    private TableView<InvitationDto> invitationsTableView;
    @FXML
    private TableColumn<InvitationDto, String> eventTitleColumn;
    @FXML
    private TableColumn<InvitationDto, String> participantColumn;
    @FXML
    private TableColumn<InvitationDto, InvitationStatus> statusColumn;
    @FXML
    private TableColumn<InvitationDto, Void> actionColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfSeats"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        formatColumn.setCellValueFactory(new PropertyValueFactory<>("format"));
        TableColumn<EventModel, Void> actionColumn = (TableColumn<EventModel, Void>) eventTableView.getColumns().get(5);

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Удалить");

            {
                deleteButton.setStyle("-fx-background-color: #661fbc; -fx-text-fill: white; -fx-cursor: hand; -fx-max-width: 100px; -fx-max-height: 5px; -fx-font-size: 14px");


                deleteButton.setOnAction(event -> {
                    EventModel eventModel = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(eventModel);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        initializeInvitationsTable();
        OrganizerDto currentOrganizer = SessionContext.getCurrentOrganizer();
        if (currentOrganizer != null && currentOrganizer.getUsername() != null) {
            this.organizerId = currentOrganizer.getId();
            organizerNameLabel.setText(currentOrganizer.getUsername());
            loadEvents();
            loadInvitations();
        } else {
            System.err.println("Организатор не найден в сессии!");
            organizerNameLabel.setText("Гость");
        }

    }

    private void initializeInvitationsTable() {

        if (eventTitleColumn == null || participantColumn == null ||
                statusColumn == null || actionColumn == null) {
            System.err.println("Ошибка: Не все столбцы инициализированы в FXML!");
            return;
        }


        eventTitleColumn.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        participantColumn.setCellValueFactory(cellData -> {

            return new SimpleStringProperty(cellData.getValue().getParticipantName());
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(InvitationStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toString());
                    switch (status) {
                        case ACCEPTED -> setTextFill(Color.GREEN);
                        case DECLINED -> setTextFill(Color.RED);
                        case PENDING -> setTextFill(Color.ORANGE);
                    }
                }
            }
        });


        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button cancelButton = new Button("Отменить");

            {
                cancelButton.getStyleClass().add("edit-button");
                cancelButton.setOnAction(event -> {
                    InvitationDto invitation = getTableView().getItems().get(getIndex());
                    cancelInvitation(invitation.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : cancelButton);
            }
        });
    }


    private void showDeleteConfirmation(EventModel eventModel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление мероприятия \"" + eventModel.getTitle() + "\"");
        alert.setContentText("Вы уверены? Это действие нельзя отменить.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteEventFromServer(eventModel.getId());
        }
    }
    private void deleteEventFromServer(Long eventId) {
        try {
            String deleteUrl = baseUrl + "/" + eventId;
            HttpURLConnection con = (HttpURLConnection) new URL(deleteUrl).openConnection();
            con.setRequestMethod("DELETE");

            if (con.getResponseCode() == 200) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Мероприятие удалено");
                    loadEvents();
                });
            } else {
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось удалить мероприятие")
                );
            }
        } catch (IOException e) {
            Platform.runLater(() ->
                    showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage())
            );
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void loadEvents() {
        try {
            String apiUrl = baseUrl + "/by-organizer?organizerId=" + this.organizerId;
            System.out.println("Fetching events from: " + apiUrl); //

            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            if (con.getResponseCode() == 200) {
                InputStream response = con.getInputStream();
                String json = new BufferedReader(new InputStreamReader(response))
                        .lines().collect(Collectors.joining("\n"));

                ObjectMapper mapper = new ObjectMapper();
                List<EventDto> eventDtos = mapper.readValue(
                        json,
                        mapper.getTypeFactory().constructCollectionType(List.class, EventDto.class)
                );

                ObservableList<EventModel> events = FXCollections.observableArrayList();
                for (EventDto dto : eventDtos) {
                    System.out.println("Processing event: " + dto.getTitle() +
                            " | Organizer: " + dto.getOrganizerId());
                    String format = parseFormat(dto.getFormat());
                    EventModel eventModel = new EventModel(
                            dto.getId(),
                            dto.getTitle(),
                            dto.getNumberOfSeats(),
                            dto.getDateTime(),
                            dto.getLocation(),
                            format
                    );
                    eventModel.setDescription(dto.getDescription());
                    eventModel.setOrganizerId(dto.getOrganizerId());

                    events.add(eventModel);

                }

                eventTableView.setItems(events);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadInvitations() {
        new Thread(() -> {
            try {
                String url = baseUrl.replace("/api/events", "/api/invitations/organizer") +
                        "?organizerId=" + this.organizerId;

                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setRequestMethod("GET");

                if (con.getResponseCode() == 200) {
                    String json = new BufferedReader(new InputStreamReader(con.getInputStream()))
                            .lines().collect(Collectors.joining("\n"));

                    ObjectMapper mapper = new ObjectMapper();
                    List<InvitationDto> invitations = mapper.readValue(
                            json,
                            mapper.getTypeFactory().constructCollectionType(List.class, InvitationDto.class)
                    );

                    Platform.runLater(() -> {
                        invitationsTableView.getItems().setAll(FXCollections.observableArrayList(invitations));
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить приглашения");
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void cancelInvitation(Long invitationId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение отмены");
        alert.setHeaderText("Отмена приглашения");
        alert.setContentText("Вы уверены, что хотите отменить это приглашение?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    String apiUrl = "http://localhost:8080/api/invitations/" + invitationId;
                    HttpURLConnection con = (HttpURLConnection) new URL(apiUrl).openConnection();
                    con.setRequestMethod("DELETE");

                    if (con.getResponseCode() == 204) {
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.INFORMATION, "Успех", "Приглашение отменено");
                            loadInvitations(); // Обновляем список
                        });
                    } else {
                        Platform.runLater(() ->
                                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось отменить приглашение")
                        );
                    }
                } catch (IOException e) {
                    Platform.runLater(() ->
                            showAlert(Alert.AlertType.ERROR, "Ошибка сети", e.getMessage())
                    );
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private String parseFormat(String rawFormat) {
        if (rawFormat == null || rawFormat.trim().isEmpty()) {
            return "не указан";
        }

        String normalized = rawFormat.trim().toLowerCase();
        return normalized.contains("online") ? "онлайн"
                : normalized.contains("offline") ? "офлайн"
                : rawFormat;
    }

    public void inviteUser(ActionEvent actionEvent) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organizer_page/invite_page.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить страницу приглашений");
        }
    }

    public void edit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organizer_page/editEvent.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть редактор");
        }
    }


    public void gotocreateEvent(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organizer_page/createEvent.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void toUserMode(ActionEvent actionEvent) {
        OrganizerDto currentOrganizer = SessionContext.getCurrentOrganizer();
        if (currentOrganizer == null || currentOrganizer.getUsername() == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Организатор не аутентифицирован");
            return;
        }

        try {
            // Запрашиваем или создаём участника
            String jsonInput = String.format("{\"username\":\"%s\"}", currentOrganizer.getUsername());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/participants/get-or-create"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Участник получен или создан
                ParticipantDto participantDto = objectMapper.readValue(response.body(), ParticipantDto.class);
                SessionContext.setCurrentParticipant(participantDto);
                SessionContext.setCurrentOrganizer(null); // Очищаем организатора
                RoleContext.selectedRole = "participant";

                // Переходим на страницу участника
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/participant_page/patPage.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось получить или создать участника: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось переключиться в режим участника: " + e.getMessage());
        }
    }

    public void toExitFromProfile(ActionEvent actionEvent) {
        SessionContext.clear();
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
