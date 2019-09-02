package spark;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import org.azienda.Confluent_Controller.MainClass;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SparkMain {

    //private StreamingContext streamingContext; //sono diversi anche se si chiamano uguali
    private JavaStreamingContext streamingContext;
    private JavaSparkContext sparkContext;
    private Map<String, Object> kafkaParams;


    public SparkMain() {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
        sparkContext = new JavaSparkContext(conf);
        streamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(1));

        kafkaParams = new HashMap<>();
        //http://kafka.apache.org/documentation.html#newconsumerconfigs
        // If your Spark batch duration is larger than the default Kafka heartbeat session timeout (30 seconds),
        // increase heartbeat.interval.ms and session.timeout.ms appropriately. For batches larger than 5 minutes,
        // this will require changing group.max.session.timeout.ms on the broker.
        //kafkaParams.put("bootstrap.servers", "localhost:9092,anotherhost:9092");
        kafkaParams.put("bootstrap.servers", MainClass.connection);
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", MainClass.group);
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);
    }

    public void direct_stream() {
        Collection<String> topics = Arrays.asList("test-AsIs-IVA", "test-json-ZONE", "test-AAAEsempio");

        JavaInputDStream<ConsumerRecord<String, String>> stream =
            KafkaUtils.createDirectStream(
                streamingContext,
                //In most cases, you should use LocationStrategies.PreferConsistent as shown above.
                // This will distribute partitions evenly across available executors.
                // If your executors are on the same hosts as your Kafka brokers, use PreferBrokers,
                // which will prefer to schedule partitions on the Kafka leader for that partition.
                // Finally, if you have a significant skew in load among partitions, use PreferFixed.
                // This allows you to specify an explicit mapping of partitions to hosts
                // (any unspecified partitions will use a consistent location).
                LocationStrategies.PreferConsistent(),
                //ConsumerStrategies.Subscribe, as shown above, allows you to subscribe to a fixed collection of topics.
                // SubscribePattern allows you to use a regex to specify topics of interest.
                // Note that unlike the 0.8 integration, using Subscribe or SubscribePattern should respond to adding
                // partitions during a running stream.
                // Finally, Assign allows you to specify a fixed collection of partitions.
                // All three strategies have overloaded constructors that allow you to specify the starting offset
                // for a particular partition.
                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
            );

        //TODO need to understand what it does
        stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));

        // to print offsets
        stream.foreachRDD(rdd -> {
            //Note that the typecast to HasOffsetRanges will only succeed if it is done in the first method
            // called on the result of createDirectStream, not later down a chain of methods.
            // Be aware that the one-to-one mapping between RDD partition and Kafka partition
            // does not remain after any methods that shuffle or repartition, e.g. reduceByKey() or window().
            OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
            rdd.foreachPartition(consumerRecords -> {
                OffsetRange o = offsetRanges[TaskContext.get().partitionId()];
                System.out.println(
                        o.topic() + " " + o.partition() + " " + o.fromOffset() + " " + o.untilOffset());
            });
        });

        // a solution to manage enable.auto.commit = false
        stream.foreachRDD(rdd -> {
            OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();

            // some time later, after outputs have completed
            ((CanCommitOffsets) stream.inputDStream()).commitAsync(offsetRanges);
        });
    }

    public void RDD_from_topic(){
        // Import dependencies and create kafka params as in Create Direct Stream above
        OffsetRange[] offsetRanges = {
                // topic, partition, inclusive starting offset, exclusive ending offset
                OffsetRange.create("test", 0, 0, 100),
                OffsetRange.create("test", 1, 0, 100)
        };

        JavaRDD<ConsumerRecord<String, String>> rdd = KafkaUtils.createRDD(
                sparkContext,
                kafkaParams,
                offsetRanges,
                LocationStrategies.PreferConsistent()
        );
    }

}
