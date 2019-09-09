package kafka;

import java.util.Properties;

public class RunnableConsumer implements Runnable{
	
	Properties props;
	String topic;
	
	public RunnableConsumer(Properties props, String topic) {
		this.props = props;
		this.topic = topic;
	}
	
	public void run() {
		KafkaMain Kafka = new KafkaMain();
		
		Kafka.runConsumer(this.props, this.topic);
		
	}

}
