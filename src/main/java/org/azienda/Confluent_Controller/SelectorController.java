package org.azienda.Confluent_Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SelectorController {
    public Button topic_btn;
    public Button connectors_btn;


    public void topic_rend(ActionEvent actionEvent) throws IOException {
        //in order to retrieve the stage for changing scene
        Stage stage = (Stage) topic_btn.getScene().getWindow();
        //Parent root = FXMLLoader.load(getClass().getResource("index2.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("topic/topics.fxml"));
        // Swap screen
        stage.setScene(new Scene(root));
    }

    public void connectors_rend(ActionEvent actionEvent) throws IOException {
        //in order to retrieve the stage for changing scene
        Stage stage = (Stage) topic_btn.getScene().getWindow();
        //Parent root = FXMLLoader.load(getClass().getResource("index2.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("connector/connectors.fxml"));
        // Swap screen
        stage.setScene(new Scene(root));
    }
}
