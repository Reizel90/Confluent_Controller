package org.azienda.Confluent_Controller.KSQL;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.azienda.Confluent_Controller.Connector.ConnectorCreatorController;
import org.azienda.Confluent_Controller.MainClass;
import org.azienda.Confluent_Controller.MenuBarCreator;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;
import java.util.ResourceBundle;

import static org.toilelibre.libe.curl.Curl.curl;

public class KSQLController implements Initializable {


    private boolean verbose = false;
    private static String current_connector = null;
    private static String current_table = null;
    private static String current_stream = null;
    private static String current_query = null;

    public MenuButton topic_class_list;
    public TextField topic_name_txt;
    public MenuBar menuBar;

    public TextArea topTextArea;
    public VBox lvbox;
    public MenuItem create_connector;
    public Button status_btn;
    public Button select_btn;
    public TextArea bottomTextArea;
    public VBox tablesvbox;
    public VBox streamsvbox;
    public VBox queriesvbox;
    public CheckBox brace_chk;
    public CheckBox comma_chk;
    public CheckBox enable_select_chk;
    public CheckBox redetop_chk;

    //@Override
    public void initialize(URL location, ResourceBundle resources) {
        //this method is called as soon as the view is loaded
        //System.out.println("Index is now loaded!");
        MenuBarCreator mc = new MenuBarCreator();
        menuBar = mc.init(menuBar);
        init();
    }

    public void init(){
        populateStreams();
        populateTables();
        populateQueries();
        populateTopics();
    }

    public void sendQuery(ActionEvent actionEvent) throws IOException {

        // .replace("/\s\s+/g", " ")  want to cover spaces, tabs, newlines, etc.
        // .replace(/  +/g, ' ')    want to cover only spaces.
//        String json = String.format(topTextArea.getText().trim());
        String json = String.format(topTextArea.getText().replaceAll("\n", " "));

        bottomTextArea.setText("\n" + json + "\n");

        String cmd = String.format(""
                + ExampleQueries.getKsql_ddl_head_cmd()
                + json
                + ExampleQueries.getKsql_ddl_tail_cmd()
        ).replaceAll("\\p{Cc}", ""); //this replaceAll replaces CTRL-CHAR

        bottomTextArea.appendText(""
                + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n\n"
                + cmd
                + "\n\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n"
        );

        HttpResponse response = curl(cmd);
        HttpEntity e = response.getEntity();

        init();
        bottomTextArea.appendText(""
                + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n\n"
                + EntityUtils.toString(e)
                        .replaceAll(("\\\\n"), "\n") // cazzo di \ che è escape char sia per java che per regex
                        .replaceAll(",", ",\n")
                + "\n\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n\n\n\n"
        );
    }

    public void selectConnector(ActionEvent actionEvent) throws IOException {

        if(enable_select_chk.isSelected()) {
            enable_select_chk.setSelected(false);
            // .replace("/\s\s+/g", " ")  want to cover spaces, tabs, newlines, etc.
            // .replace(/  +/g, ' ')    want to cover only spaces.
//        String json = String.format(topTextArea.getText().trim());
            String json = String.format(bottomTextArea.getText().replaceAll("\n", " "));

            topTextArea.setText("\n" + json + "\n");

            String cmd = String.format(""
                    + ExampleQueries.getKsql_select_head_cmd()
                    + json
                    + ExampleQueries.getKsql_select_tail_cmd()
            ).replaceAll("\\p{Cc}", ""); //this replaceAll replaces CTRL-CHAR

            topTextArea.appendText(""
                    + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n\n"
                    + cmd
                    + "\n\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n"
            );

            HttpResponse response = curl(cmd);
            HttpEntity e = response.getEntity();

            init();
            topTextArea.appendText(""
                    + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n\n"
                    + EntityUtils.toString(e)
                    .replaceAll(("\\\\n"), "\n") // cazzo di \ che è escape char sia per java che per regex
                    .replaceAll("\\{", "{\n")
                    + "\n\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n\n\n\n"
            );

        }else{
            topTextArea.setText(""
                    + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n\n"
                    + "ARE YOU SURE YOU WANT TO SEND THIS?"
                    + "\n\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n\n\n\n"
            );
        }
    }

    public void createExample(ActionEvent actionEvent) {
        topTextArea.clear();
        topTextArea.setText(ExampleQueries.getTopic_to_table());
    }

    public void selectExample(ActionEvent actionEvent) {
            topTextArea.clear();
            topTextArea.setText(ExampleQueries.getSelectcmd());
    }

    public void dropExample(ActionEvent actionEvent) {
        topTextArea.clear();
        topTextArea.setText(ExampleQueries.getDropcmd());
    }

    public void terminateExample(ActionEvent actionEvent) {
        topTextArea.clear();
        topTextArea.setText(ExampleQueries.getTerminatecmd());
    }

    public void delete_topic(ActionEvent actionEvent) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, MainClass.connection + ":9092");
        AdminClient admin = KafkaAdminClient.create(config);

        String topicNames = topic_name_txt.getText();
        admin.deleteTopics(Collections.singletonList(topicNames));
        populateTopics();
//        admin.deleteTopics(Arrays.asList(topicNames));
//        Logger log;
//        log.info("Topics '{}' deleted.", topicNames);

        admin.close();
    }

///////////////////////// ROBA VECCHIA di altre viste /////////////////////////////////////////
    public void createConnector(ActionEvent actionEvent) {
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

    ////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// PRIVATE ZONE ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////// POPULATE ZONE //////////////////////////////////////

    private void populateStreams() {
        streamsvbox.getChildren().clear();
        String cmd = String.format(""
                + "curl -X POST http://" + MainClass.connection + ":8088/ksql"
                + " -H \"Content-Type: application/vnd.ksql.v1+json; charset=utf-8\" "
                + " -d '{"
                + "  \"ksql\": \"LIST STREAMS;\","
                + "  \"streamsProperties\": {}"
                + "}'"
        );

        // [{"@type":"streams"
        // "statementText":"LIST STREAMS;"
        // "streams":[                                  // split("\"streams\":[");

        // {"type":"STREAM"
        // "name":"KSQL_PROCESSING_LOG"
        // "topic":"default_ksql_processing_log"
        // "format":"JSON"}
                                                    // split( "}{" );
        // {"type":"STREAM"
        // "name":"REIZEL90_DBO_ALLARMI"
        // "topic":"REIZEL90.dbo.ALLARMI"
        // "format":"JSON"}

        // ]}]                                          // substring (0, s.lenght-3);

        HttpResponse response;
        response = curl(cmd);

        try {
            HttpEntity e = response.getEntity();
            // since once consumed can't be read again so in order to use this string twice i need to store it in a String
            String response_string = null;
            response_string = EntityUtils.toString(e);

            String streams = response_string.split("\"streams\":\\[")[1];
            //0 -/////////- [{"@type":"streams","statementText":"LIST STREAMS;",
            //1 -/////////- {"type":"STREAM","name":"KSQL_PROCESSING_LOG","topic":"default_ksql_processing_log","format":"JSON"},{"type":"STREAM","name":"REIZEL90_DBO_ALLARMI","topic":"REIZEL90.dbo.ALLARMI","format":"JSON"}]}]

            String[] splitted = streams.split("},\\{");
            //0 -/////////- {"type":"STREAM","name":"KSQL_PROCESSING_LOG","topic":"default_ksql_processing_log","format":"JSON"
            //1 -/////////- "type":"STREAM","name":"REIZEL90_DBO_ALLARMI","topic":"REIZEL90.dbo.ALLARMI","format":"JSON"}]}]

            for (int i = 0; i < splitted.length; i++) {
                String stream_name = splitted[i].split(",")[1].split(":")[1]
                        .replaceAll("\\_", "_").replaceAll("\"", "");
                // KSQL_PROCESSING_LOG
                if (verbose) System.out.println("populate streams: " + stream_name);
                String streamdesccmd = describe(stream_name);
                createButton(streamsvbox, stream_name, streamdesccmd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //swap("ksql.fxml");
        }
    }

    private void populateTables() {
        tablesvbox.getChildren().clear();
        String cmd = String.format(""
                + "curl -X POST http://" + MainClass.connection + ":8088/ksql"
                + " -H \"Content-Type: application/vnd.ksql.v1+json; charset=utf-8\" "
                + " -d '{"
                + "  \"ksql\": \"LIST TABLES;\","
                + "  \"streamsProperties\": {}"
                + "}'"
        );

        HttpResponse response;
        response = curl(cmd);

        try {
            HttpEntity e = response.getEntity();
            // since once consumed can't be read again so in order to use this string twice i need to store it in a String
            String response_string = null;
            response_string = EntityUtils.toString(e);
            if (verbose) System.out.println("95 response_string: " + response_string);

            String streams = response_string.split("\"tables\":\\[")[1];
            String[] splitted = streams.split("},\\{");

            //if(splitted.length!=1) // DEVO AVERE ALMENO DUE TABELLE PER ESSERE MOSTRATE @TODO MIGLIORARE
                for (int i = 0; i < splitted.length; i++) {
                    String table_name = splitted[i].split(",")[1].split(":")[1].replaceAll("\"", "");
                    // KSQL_PROCESSING_LOG
                    if (verbose) System.out.println("populate tables: " + table_name);
                    String tabledesccmd = describe(table_name);
                    createButton(tablesvbox, table_name, tabledesccmd);
                }
        } catch (Exception ex) {
            System.out.println("populate table error: ");
            ex.printStackTrace();
            //swap("ksql.fxml");
        }
    }

    private void populateQueries() {
        queriesvbox.getChildren().clear();
        String cmd = String.format(""
                + "curl -X POST http://" + MainClass.connection + ":8088/ksql"
                + " -H \"Content-Type: application/vnd.ksql.v1+json; charset=utf-8\" "
                + " -d '{"
                + "  \"ksql\": \"LIST QUERIES;\","
                + "  \"streamsProperties\": {}"
                + "}'"
        );

        HttpResponse response;
        response = curl(cmd);

        try {
            HttpEntity e = response.getEntity();
            // since once consumed can't be read again so in order to use this string twice i need to store it in a String
            String response_string = null;
            response_string = EntityUtils.toString(e);

            String queries = response_string.split("\"queries\":\\[")[1];
            String[] splitted = queries.split("},\\{");

            for (int i = 0; i < splitted.length; i++) {
                String query_name = splitted[i].split(",")[1].split(":")[1].replaceAll("\"", "");
                // KSQL_PROCESSING_LOG
                if (verbose) System.out.println("populate queries: " + query_name);
                String querydesccmd = explain(query_name);
                createButton(queriesvbox, query_name, querydesccmd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //swap("ksql.fxml");
        }
    }

    private void populateTopics() {

        topic_class_list.getItems().clear();
        //curlCall("GET", "topics");
        String cmd = String.format("curl http://" + MainClass.connection + ":8082/topics"); //work

        HttpResponse response = curl(cmd);
        String response_string = null;
        try {
            HttpEntity e = response.getEntity();
            response_string = EntityUtils.toString(e);

            String[] splitted = response_string.split(",");
            for (int i = 0; i < splitted.length; i++) {
                if (verbose) System.out.println(splitted[i]);
                createMenuItem(topic_class_list, splitted[i]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            swap("ksqls.fxml");
        }
    }

    /////////////////////////////// CREATION ZONE ///////////////////////////////////////

    private void createButton(VBox vbox, String name, String cmd) {
        Button button;
        if (name.endsWith("]")) {//copre il caso in cui ho un unico elemento e quindi inizierebbe e finirebbe con una quadra
            name = name.substring(0, name.length() - 1);
        }
        if (verbose) System.out.println("s: " + name);
        if (verbose) System.out.println("s.sub: " + name.substring(0, 1));
        if (name.substring(0, 1).endsWith("[")) {
            name = name.substring(1, name.length()); // need to erase first char "["
        }
        button = new Button(name);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

//                String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/" + button.getText().replaceAll("\"", "")); //work
//                String cmd = String.format(""
//                        + "curl -X POST http://" + MainClass.connection + ":8088/ksql"
//                        + " -H \"Content-Type: application/vnd.ksql.v1+json; charset=utf-8\" "
//                        + " -d '{"
//                        + "  \"ksql\": \"DESCRIBE EXTENDED "
//                        + button.getText().replaceAll("\"", "") + ";\","
//                        + "  \"streamsProperties\": {}"
//                        + "}'"
//                );

                if (verbose) System.out.println("button handler command: " + cmd);

                current_stream = button.getText().replaceAll("\"", "");
                //current_connector = button.getText().replaceAll("\"", "");
                //ConnectorCreatorController.connector = button.getText().replaceAll("\"", "");

                HttpResponse response = curl(cmd);
                printResponse(response);
            }
        });
        vbox.getChildren().add(button);
    }

    // prepare the list of topics for the hint box
    private void createMenuItem(MenuButton topic_class_list, String name) {
        MenuItem mitem;
        if (name.endsWith("]")) {//copre il caso in cui ho un unico elemento e quindi inizierebbe e finirebbe con una quadra
            name = name.substring(0, name.length() - 1);
        }
        if (verbose) System.out.println("s: " + name);
        if (verbose) System.out.println("s.sub: " + name.substring(0, 1));
        if (name.substring(0, 1).endsWith("[")) {
            name = name.substring(1, name.length()); // need to erase first char "["
        }
        String new_s = name.split(",")[0].replace("\"", "");
        mitem = new MenuItem(new_s);
        mitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                topic_name_txt.setText(mitem.getText());
            }
        });
        topic_class_list.getItems().add(mitem);
    }

    /////////////////////////////// CURL ZONE ///////////////////////////////////////

    private String describe(String name){

        return String.format(""
                + "curl -X POST http://" + MainClass.connection + ":8088/ksql"
                + " -H \"Content-Type: application/vnd.ksql.v1+json; charset=utf-8\" "
                + " -d '{"
                + "  \"ksql\": \"DESCRIBE EXTENDED "
                + name.replaceAll("\"", "") + ";\","
                + "  \"streamsProperties\": {}"
                + "}'"
        );
    }

    private String explain(String name) {

        return String.format(""
                + "curl -X POST http://" + MainClass.connection + ":8088/ksql"
                + " -H \"Content-Type: application/vnd.ksql.v1+json; charset=utf-8\" "
                + " -d '{"
                + "  \"ksql\": \"EXPLAIN "
                + name.replaceAll("\"", "") + ";\","
                + "  \"streamsProperties\": {}"
                + "}'"
        );
    }


    private HttpResponse curlCall(String method, String command) {
        String cmd = String.format("curl -X " + method + " http://" + MainClass.connection + ":8083/connectors/" + current_connector.replaceAll("\"", "") + "/" + command); //work

        HttpResponse response = curl(cmd);
        return response;
    }

    private HttpResponse curlCommand(String command) {
        String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/" + current_connector.replaceAll("\"", "") + "/" + command); //work

        HttpResponse response = curl(cmd);
        return response;
    }

    private HttpResponse curlMethod(String method) {
        String cmd = String.format("curl -X " + method + " http://" + MainClass.connection + ":8083/connectors/" + current_connector.replaceAll("\"", "")); //work

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

            if(comma_chk.isSelected())
                response_string = response_string.replaceAll(",", ",\n");

            if(brace_chk.isSelected())
                response_string = response_string.replaceAll("}", "}\n");

            if (verbose) System.out.println("print response: " + response_string);


            if(redetop_chk.isSelected()){
                topTextArea.clear();
                topTextArea.setText(response_string);
            }else {
                bottomTextArea.clear();
                bottomTextArea.setText(response_string);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /////////////////////////////// FXML ZONE ///////////////////////////////////////

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
