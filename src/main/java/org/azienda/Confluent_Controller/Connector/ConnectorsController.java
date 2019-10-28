package org.azienda.Confluent_Controller.Connector;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.azienda.Confluent_Controller.MainClass;
import org.azienda.Confluent_Controller.MenuBarCreator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.toilelibre.libe.curl.Curl.curl;

public class ConnectorsController implements Initializable {

    private boolean verbose=true;
    private static String current_connector = null;

    public MenuBar menuBar;
    public TextArea textarea;
    public VBox lvbox;
//    public MenuItem average;
    public MenuItem create_connector;
    public VBox topicvbox;
    public VBox connectorsvbox;
    public Button status_btn;
    public TextArea errorarea;



    //@Override
    public void initialize(URL location, ResourceBundle resources) {
        //this method is called as soon as the view is loaded
        //System.out.println("Index is now loaded!");
        MenuBarCreator mc = new MenuBarCreator();
        menuBar = mc.init(menuBar);

        String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/");

        if(verbose)System.out.println("comando: " + cmd);
        try {
            HttpResponse response;
            response = curl(cmd);
            HttpEntity e = response.getEntity();
            // since once consumed can't be read again so in order to use this string twice i need to store it in a String
            String response_string = null;
            response_string = EntityUtils.toString(e);

            String[] splitted = response_string.split(",");
            connectorsvbox.getChildren().clear();
            // il primo item è brutto
            for (int i = 0; i < splitted.length; i++) {
                System.out.println(splitted[i]);
                createButton(connectorsvbox, splitted[i]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            swap("connectors.fxml");
        }

    }

    private void createButton(VBox connectorsvbox, String s) {
        Button button;
        if (s.endsWith("]")) {//copre il caso in cui ho un unico elemento e quindi inizierebbe e finirebbe con una quadra
            s = s.substring(0, s.length() - 1);
        }
        if(verbose)System.out.println("s: " +s);
        if(verbose)System.out.println("s.sub: " +s.substring(0,1));
        if (s.substring(0,1).endsWith("[")){
            s = s.substring(1, s.length()); // need to erase first char "["
        }
        button = new Button(s);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/" + button.getText().replaceAll("\"", "")); //work
                current_connector = button.getText().replaceAll("\"", "");
                ConnectorCreatorController.connector = button.getText().replaceAll("\"", "");

                HttpResponse response= curl(cmd);
                printResponse(response);
            }
        });
        connectorsvbox.getChildren().add(button);

    }

    public void createConnector(ActionEvent actionEvent){
        ConnectorCreatorController.connector = current_connector;
        swap("createConnector.fxml");
    }

    public void tasksConnector(ActionEvent actionEvent) {
        HttpResponse response = curlCommand("tasks");
        printResponse(response);
    }

    public void statusConnector(ActionEvent actionEvent) {
        HttpResponse response = curlCommand("status");
        printResponse(response);
    }

    public void viewConnector(ActionEvent actionEvent) {
        HttpResponse response = curlMethod("GET");
        printResponse(response);
    }

    public void updateConnector(ActionEvent actionEvent) {
        ConnectorUpdateController.connector = current_connector;
        swap("updateConnector.fxml");
    }

    public void deleteConnector(ActionEvent actionEvent) {
        if(errorarea.getText().equals("DELETE")){
            curlMethod("DELETE");
            //reload
            swap("connectors.fxml");
        }else{
            errorarea.appendText("\n" + "scrivere qui DELETE per eliminare il connettore");
        }
    }

    public void pauseConnector(ActionEvent actionEvent) { curlCall("PUT", "pause");
    }

    public void resumeConnector(ActionEvent actionEvent) {
        curlCall("PUT", "resume");
    }

    public void restartConnector(ActionEvent actionEvent) {
        curlCall("PUT", "restart");
    }

    private HttpResponse curlCall(String method, String command){
        String cmd = String.format("curl -X " + method + " http://" + MainClass.connection + ":8083/connectors/" + current_connector.replaceAll("\"", "") + "/" + command); //work
        if(verbose)System.out.println("comando: " + cmd);
        HttpResponse response = curl(cmd);
        return response;
    }

    private HttpResponse curlCommand(String command){
        String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/" + current_connector.replaceAll("\"", "") + "/" + command); //work
        if(verbose)System.out.println("comando: " + cmd);
        HttpResponse response = curl(cmd);
        return response;
    }

    private HttpResponse curlMethod(String method){
        String cmd = String.format("curl -X " + method + " http://" + MainClass.connection + ":8083/connectors/" + current_connector.replaceAll("\"", "")); //work
        if(verbose)System.out.println("comando: " + cmd);
        HttpResponse response = curl(cmd);
        return response;
    }

    private void printResponse(HttpResponse response) {
        try {
            HttpEntity e = response.getEntity();
            // since once consumed can't be read again
            // in order to use this string twice i need to store it in a String
            String response_string = null;

            response_string = EntityUtils.toString(e);
            response_string = response_string.replaceAll(",",",\n");

            textarea.setText(response_string);
            //System.out.println(response_string);
        } catch (IOException ex) {
            errorarea.clear();
            errorarea.setText(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void swap(String view) {
        Stage stage = (Stage) lvbox.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(view));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Swap screen
        stage.setScene(new Scene(root));
    }

}
