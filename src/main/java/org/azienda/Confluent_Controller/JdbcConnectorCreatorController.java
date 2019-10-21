package org.azienda.Confluent_Controller;

import g.liva.AddConnector;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.net.URL;
import java.util.ResourceBundle;

import static org.toilelibre.libe.curl.Curl.curl;

public class JdbcConnectorCreatorController implements Initializable {

    public static String connector = null;

    public TextField connector_name;
    public TextField connector_class;
    public MenuButton connector_class_list;
    public TextField value_converter;

    public String transforms = "ValueToKey";
    public TextField transforms_ValueToKey_type;
    public TextField transforms_ValueToKey_fields;

    public TextField connection_url;
    public TextField connection_user;
    public TextField connection_password;

    public TextField table_whitelist;
    public TextField mode;
    public TextField incrementing_column_name;
    public TextField topic_prefix;

    public String connectorConfig =null;
    public Button submit;
    public TextArea txtarea;
    public Button text_submit_btn;
    public MenuItem menu_conn;

    public void initialize(URL location, ResourceBundle resources){

        try {
            String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/" + connector.replaceAll("\"", "")); //work

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
            AddConnector ac = new AddConnector(MainClass.connection + ":8083");
            HttpResponse r = ac.AddConnector(connectorConfig);
            HttpEntity e = r.getEntity();

            System.out.println(EntityUtils.toString(e));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addit(ActionEvent actionEvent) {

        connectorConfig = "{\"name\": \""+connector_name.getText()+"\",\n" +
                "    \"config\": {\n" +
                "      \"connector.class\": \""+connector_class.getText()+"\",\n" +
                "      \"value.converter\": \""+value_converter.getText()+"\",\n" +

                "      \"transforms\": \""+transforms+"\",\n" +
                "      \"transforms.ValueToKey.type\": \""+transforms_ValueToKey_type.getText()+"\",\n" +
                "      \"transforms.ValueToKey.fields\": \""+transforms_ValueToKey_fields.getText()+"\",\n" +

                "      \"connection.url\": \""+connection_url.getText()+"\",\n" +
                "      \"connection.user\": \""+connection_user.getText()+"\",\n" +
                "      \"connection.password\": \""+connection_password.getText()+"\",\n" +
                "      \"table.whitelist\": \""+table_whitelist.getText()+"\",\n" +

                "      \"mode\": \""+mode.getText()+"\",\n" +
                "      \"incrementing.column.name\": \""+incrementing_column_name.getText()+"\",\n" +
                "      \"topic.prefix\": \""+topic_prefix.getText()+"\"\n" +

                "    } }";
        connectorConfig = connectorConfig.replace("\n","");
        connectorConfig = connectorConfig.replace(" ","");
        connectorConfig = connectorConfig.replace("\t","");
        System.out.println(connectorConfig);

        try {
            AddConnector ac = new AddConnector(MainClass.connection + ":8083");
            HttpResponse r = ac.AddConnector(connectorConfig);
            HttpEntity e = r.getEntity();

            System.out.println(EntityUtils.toString(e));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createConnector(ActionEvent actionEvent){
        try {
            //in order to retrieve the stage for changing scene
            Stage stage = (Stage) table_whitelist.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("jdbc_connector_creator.fxml"));
            // Swap screen
            stage.setScene(new Scene(root));
        }catch(Exception e){
            System.out.println(e);
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
