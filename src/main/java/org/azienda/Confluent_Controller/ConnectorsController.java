package org.azienda.Confluent_Controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.toilelibre.libe.curl.Curl.curl;

public class ConnectorsController implements Initializable {

    private static String current_connector = null;

    public TextArea textarea;
    public VBox lvbox;
//    public MenuItem average;
    public MenuItem create_connector;
    public VBox topicvbox;
    public VBox connectorsvbox;
    public Button status_btn;


    //@Override
    public void initialize(URL location, ResourceBundle resources) {
        //this method is called as soon as the view is loaded
        //System.out.println("Index is now loaded!");
        String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/");

        System.out.println("comando: " + cmd);
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
                createConnectorButton(connectorsvbox, splitted[i]);
            }
            ;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Something went wrong gonna reload");
            Stage stage = (Stage) lvbox.getScene().getWindow();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("index2.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Swap screen
            stage.setScene(new Scene(root));
        }

    }

    private void createConnectorButton(VBox connectorsvbox, String s) {
        Button button;
        if (s.endsWith("]")) {//copre il caso in cui ho un unico elemento e quindi inizierebbe e finirebbe con una quadra
            s = s.substring(0, s.length() - 1);
        }
        System.out.println("s: " +s);
        System.out.println("s.sub: " +s.substring(0,1));
        if (s.substring(0,1).endsWith("[")){
            s = s.substring(1, s.length()); // need to erase first char "["
        }
        button = new Button(s);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/" + button.getText().replaceAll("\"", "")); //work
                current_connector = button.getText().replaceAll("\"", "");
                JdbcConnectorCreatorController.connector = button.getText().replaceAll("\"", "");

                HttpResponse response;
                System.out.println("comando: " + cmd);
                try {
                    response = curl(cmd);

                    HttpEntity e = response.getEntity();
                    // since once consumed can't be read again
                    // in order to use this string twice i need to store it in a String
                    String response_string = null;


                    response_string = EntityUtils.toString(e);
                    response_string = response_string.replaceAll(",",",\n");

                    textarea.setText(response_string);
                    //System.out.println(response_string);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        connectorsvbox.getChildren().add(button);

    }

    // totally to be done
    private void createTopicButton(VBox connectorsvbox, String s) {
        Button button;
        if (s.endsWith("]")) {//copre il caso in cui ho un unico elemento e quindi inizierebbe e finirebbe con una quadra
            s = s.substring(0, s.length() - 1);
        }
        if (s.substring(0,1).endsWith("[")){
            s = s.substring(1, s.length()); // need to erase first char "["
        }
        button = new Button(s);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/" + button.getText().replaceAll("\"", "")); //work

                HttpResponse response;
                System.out.println("comando: " + cmd);
                try {
                    response = curl(cmd);

                    HttpEntity e = response.getEntity();
                    // since once consumed can't be read again
                    // in order to use this string twice i need to store it in a String
                    String response_string = null;


                    response_string = EntityUtils.toString(e);
                    response_string = response_string.replaceAll(",",",\n");

                    textarea.setText(response_string);
                    System.out.println(response_string);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        connectorsvbox.getChildren().add(button);

    }

    public void createConnector(ActionEvent actionEvent){
        try {
            //in order to retrieve the stage for changing scene
            Stage stage = (Stage) lvbox.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("jdbc_connector_creator.fxml"));
            // Swap screen
            stage.setScene(new Scene(root));
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void statusConnector(ActionEvent actionEvent) {
        String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/" + current_connector.replaceAll("\"", "") + "/status"); //work

        HttpResponse response;
        System.out.println("comando: " + cmd);
        try {
            response = curl(cmd);

            HttpEntity e = response.getEntity();
            // since once consumed can't be read again
            // in order to use this string twice i need to store it in a String
            String response_string = null;


            response_string = EntityUtils.toString(e);
            response_string = response_string.replaceAll(",",",\n");

            textarea.setText(response_string);
            //System.out.println(response_string);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

//    public void averagerefresh(ActionEvent actionEvent) throws InterruptedException {
//
//        ///////////////////////////DB GET DATA/////////////////////////////////
//        try {
//            String url = "jdbc:sqlserver://192.168.1.108:1433;databaseName=SAMT4;";
//            Connection conn = DriverManager.getConnection(url,"sa","sa");
//            Statement st = conn.createStatement();
//            ResultSet sel = st.executeQuery("SELECT  alm0.ID AS ALLARME_ID" +
//                    "       ,alm0.IDOPSDLFAS AS FASE_ID" +
//                    "          ,ca0.ID AS CAUSALE_ID" +
//                    "          ,ca0.CODICE AS CAUSALE_COD" +
//                    "          ,ca0.DESCR AS CAUSALE_DES" +
//                    "          ,cel0.ID AS CELLA_ID" +
//                    "          ,cel0.CODICE AS CELLA_COD" +
//                    "          ,cel0.DESCR AS CELLA_DES" +
//                    "          ,pe0.ID AS PERSONA_ID" +
//                    "          ,pe0.CODICE AS PERSONA_COD" +
//                    "          ,pe0.DESCR1 AS PERSONA_DES" +
//                    "          ,CAST(alm0.DATIN+alm0.ORAIN-2 AS DATETIME) AS INIZIO_ALLARME" +
//                    "          ,ISNULL(CAST(alm0.DATFI+alm0.ORAFI -2 AS DATETIME),getdate()) AS FINE_ALLARME" +
//                    "          ,alm0.DUR*24*60 AS DURATA_ALLARME" +
//                    " " +
//                    "FROM ALLARMI alm0" +
//                    "     LEFT JOIN CAUALL ca0 ON ca0.ID = alm0.IDCAUALL" +
//                    "       INNER JOIN CELPRO cel0 ON cel0.ID = alm0.IDCELPRO" +
//                    "        INNER JOIN DIPENDENTI d0 ON d0.ID = alm0.IDDIPEND" +
//                    "       INNER JOIN PERSONE pe0 ON pe0.ID = d0.IDPERSONA");
//
//            //System.out.println(sel.first() + " è il primo elemento della selezione");
//            //System.out.println(sel.getRow() + " è boh sel.getRow()");
//
//            int ind = 0;
//            textarea.clear();
//            //print averageByKey
//            while(!sel.getString(ind).isEmpty()){
//                textarea.appendText("ind: " + sel.getString(ind) + "\n");
//            }
//
//                textarea.appendText("ALLARME_ID= " + sel.findColumn("ALLARME_ID") + "\n");
//                // System.out.println("bd Key=" + data._1() + " Average=" + data._2()._1() + " Total=" + data._2()._2());
//
//
//                conn.close();
//        } catch (Exception e) {
//            System.err.println("Got an exception! ");
//            System.err.println(e.getMessage());
//            e.printStackTrace();
//        }
//
//    }
//
//    private static PairFunction<Tuple2<String, Tuple2<Integer, Integer>>, String, Tuple2> getAverageByKey2 = (tuple) -> {
//        Tuple2<Integer, Integer> val2 = tuple._2;
//        int total2 = val2._1;
//        int count2 = val2._2;
//        Tuple2<String, Tuple2> averagePair2 = new Tuple2<String, Tuple2>(tuple._1, new Tuple2(total2 / count2, count2));
//        return averagePair2;
//    };
//
//    private JavaPairRDD<String, Tuple2> myCalculation(JavaRDD<ConsumerRecord<String, String>> rdd) {
//
//        System.out.println("ci sono " + rdd.count() + " nuovi elementi");
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//        JavaRDD<AAAEsempio> json_deserialized = rdd.map(p -> {
//            //System.out.println("STAMPA tutto "+p.toString());
//            // obtained with base jdbc connector on jdbc:sqlserver with org.apache.kafka.connect.json.JsonConverter on both key and value converter
//            //ConsumerRecord(topic = test-json-AAAEsempio, partition = 0, leaderEpoch = 0, offset = 29,
//            // CreateTime = 1567599629900, serialized key size = -1, serialized value size = 412,
//            // headers = RecordHeaders(headers = [], isReadOnly = false), key = null,
//            // value = {"schema":{"type":"struct","fields":[{"type":"int64","optional":false,"field":"ID"},
//            // {"type":"int32","optional":true,"name":"org.apache.kafka.connect.data.Date","version":1,"field":"TIMESTAMP"},
//            // {"type":"int64","optional":false,"field":"VALORE"},{"type":"string","optional":false,"field":"CLASSE"}],
//            // "optional":false,"name":"AAAEsempio"},
//            // "payload":{"ID":30,"TIMESTAMP":null,"VALORE":188,"CLASSE":"PALAZZO C      "}})
//            return objectMapper
//                    .readValue(p.value()
//                                    .split(",\"payload\":")[1] //split and take the second part of the string (the data)
//                            //.substring(0, p.value().split(",\"payload\":")[1].length() - 1) //cut the last char (not necessary)
//                            , AAAEsempio.class);
//        });
//
//        JavaPairRDD<String, Integer> pairRDD = json_deserialized.mapToPair(p1 ->
//                new Tuple2<String, Integer>(p1.getCLASSE(), p1.getVALORE()));
//
//        //count each values per key
//        JavaPairRDD<String, Tuple2<Integer, Integer>> valueCount = pairRDD.mapValues(value ->
//                new Tuple2<Integer, Integer>(value, 1));
//        //valueCount.foreach(x -> System.out.println("value Count: " + x));
//
//        //add values by reduceByKey
//        JavaPairRDD<String, Tuple2<Integer, Integer>> reducedCount = valueCount.reduceByKey((tuple1, tuple2) ->
//                new Tuple2<Integer, Integer>(tuple1._1 + tuple2._1, tuple1._2 + tuple2._2));
//        reducedCount.foreach(x -> System.out.println("bd reduced Count: " + x));
//
//        //Mycalculate average
//        JavaPairRDD<String, Tuple2> averagePair2 = reducedCount.mapToPair(getAverageByKey2);
//
//        return averagePair2;
//    }

}
