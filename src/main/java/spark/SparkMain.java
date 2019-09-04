package spark;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.azienda.Confluent_Controller.MainClass;

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
        streamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(15));

        // decido di stampare soltanto gli errori e non le INFO
        sparkContext.setLogLevel("ERROR");

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
        kafkaParams.put("auto.offset.reset", "earliest"); //earliest or latest
        kafkaParams.put("enable.auto.commit", false);
    }

    public void read_topics_spark() throws InterruptedException {

        // Collection<String> topics2 = Arrays.asList("test-string-ZONE", "test-string-AAAEsempio");
        Collection<String> topics2 = Arrays.asList("test-string-AAAEsempio");

        JavaInputDStream<ConsumerRecord<String, String>> stream2 =
            KafkaUtils.createDirectStream(
                streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics2, kafkaParams)
            );

        stream2.foreachRDD(rdd -> {

//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//            JavaPairRDD<String, Iterable<Integer>> x = rdd.map(p -> objectMapper.readValue(p.value()
//                            .replace("=", ":")
//                            .replace("Struct", ""),
//                    AAAEsempio.class))
//                    .mapToPair(p -> new Tuple2<>(p.getCLASSE(), p.getVALORE()))
//                    .groupByKey();


            // to show on console the contents of topics
            rdd.foreach(record -> {
                record.value().replace("=", ":").replace("Struct", "");
                System.out.println("record no conversion: " + record.toString());
                //record no conversion: ConsumerRecord(topic = test-string-AAAEsempio, partition = 0, leaderEpoch = 0, offset = 14, CreateTime = 1567590742194, serialized key size = -1, serialized value size = 47, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = Struct{ID=15,VALORE=192,CLASSE=PALAZZO C      })
                System.out.println("value no conversion: " + record.value().toString());
                // value no conversion: Struct{ID=29,CODICE=FZ  ,DESCR=FAENZA //(without the map)
            });
        });

        streamingContext.start();
        streamingContext.awaitTermination();

    }

    public void RDD_from_topic(){
        // Import dependencies and create kafka params as in Create Direct Stream above
        OffsetRange[] offsetRanges = {
                // topic, partition, inclusive starting offset, exclusive ending offset
                OffsetRange.create("test-json-ZONE", 0, 0, 100),
                OffsetRange.create("test-AAAEsempio", 1, 0, 100)
        };

        JavaRDD<ConsumerRecord<String, String>> rdd = KafkaUtils.createRDD(
                sparkContext,
                kafkaParams,
                offsetRanges,
                LocationStrategies.PreferConsistent()
        );

        System.out.println("rdd: " + rdd.count());
    }

//    public void code(){
//
//        byte[] keyBytes = keyJsonConverter.fromConnectData(record.topic(), record.keySchema(), record.key());
//        msg = "deserializing key using JSON deserializer";
//        keyJson = keyJsonDeserializer.deserialize(record.topic(), keyBytes);
//        msg = "deserializing key using JSON converter";
//        keyWithSchema = keyJsonConverter.toConnectData(record.topic(), keyBytes);
//        msg = "comparing key schema to that serialized/deserialized with JSON converter";
//        assertThat(keyWithSchema.schema()).isEqualTo(record.keySchema());
//        byte[] valueBytes = valueJsonConverter.fromConnectData(record.topic(), record.valueSchema(), record.value());
//        msg = "deserializing value using JSON deserializer";
//        valueJson = valueJsonDeserializer.deserialize(record.topic(), valueBytes);
//        msg = "deserializing value using JSON converter";
//        valueWithSchema = valueJsonConverter.toConnectData(record.topic(), valueBytes);
//        msg = "comparing value schema to that serialized/deserialized with JSON converter";
//        assertEquals(valueWithSchema.schema(), record.valueSchema());
//
//    }


    /////////////////////////// TEST ZONE ////////////////////////////////////

    public void restart_streaming_context() throws InterruptedException {
        System.out.println(streamingContext.getState());
        //if(streamingContext.getState().equals("ACTIVE"))
        // stop streaming without stopping context
        streamingContext.stop(false, true);
        streamingContext.awaitTermination();
        // substitute old streaming context with a new one
        streamingContext = null;
        streamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(15));
        direct_stream2();
    }

    public void direct_stream() throws InterruptedException {

        // be careful to name topics
        Collection<String> topics = Arrays.asList("test-json-ZONE", "test-json-AAAEsempio");

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
//        stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));

        System.out.println("1direct count: " +stream.count());

        // to print offsets
        // lambda expressions are closures.
//        stream.foreachRDD(rdd -> {
//            System.out.println("1direct rdd count: " +rdd.count());
//            //Note that the typecast to HasOffsetRanges will only succeed if it is done in the first method
//            // called on the result of createDirectStream, not later down a chain of methods.
//            // Be aware that the one-to-one mapping between RDD partition and Kafka partition
//            // does not remain after any methods that shuffle or repartition, e.g. reduceByKey() or window().
//            OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
//            rdd.foreachPartition(consumerRecords -> {
//                OffsetRange o = offsetRanges[TaskContext.get().partitionId()];
//                System.out.println("1INFO: " +
//                        o.topic() + " " + o.partition() + " " + o.fromOffset() + " " + o.untilOffset());
//            });
//        });

        System.out.println("chkpt");

        // a solution to manage enable.auto.commit = false
//        stream.foreachRDD(rdd -> {
//            OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
//
//            // some time later, after outputs have completed
//            ((CanCommitOffsets) stream.inputDStream()).commitAsync(offsetRanges);
//        });

        stream.foreachRDD(rdd -> {

//            System.out.println("--- New RDD with " + rdd.partitions().size()
//                    + " partitions and " + rdd.count() + " records");
           // --- New RDD with 2 partitions and 0 records

            rdd.foreach(record -> {
                System.out.println("record json: " + record.toString());
                System.out.println("value json: " + record.value());
            });
        });

        System.out.println();

        ////////////////////////////// test 2 from not converted data ////////////////////////////////////////
//        Collection<String> topics2 = Arrays.asList("test-no-converter-ZONE", "test-no-converter-AAAEsempio");
//        JavaInputDStream<ConsumerRecord<String, String>> stream2 =
//                KafkaUtils.createDirectStream(
//                        streamingContext,
//                        LocationStrategies.PreferConsistent(),
//                        ConsumerStrategies.<String, String>Subscribe(topics2, kafkaParams)
//                );
//
//        stream2.foreachRDD(rdd -> {
//            rdd.foreach(record -> {
//                System.out.println("record no conversion: " + record.toString());
//                System.out.println("value no conversion: " + record.value().toString());
//            });
//        });

        streamingContext.start();
        streamingContext.awaitTermination();

        //restart_streaming_context();


    }

    public void direct_stream2() throws InterruptedException {

        Collection<String> topics = Arrays.asList("test-json-ZONE", "test-json-AAAEsempio");

        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
                );

        System.out.println("2direct count: " +stream.count());

        stream.foreachRDD(rdd -> {
            rdd.foreach(record -> {
                System.out.println("2record json: " + record.toString());
                System.out.println("2value json: " + record.value());
            });
        });
        System.out.println();

        ////////////////////////////// test 2 from not converted data ////////////////////////////////////////
        Collection<String> topics2 = Arrays.asList("test-no-converter-ZONE", "test-no-converter-AAAEsempio");
        JavaInputDStream<ConsumerRecord<String, String>> stream2 =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics2, kafkaParams)
                );

        stream2.foreachRDD(rdd -> {
            rdd.foreach(record -> {
                System.out.println("2record no conversion: " + record.toString());
                System.out.println("2value no conversion: " + record.value().toString());
            });
        });

        streamingContext.start();
        streamingContext.awaitTermination();
    }

}
