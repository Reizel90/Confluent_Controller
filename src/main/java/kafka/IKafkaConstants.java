package kafka;

public interface IKafkaConstants {
	//ifconfig su unix system per controllare ip
	public static String KAFKA_BROKERS = "192.168.1.189:9092"; //kafka broker es. cluster "localhost:9091,localhost:9092"
    public static Integer MESSAGE_COUNT=1000;
    public static String CLIENT_ID="client1"; // producer ID per far sapere la sorgente delle info a kafka
    public static String TOPIC_NAME="test-AAAEsempio";
    public static String GROUP_ID_CONFIG="consumerGroup1";
    public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
    public static String OFFSET_RESET_LATEST="latest";
    public static String OFFSET_RESET_EARLIER="earliest";
    public static Integer MAX_POLL_RECORDS=1;
}
