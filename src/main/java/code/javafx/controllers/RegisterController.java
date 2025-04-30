package code.javafx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    }

    @FXML
    public void register(MouseEvent mouseEvent) {
    }

    public void exit(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void back_page(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login_page/login.fxml"));
        App.stage.getScene().setRoot(root);
    }
}
