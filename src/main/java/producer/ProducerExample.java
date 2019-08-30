package producer;

import org.apache.avro.generic.GenericData;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;


public class ProducerExample {

	KafkaProducer<String, String> producer;

	public ProducerExample() {
		Properties p = new Properties();
		//Hardcoded IP for the moment - you can think to refactor it with a parameter in the constructor
		p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		p.put(ProducerConfig.ACKS_CONFIG, "all"); //get everything
		p.put(ProducerConfig.RETRIES_CONFIG, 0); //if it fails, it fails
		p.put(ProducerConfig.BATCH_SIZE_CONFIG, 16*1024); //16MB of buffer
		p.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		p.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 2*1024*1024); //2GB max memory
		p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); //send data as text
		p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producer  = new KafkaProducer<>(p);
	}

	public Future<RecordMetadata> insertMsgIntoTopic(String topicName, String key, String msg){
		ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, msg);
		Future<RecordMetadata> f = producer.send(record);
		// you can think to add f.get() to make the current method sync
		return f;
	}

	public static void main(String[] args) throws Exception {
		ProducerExample pe = new ProducerExample();
		Future<RecordMetadata> f = pe.insertMsgIntoTopic("test", "k", "v");
		RecordMetadata m = f.get();
		System.out.println(m.topic());
	}
}
