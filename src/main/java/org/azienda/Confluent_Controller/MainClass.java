package org.azienda.Confluent_Controller;
import kafka.IKafkaConstants;
import kafka.KafkaMain;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import io.debezium.config.Configuration;
import io.debezium.config.Configuration.ConfigBuilder;
import io.debezium.connector.sqlserver.SqlServerConnection;
import io.debezium.connector.sqlserver.SqlServerConnector;


/**
 * Hello world!
 *
 */
public class MainClass 
{
	public static void main(String[] args) {
		
		String connection = "192.168.1.189:9092";
		
		Properties props0 = new Properties();
		props0.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props0.put(ConsumerConfig.GROUP_ID_CONFIG, "myTestGroup");
		props0.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props0.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props0.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);
		props0.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props0.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
//		Kafka.runProducer();
		//RunnableConsumer test_local = new RunnableConsumer(props0, "demo");
		//test_local.run();
		
		Properties props1 = new Properties();
		props1.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, connection);
		props1.put(ConsumerConfig.GROUP_ID_CONFIG, "myTestGroup");
		props1.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props1.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props1.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);
		props1.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props1.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

//		Kafka.runProducer();
		//RunnableConsumer test_string = new RunnableConsumer(props1, "test-string-IVA");
		//test_string.run();
		
		
		Properties props2 = new Properties();
		props2.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, connection);
		props2.put(ConsumerConfig.GROUP_ID_CONFIG, "myTestGroup");
		props2.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props2.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props2.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);
		props2.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props2.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

//		Kafka.runProducer();
		//RunnableConsumer test_asis = new RunnableConsumer(props1, "test2-IVA");
		//test_asis.run();
		
		
		
		/// TODO create and launch a jdbc-sql source connector for sql server
		Map<String, String> propsql;
		propsql.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, connection);
		propsql.compute(, remappingFunction)
		
		//SqlServerConnection x = new SqlServerConnection((Configuration) props0);
				SqlServerConnector p = new SqlServerConnector();
				p.start(propsql);

		
		
		try {
			TimeUnit.MINUTES.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		


	}


}
