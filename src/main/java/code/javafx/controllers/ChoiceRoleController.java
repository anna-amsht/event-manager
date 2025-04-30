package code.javafx.controllers;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;


import java.net.URL;
import java.util.ResourceBundle;

public class ChoiceRoleController implements Initializable {


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO
    }

    @FXML
    public void login(javafx.scene.input.MouseEvent mouseEvent) {
    }
    @FXML
    public void exit(MouseEvent mouseEvent) {
        System.exit(0);
    }

}
