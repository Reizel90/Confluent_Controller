package kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;


public class ProducerKafka {

	public static Producer<Long, String> createProducer(Properties props) {
		//The class that will be used to determine the partition in which the record will go.
		//props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
		return new KafkaProducer<>(props);
	}
}
