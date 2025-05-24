package code.javafx.controllers;

import code.javafx.models.EventModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

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
    private int organizerId;
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
    }

    public void setOrganizerId(int id) {
        this.organizerId = id;
        loadEvents();
    }


    private void loadEvents() {
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            if (con.getResponseCode() == 200) {
                InputStream response = con.getInputStream();
                String json = new BufferedReader(new InputStreamReader(response))
                        .lines().collect(Collectors.joining("\n"));

                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> eventList = mapper.readValue(json, List.class);

                ObservableList<EventModel> events = FXCollections.observableArrayList();
                for (Map<String, Object> item : eventList) {
                    events.add(new EventModel(
                            (String) item.get("title"),
                            (int) item.get("numberOfSeats"),
                            (String) item.get("dateTime"),
                            (String) item.get("location"),
                            (String) item.get("format")
                    ));
                }

                eventTableView.setItems(events);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void inviteUser(ActionEvent actionEvent) {
    }

    public void edit(ActionEvent actionEvent) {
    }

    public void createEvent(ActionEvent actionEvent) {
    }

    public void toUserMode(ActionEvent actionEvent) {
    }

    public void toExitFromProfile(ActionEvent actionEvent) {
    }

    public void closePr(MouseEvent mouseEvent) {
        System.exit(0);
    }


}
