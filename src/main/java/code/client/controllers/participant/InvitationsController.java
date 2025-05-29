package code.client.controllers.participant;

import code.api.dto.InvitationDto;

import code.store.entities.InvitationStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import java.util.stream.Collectors;


public class InvitationsController {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML private TableView<InvitationDto> invitationsTable;
    @FXML private TableColumn<InvitationDto, String> eventColumn;
    @FXML private TableColumn<InvitationDto, String> organizerColumn;
    @FXML private TableColumn<InvitationDto, String> statusColumn;

    private Long participantId;

    public void initData(Long participantId) {
        this.participantId = participantId;
        initializeTable();
        loadInvitations();
    }

    private void initializeTable() {
        eventColumn.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        organizerColumn.setCellValueFactory(new PropertyValueFactory<>("organizerName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadInvitations() {
        new Thread(() -> {
            try {
                String url = BASE_URL + "/participants/" + participantId + "/invitations";
                System.out.println("Запрос приглашений по URL: " + url);

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    String json = readResponse(connection);
                    List<InvitationDto> invitations = objectMapper.readValue(
                            json, new TypeReference<List<InvitationDto>>() {});

                    Platform.runLater(() -> {
                        ObservableList<InvitationDto> items = FXCollections.observableArrayList(invitations);
                        invitationsTable.setItems(items);
                        System.out.println("Успешно загружено " + items.size() + " приглашений");
                    });
                } else {
                    String error = readError(connection);
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR,"Ошибка сервера",
                                "Код: " + responseCode + "\n" + error);
                        System.err.println("Ошибка при загрузке: " + error);
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR,"Ошибка соединения", e.getMessage());
                    System.err.println("Ошибка сети: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private String readError(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void acceptInvitation() {
        InvitationDto selected = invitationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.ERROR,"Ошибка", "Выберите приглашение из списка");
            return;
        }

        new Thread(() -> {
            try {
                String url = String.format("%s/invitations/%d/accept",
                        BASE_URL,
                        selected.getId());

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    Platform.runLater(() -> {
                        selected.setStatus(InvitationStatus.ACCEPTED);
                        invitationsTable.refresh();
                        showAlert(Alert.AlertType.INFORMATION,"Успех", "Приглашение принято");
                    });
                } else {
                    String error = readError(connection);
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR,"Ошибка", "Не удалось принять приглашение: " + error);
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR,"Ошибка сети", "Ошибка при отправке запроса: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void declineInvitation() {
        InvitationDto selected = invitationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.ERROR,"Ошибка", "Выберите приглашение из списка");
            return;
        }

        new Thread(() -> {
            try {
                String url = String.format("%s/invitations/%d/reject",
                        BASE_URL,
                        selected.getId());

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    Platform.runLater(() -> {
                        selected.setStatus(InvitationStatus.DECLINED);
                        invitationsTable.refresh();
                        showAlert(Alert.AlertType.INFORMATION,"Успех", "Приглашение отклонено");
                    });
                } else {
                    String error = readError(connection);
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR,"Ошибка", "Не удалось отклонить приглашение: " + error);
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR,"Ошибка сети", "Ошибка при отправке запроса: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    public void back(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/participant_page/patPage.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closePr(MouseEvent mouseEvent) {
        System.exit(0);
    }
}