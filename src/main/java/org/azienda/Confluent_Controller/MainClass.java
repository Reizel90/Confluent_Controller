package org.azienda.Confluent_Controller;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * Hello world!
 */
public class MainClass {

    public static final String testconnection = "192.168.1.189";

    public static final String connection = "192.168.1.189:9092";
    public static final String group = "myTestGroup";

    public static void main(String[] args) throws IOException {

        //rest_request(); //java.net.ConnectException: Connection timed out: connect alla linea get response code





        String base_path = System.getProperty("user.dir");
        String resources_path = base_path  + "\\src\\main\\resources";
        System.out.println(resources_path);

        //esempio configurazione proprietà connettore
//        {
//            "name": "inventory-connector",  (1)
//            "config": {
    //            "connector.class": "io.debezium.connector.sqlserver.SqlServerConnector", (2)
    //            "database.hostname": "192.168.99.100", (3)
    //            "database.port": "1433", (4)
    //            "database.user": "sa", (5)
    //            "database.password": "Password!", (6)
    //            "database.dbname": "testDB", (7)
    //            "database.server.name": "fullfillment", (8)
    //            "table.whitelist": "dbo.customers", (9)
    //            "database.history.kafka.bootstrap.servers": "kafka:9092", (10)
    //            "database.history.kafka.topic": "dbhistory.fullfillment" (11)
//          }
//        }




        try {
            TimeUnit.MINUTES.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void local_consumer() {
        Properties props0 = propes("localhost:9092");

        RunnableConsumer test_local = new RunnableConsumer(props0, "demo");
        test_local.run();
    }

    public void remote_string_consumer() {
        Properties props1 = propes(this.connection);

        RunnableConsumer test_string = new RunnableConsumer(props1, "test-string-IVA");
        test_string.run();
    }

    public void remote_consumer(){
		Properties props2 = propes(this.connection);

		RunnableConsumer test_asis = new RunnableConsumer(props2, "test2-IVA");
		test_asis.run();
	}

	public Properties propes(String connection){
        Properties props0 = new Properties();
        props0.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, connection);
        props0.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props0.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props0.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props0.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);
        props0.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props0.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props0;
    }

    public static void rest_request() throws IOException {
        // https://debezium.io/docs/connectors/sqlserver/
        // TODO create a configuration file for the SQL Server Connector and
        //  use the Kafka Connect REST API to add that connector to your Kafka Connect cluster.

        URL urlForGetRequest = new URL("https://"+ testconnection + ":9092/connectors" );
        //URL urlForGetRequest = new URL("https://jsonplaceholder.typicode.com/posts/1");
        String readLine = null;
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
        conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
        int responseCode = conection.getResponseCode();
        System.out.println(responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in .readLine()) != null) {
                response.append(readLine);
            } in .close();
            // print result
            System.out.println("JSON String Result " + response.toString());
            //GetAndPost.POSTRequest(response.toString());
        } else {
            System.out.println("GET NOT WORKED");
        }
    }

}
