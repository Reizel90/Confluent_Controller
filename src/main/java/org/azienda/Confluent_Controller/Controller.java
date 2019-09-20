package org.azienda.Confluent_Controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    public Button LogInButton;
    public TextArea center_text;
    public TextField usernamefield;
    public TextField passwordfield;
    public BorderPane bpane;
    private int login_attempt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //this method is called as soon as the view is loaded
        System.out.println("View is now loaded!");

        //in order to retrieve the stage for changing scene
//        Stage stageTheEventSourceNodeBelongs = (Stage) ((Node)event.getSource()).getScene().getWindow();
//        // OR
//        Stage stage = (Stage) bpane.getScene().getWindow();
//        // these two of them return the same stage
//        // Swap screen
//        stage.setScene(new Scene(new Pane()));
    }

    public void loginAction(ActionEvent actionEvent) {
        center_text.appendText("login attempt number " + login_attempt
                + " user: " + usernamefield.getText()
                + " password: " + passwordfield.getText() + "\n");
        login_attempt += 1;
        System.out.println("login");
    }
}
