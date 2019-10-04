package org.azienda.Confluent_Controller;

import g.liva.AddConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class JdbcConnectorCreatorController implements Initializable {

    public TextField connector_name;
    public TextField connector_class;
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

    public void initialize(URL location, ResourceBundle resources){

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

    public void averagerefresh(ActionEvent actionEvent) {
    }
}
