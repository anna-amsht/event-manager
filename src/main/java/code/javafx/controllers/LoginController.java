package code.javafx.controllers;

import code.javafx.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void login(MouseEvent mouseEvent) {
    }
    @FXML
    public void signUp(MouseEvent mouseEvent) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/fxml/register_page/register.fxml"));
        ((Stage)((Node)mouseEvent.getSource()).getScene().getWindow()).getScene().setRoot(fxml);
    }

    public void exit(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void back_page(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/choice_page/choice.fxml"));
        App.stage.getScene().setRoot(root);

    }
}
