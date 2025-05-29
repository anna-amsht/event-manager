package code.client.controllers.organizer;

import code.api.dto.EventDto;
import code.api.dto.OrganizerDto;
import code.client.App;
import code.client.models.EventModel;
import code.client.models.SessionContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EditEventController implements Initializable {
    private final String baseUrl = "http://localhost:8080/api/events";
    private Long organizerId;

    @FXML private TableView<EventModel> eventTableView;
    @FXML private TableColumn<EventModel, String> titleColumn;
    @FXML private TableColumn<EventModel, Integer> seatsColumn;
    @FXML private TableColumn<EventModel, String> dateColumn;
    @FXML private TableColumn<EventModel, String> placeColumn;
    @FXML private TableColumn<EventModel, String> formatColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadEvents();
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfSeats"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        formatColumn.setCellValueFactory(new PropertyValueFactory<>("format"));


        TableColumn<EventModel, Void> actionColumn = (TableColumn<EventModel, Void>) eventTableView.getColumns().get(5);
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Изменить");

            {
                editButton.getStyleClass().add("edit-button");
                editButton.setOnAction(event -> {
                    EventModel eventModel = getTableView().getItems().get(getIndex());
                    openEditDialog(eventModel);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });
    }

    private void loadEvents() {
        OrganizerDto currentOrganizer = SessionContext.getCurrentOrganizer();
        if (currentOrganizer != null) {
            this.organizerId = currentOrganizer.getId();

            try {
                String apiUrl = baseUrl + "/by-organizer?organizerId=" + this.organizerId;
                String json = sendGetRequest(apiUrl);

                ObjectMapper mapper = new ObjectMapper();
                List<EventDto> eventDtos = mapper.readValue(
                        json,
                        mapper.getTypeFactory().constructCollectionType(List.class, EventDto.class)
                );

                ObservableList<EventModel> events = FXCollections.observableArrayList();
                for (EventDto dto : eventDtos) {
                    EventModel model = new EventModel(
                            dto.getId(),
                            dto.getTitle(),
                            dto.getDescription(),
                            dto.getNumberOfSeats(),
                            dto.getDateTime(),
                            dto.getLocation(),
                            parseFormat(dto.getFormat())
                    );
                    model.setOrganizerId(this.organizerId); // Устанавливаем organizerId
                    events.add(model);
                }

                eventTableView.setItems(events);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Ошибка", "Не удалось загрузить мероприятия: " + e.getMessage());
            }
        } else {
            showAlert("Ошибка", "Организатор не авторизован");
        }
    }

    private void openEditDialog(EventModel eventModel) {
        EventModel originalModel = new EventModel(
                eventModel.getId(),
                eventModel.getTitle(),
                eventModel.getDescription(),
                eventModel.getNumberOfSeats(),
                eventModel.getDateTime(),
                eventModel.getPlace(),
                eventModel.getFormat()
        );
        originalModel.setOrganizerId(eventModel.getOrganizerId());


        Dialog<EventModel> dialog = new Dialog<>();
        dialog.setTitle("Редактирование мероприятия");
        dialog.setHeaderText("Редактирование: " + originalModel.getTitle());


        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(originalModel.getTitle());
        TextArea descriptionField =new TextArea(originalModel.getDescription());
        Spinner<Integer> seatsSpinner = new Spinner<>(1, Integer.MAX_VALUE, originalModel.getNumberOfSeats());
        TextField dateField = new TextField(originalModel.getDateTime());
        TextField placeField = new TextField(originalModel.getPlace());
        ComboBox<String> formatCombo = new ComboBox<>();
        formatCombo.getItems().addAll("онлайн", "офлайн");
        formatCombo.setValue(originalModel.getFormat());

        grid.add(new Label("Название:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Описание:"), 0, 5);
        grid.add(descriptionField, 1, 5);
        grid.add(new Label("Количество мест:"), 0, 1);
        grid.add(seatsSpinner, 1, 1);
        grid.add(new Label("Дата:"), 0, 2);
        grid.add(dateField, 1, 2);
        grid.add(new Label("Место:"), 0, 3);
        grid.add(placeField, 1, 3);
        grid.add(new Label("Формат:"), 0, 4);
        grid.add(formatCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(false);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    EventModel updatedModel = new EventModel(
                            originalModel.getId(),
                            titleField.getText(),
                            seatsSpinner.getValue(),
                            dateField.getText(),
                            placeField.getText(),
                            formatCombo.getValue()
                    );
                    updatedModel.setDescription(descriptionField.getText());
                    updatedModel.setOrganizerId(originalModel.getOrganizerId());
                    return updatedModel;
                } catch (Exception e) {
                    showAlert("Ошибка", "Некорректные данные: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<EventModel> result = dialog.showAndWait();
        result.ifPresent(this::updateEvent);
    }

    private void updateEvent(EventModel eventModel) {
        try {

            EventDto eventDto = new EventDto();
            eventDto.setTitle(eventModel.getTitle());
            eventDto.setDescription(eventModel.getDescription());
            eventDto.setNumberOfSeats(eventModel.getNumberOfSeats());
            eventDto.setDateTime(eventModel.getDateTime());
            eventDto.setLocation(eventModel.getPlace());
            eventDto.setFormat(eventModel.getFormat().equals("онлайн") ? "ONLINE" : "OFFLINE");
            eventDto.setOrganizerId(eventModel.getOrganizerId());


            ObjectMapper mapper = new ObjectMapper();
            String jsonInput = mapper.writeValueAsString(eventDto);


            String apiUrl = baseUrl + "/" + eventModel.getId();
            String response = sendPutRequest(apiUrl, jsonInput);

            if (response != null) {
                showAlert("Успех", "Мероприятие обновлено");
                loadEvents();
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось обновить мероприятие: " + e.getMessage());
            e.printStackTrace();
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

    private String sendPutRequest(String url, String jsonInput) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(jsonInput.getBytes("utf-8"));
        }

        if (con.getResponseCode() == 200) {
            return new BufferedReader(new InputStreamReader(con.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
        }
        throw new IOException("Server returned: " + con.getResponseCode());
    }

    private String parseFormat(String rawFormat) {
        if (rawFormat == null) return "не указан";
        return rawFormat.equalsIgnoreCase("ONLINE") ? "онлайн" : "офлайн";
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            showAlert( "Ошибка", "Не удалось загрузить страницу приглашений");
        }
    }

    public void toUserMode(ActionEvent actionEvent) {
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
