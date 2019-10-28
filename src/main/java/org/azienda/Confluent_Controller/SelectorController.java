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
        swap("topic/topics.fxml");
    }

    public void connectors_rend(ActionEvent actionEvent) throws IOException {
        swap("connector/connectors.fxml");
    }

    public void ksql_rend(ActionEvent actionEvent) throws IOException {
        swap("ksql/ksqls.fxml");
    }

    private void swap(String view) throws IOException {
        try {
            //in order to retrieve the stage for changing scene
            Stage stage = (Stage) topic_btn.getScene().getWindow();
            //Parent root = FXMLLoader.load(getClass().getResource("index2.fxml"));
            Parent root = FXMLLoader.load(getClass().getResource(view));
            // Swap screen
            stage.setScene(new Scene(root));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
