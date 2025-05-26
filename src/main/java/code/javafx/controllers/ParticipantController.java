package code.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class ParticipantController {
    public void toExitFromProfile(ActionEvent actionEvent) {
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
    }

    public void gotoInvitations(ActionEvent actionEvent) {
    }
}
