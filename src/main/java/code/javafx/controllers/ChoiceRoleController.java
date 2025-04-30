package code.javafx.controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChoiceRoleController implements Initializable {

   ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO
    }

    @FXML
    public void open_login_page(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/fxml/login_page/login.fxml"));
        ((Stage)((Node)mouseEvent.getSource()).getScene().getWindow()).getScene().setRoot(fxml);
    }
    @FXML
    public void exit(MouseEvent mouseEvent) {
        System.exit(0);
    }

}
