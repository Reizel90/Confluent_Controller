package kafka;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;


public class ConsumerKafka {

	public static Consumer<Long, String> createConsumer(Properties props, String topic) {

		final Consumer<Long, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(topic));
		return consumer;
	}
}
