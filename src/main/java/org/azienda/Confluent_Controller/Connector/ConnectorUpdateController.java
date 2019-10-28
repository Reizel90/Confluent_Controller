package org.azienda.Confluent_Controller.Connector;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.azienda.Confluent_Controller.MainClass;
import org.azienda.Confluent_Controller.MenuBarCreator;

import java.net.URL;
import java.util.ResourceBundle;

import static org.toilelibre.libe.curl.Curl.curl;

public class ConnectorUpdateController {



    public static String connector = null;

    public TextField connector_class;
    public MenuButton connector_class_list;

    public String connectorConfig =null;
    public Button submit;
    public TextArea txtarea;
    public Button text_submit_btn;
    public MenuItem menu_conn;
    public MenuBar menuBar;

    public void initialize(URL location, ResourceBundle resources){
        MenuBarCreator mc = new MenuBarCreator();
        menuBar = mc.init(menuBar);
        try {
            String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/" + connector.replaceAll("\"", "") + "/config") ; //work

            HttpResponse response;
            System.out.println("comando: " + cmd);

            response = curl(cmd);

            HttpEntity e = response.getEntity();
            // since once consumed can't be read again
            // in order to use this string twice i need to store it in a String
            String response_string = null;

            response_string = EntityUtils.toString(e);
            response_string = response_string.replaceAll(",",",\n");

            txtarea.setText(response_string);

            //System.out.println("response string: " + response_string + ".");
        } catch (Exception ex) {
//            connectorConfig = connectorConfig.replace("\n","");
//            connectorConfig = connectorConfig.replace(" ","");
//            connectorConfig = connectorConfig.replace("\t","");
//            System.out.println(connectorConfig);
        }

        try {
            // comando per prendere tutti i connettori disponibili da un nodo
            String cmd2 = String.format("curl http://"+ MainClass.connection +":8083/connector-plugins");

            HttpResponse response2;
            System.out.println("comando2: " + cmd2);

            response2 = curl(cmd2);

            HttpEntity e2 = response2.getEntity();
            // since once consumed can't be read again
            // in order to use this string twice i need to store it in a String
            String response_string2 = null;

            response_string2 = EntityUtils.toString(e2);
            String[] splitted = response_string2.split("\\},\\{");
            connector_class_list.getItems().clear();
            for (int i = 0; i < splitted.length; i++) {
                System.out.println(splitted[i]);
                createMenuItem(connector_class_list, splitted[i]);
            }

            //System.out.println(response_string2.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }





    public void textaddit(ActionEvent actionEvent) {
        connectorConfig = txtarea.getText();
        connectorConfig = connectorConfig.replace("\n","");
        connectorConfig = connectorConfig.replace(" ","");
        connectorConfig = connectorConfig.replace("\t","");
        System.out.println(connectorConfig);

        try {
            String cmd = String.format("curl -X PUT http://" + MainClass.connection + ":8083/connectors/" + connector.replaceAll("\"", "") + "/config"); //work

            HttpResponse response;
            System.out.println("comando: " + cmd);

            response = curl(cmd);

            HttpEntity e = response.getEntity();
            // since once consumed can't be read again
            // in order to use this string twice i need to store it in a String
            String response_string = null;

            response_string = EntityUtils.toString(e);
            response_string = response_string.replaceAll(",",",\n");

            txtarea.setText(response_string);


            System.out.println(EntityUtils.toString(e));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void createMenuItem(MenuButton connector_class_menu, String s) {
        MenuItem mitem;
        String new_s = s.split(",")[0].split(":")[1].replace("\"","");
        mitem = new MenuItem(new_s);
        mitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connector_class.setText(mitem.getText());
            }
        });
        connector_class_menu.getItems().add(mitem);
    }

    public void Connector_home(ActionEvent actionEvent) {
        try {
            //in order to retrieve the stage for changing scene
            Stage stage = (Stage) connector_class.getScene().getWindow();
            //Parent root = FXMLLoader.load(getClass().getResource("index2.fxml"));
            Parent root = FXMLLoader.load(getClass().getResource("connectors.fxml"));
            // Swap screen
            stage.setScene(new Scene(root));
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
