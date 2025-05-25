package code.javafx.controllers;

import code.api.dto.EventDto;
import code.javafx.models.EventModel;
import code.javafx.models.SessionContext;
import code.store.entities.OrganizerEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OrganizerController implements Initializable {
    private Long organizerId;
    private final String baseUrl = "http://localhost:8080/api/events";

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfSeats"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        formatColumn.setCellValueFactory(new PropertyValueFactory<>("format"));

        OrganizerEntity currentOrganizer = SessionContext.getCurrentOrganizer();
        if (currentOrganizer != null) {
            this.organizerId = currentOrganizer.getId();
            loadEvents();
        } else {
            System.err.println("Организатор не найден в сессии!");
        }
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
                    String format = parseFormat(dto.getFormat()); // Нормализация формата
                    events.add(new EventModel(
                            dto.getTitle(),
                            dto.getNumberOfSeats(),
                            dto.getDateTime(),
                            dto.getLocation(),
                            format
                    ));
                }

                eventTableView.setItems(events);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parseFormat(String rawFormat) {
        if (rawFormat == null || rawFormat.trim().isEmpty()) {
            return "не указан";
        }

        String normalized = rawFormat.trim().toLowerCase();
        return normalized.contains("online") ? "онлайн"
                : normalized.contains("offline") ? "офлайн"
                : rawFormat; // или "неизвестно", если хотите заменять другие значения
    }

    public void inviteUser(ActionEvent actionEvent) {
    }

    public void edit(ActionEvent actionEvent) {
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

    public void toUserMode(ActionEvent actionEvent) {
    }

    public void toExitFromProfile(ActionEvent actionEvent) {
    }

    public void closePr(MouseEvent mouseEvent) {
        System.exit(0);
    }


}
