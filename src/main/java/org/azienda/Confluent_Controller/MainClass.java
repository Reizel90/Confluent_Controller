package org.azienda.Confluent_Controller;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * Hello world!
 */
public class MainClass {

    public static final String connection = "192.168.1.189:9092";
    public static final String group = "myTestGroup";

    public static void main(String[] args) {



        /// TODO create a configuration file for the SQL Server Connector and
        //  use the Kafka Connect REST API to add that connector to your Kafka Connect cluster.


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

}
