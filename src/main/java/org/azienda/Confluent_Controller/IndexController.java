package org.azienda.Confluent_Controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.entity.AAAEsempio;
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
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;
import spark.SparkMain;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

import static org.toilelibre.libe.curl.Curl.curl;

public class IndexController implements Initializable {

    public TextArea textarea;
    public VBox lvbox;
    public MenuItem average;

    //@Override
    public void initialize(URL location, ResourceBundle resources) {
        //this method is called as soon as the view is loaded
        System.out.println("Index is now loaded!");

        String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/");

        HttpResponse response;
        System.out.println("comando: " + cmd);
        try {
            response = curl(cmd);

            HttpEntity e = response.getEntity();
            // since once consumed can't be read again so in order to use this string twice i need to store it in a String
            String response_string = null;


            response_string = EntityUtils.toString(e);

            String[] splitted = response_string.split(",");
            lvbox.getChildren().clear();
            // il primo item è brutto
            for (int i = 0; i < splitted.length; i++) {
                System.out.println(splitted[i]);
                Button button;
                if (i == 0)
                    button = new Button(splitted[i].substring(1, splitted[i].length())); // need to erase first char "["
                else if (i == splitted.length - 1)
                    button = new Button(splitted[i].substring(0, splitted[i].length() - 1)); // need to erase last char "]"
                else button = new Button(splitted[i]);
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String cmd = String.format("curl http://" + MainClass.connection + ":8083/connectors/" + button.getText().replaceAll("\"", "")); //work

                        HttpResponse response;
                        System.out.println("comando: " + cmd);
                        try {
                            response = curl(cmd);

                            HttpEntity e = response.getEntity();
                            // since once consumed can't be read again so in order to use this string twice i need to store it in a String
                            String response_string = null;


                            response_string = EntityUtils.toString(e);
                            textarea.setText(response_string);
                            System.out.println(response_string);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                lvbox.getChildren().add(button);
            }
            ;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Something went wrong");
            Stage stage = (Stage) lvbox.getScene().getWindow();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("index.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Swap screen
            stage.setScene(new Scene(root));
        }

    }

    public void averagerefresh(ActionEvent actionEvent) throws InterruptedException {

        try {
            if (!SparkMain.getStreamingContext().getState().toString().equals("INITIALIZED"))
                SparkMain.reset_streaming_context();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.toString());
        }

        Map<String, Object> kafkaParams = new HashMap<>();
        //http://kafka.apache.org/documentation.html#newconsumerconfigs
        // If your Spark batch duration is larger than the default Kafka heartbeat session timeout (30 seconds),
        // increase heartbeat.interval.ms and session.timeout.ms appropriately. For batches larger than 5 minutes,
        // this will require changing group.max.session.timeout.ms on the broker.
        //kafkaParams.put("bootstrap.servers", "localhost:9092,anotherhost:9092");
        kafkaParams.put("bootstrap.servers", MainClass.connection + ":9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", MainClass.group);
        kafkaParams.put("auto.offset.reset", "earliest"); //earliest or latest
        kafkaParams.put("enable.auto.commit", false);

        // Collection<String> topics2 = Arrays.asList("test-string-ZONE", "test-string-AAAEsempio");
        //Collection<String> topics2 = Arrays.asList("test-json-AAAEsempio");
        Collection<String> topics2 = Arrays.asList("test-bulk-json-delete-AAAEsempio");

        JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(
                SparkMain.getStreamingContext(),
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics2, kafkaParams)
        );

        stream.foreachRDD(rdd -> {
            if (rdd.isEmpty()) {
                System.out.println("niente di nuovo");
                //nothing to do
            } else {

                JavaPairRDD<String, Tuple2> averagePair2 = myCalculation(rdd);

                ///////////////////////////DB SAVEFATA/////////////////////////////////
//                try {
//                    String url = "jdbc:msql://200.210.220.1:1114/Demo";
//                    Connection conn = DriverManager.getConnection(url,"","");
//                    Statement st = conn.createStatement();
//                    st.executeUpdate("INSERT INTO Customers " +
//                            "VALUES (1001, 'Simpson', 'Mr.', 'Springfield', 2001)");
//                    st.executeUpdate("INSERT INTO Customers " +
//                            "VALUES (1002, 'McBeal', 'Ms.', 'Boston', 2004)");
//                    st.executeUpdate("INSERT INTO Customers " +
//                            "VALUES (1003, 'Flinstone', 'Mr.', 'Bedrock', 2003)");
//                    st.executeUpdate("INSERT INTO Customers " +
//                            "VALUES (1004, 'Cramden', 'Mr.', 'New York', 2001)");
//
//                    conn.close();
//                } catch (Exception e) {
//                    System.err.println("Got an exception! ");
//                    System.err.println(e.getMessage());
//                }

                Connection connection = null;
                try {
                    // the sql server driver string
                    //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                    // the sql server url
                    String url = "jdbc:sqlserver://192.168.1.108:1433;DatabaseName=SAMT4";

                    // get the sql server database connection
                    connection = DriverManager.getConnection(url, "sa", "sa");

                    System.out.println("mi sono connesso al database");
                    // now do whatever you want to do with the connection
                    // ...

                    Statement statement = connection.createStatement();

                    //// create example
//                    String code = "Create ...."; /* your view creation SQL */
//                    statement.executeUpdate(code);

                    //// query example
//                    String code2 = "SELECT Lname FROM Customers WHERE Snum = 2001"; /* your view creation SQL */
//                    ResultSet rs = statement.executeQuery(code2);

                    connection.close();
                }
//                catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                    System.exit(1);
//                }
                catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(2);
                }


                textarea.clear();
                //print averageByKey
                averagePair2.collect().forEach(data -> {
                    textarea.appendText("bd Key=" + data._1() + " Average=" + data._2()._1() + " Total=" + data._2()._2() + "\n");
                    // System.out.println("bd Key=" + data._1() + " Average=" + data._2()._1() + " Total=" + data._2()._2());
                });

            }
        });

        SparkMain.getStreamingContext().start();
    }

    private static PairFunction<Tuple2<String, Tuple2<Integer, Integer>>, String, Tuple2> getAverageByKey2 = (tuple) -> {
        Tuple2<Integer, Integer> val2 = tuple._2;
        int total2 = val2._1;
        int count2 = val2._2;
        Tuple2<String, Tuple2> averagePair2 = new Tuple2<String, Tuple2>(tuple._1, new Tuple2(total2 / count2, count2));
        return averagePair2;
    };


    private JavaPairRDD<String, Tuple2> myCalculation(JavaRDD<ConsumerRecord<String, String>> rdd) {

        System.out.println("ci sono " + rdd.count() + " nuovi elementi");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        JavaRDD<AAAEsempio> json_deserialized = rdd.map(p -> {
            //System.out.println("STAMPA tutto "+p.toString());
            // obtained with base jdbc connector on jdbc:sqlserver with org.apache.kafka.connect.json.JsonConverter on both key and value converter
            //ConsumerRecord(topic = test-json-AAAEsempio, partition = 0, leaderEpoch = 0, offset = 29,
            // CreateTime = 1567599629900, serialized key size = -1, serialized value size = 412,
            // headers = RecordHeaders(headers = [], isReadOnly = false), key = null,
            // value = {"schema":{"type":"struct","fields":[{"type":"int64","optional":false,"field":"ID"},
            // {"type":"int32","optional":true,"name":"org.apache.kafka.connect.data.Date","version":1,"field":"TIMESTAMP"},
            // {"type":"int64","optional":false,"field":"VALORE"},{"type":"string","optional":false,"field":"CLASSE"}],
            // "optional":false,"name":"AAAEsempio"},
            // "payload":{"ID":30,"TIMESTAMP":null,"VALORE":188,"CLASSE":"PALAZZO C      "}})
            return objectMapper
                    .readValue(p.value()
                                    .split(",\"payload\":")[1] //split and take the second part of the string (the data)
                            //.substring(0, p.value().split(",\"payload\":")[1].length() - 1) //cut the last char (not necessary)
                            , AAAEsempio.class);
        });

        JavaPairRDD<String, Integer> pairRDD = json_deserialized.mapToPair(p1 ->
                new Tuple2<String, Integer>(p1.getCLASSE(), p1.getVALORE()));

        //count each values per key
        JavaPairRDD<String, Tuple2<Integer, Integer>> valueCount = pairRDD.mapValues(value ->
                new Tuple2<Integer, Integer>(value, 1));
        //valueCount.foreach(x -> System.out.println("value Count: " + x));

        //add values by reduceByKey
        JavaPairRDD<String, Tuple2<Integer, Integer>> reducedCount = valueCount.reduceByKey((tuple1, tuple2) ->
                new Tuple2<Integer, Integer>(tuple1._1 + tuple2._1, tuple1._2 + tuple2._2));
        reducedCount.foreach(x -> System.out.println("bd reduced Count: " + x));

        //Mycalculate average
        JavaPairRDD<String, Tuple2> averagePair2 = reducedCount.mapToPair(getAverageByKey2);

        return averagePair2;
    }
}
