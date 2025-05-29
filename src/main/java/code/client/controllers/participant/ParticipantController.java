package code.client.controllers.participant;

import code.api.dto.OrganizerDto;
import code.api.dto.ParticipantDto;
import code.client.App;
import code.client.models.SessionContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ParticipantController implements Initializable {
    @FXML
    private Label participantNameLabel;
    Long participantId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ParticipantDto currentParticipant = SessionContext.getCurrentParticipant();
        if (currentParticipant != null && currentParticipant.getUsername() != null) {
            this.participantId = currentParticipant.getId();
            participantNameLabel.setText(currentParticipant.getUsername());

        } else {
            System.err.println("Участник не найден в сессии!");
            participantNameLabel.setText("Гость");
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

    public void gotovisitEvent(ActionEvent actionEvent) {
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


    public void gotoVisitedEvents(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/participant_page/registered_events.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить страницу мероприятий");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}
