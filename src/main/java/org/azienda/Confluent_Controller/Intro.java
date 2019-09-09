package org.azienda.Confluent_Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.net.URL;
import java.util.ResourceBundle;

import static org.toilelibre.libe.curl.Curl.curl;

public class Intro {


    public Button connection_button;
    public TextField connection_field;
    public TextArea output_text;

    //@Override
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

    public void connect(ActionEvent actionEvent) {
        String conn = connection_field.getText();

        String cmd = String.format("curl http://"+conn+":8082/topics/");
        HttpResponse response;
        System.out.println("comando: "+cmd);

        try {
        response = curl(cmd);

        HttpEntity e = response.getEntity();
        // since once consumed can't be read again so in order to use this string twice i need to store it in a String
        String response_string = null;

            response_string = EntityUtils.toString(e);
            String new_line = "\n";
            response_string = response_string.replaceAll(",", new_line);
            output_text.setText(response_string);
            System.out.println("risposta: "+response_string);
        } catch (Exception ex) {
            output_text.setText("l'indirizzo non è valido");
        }


    }
}
