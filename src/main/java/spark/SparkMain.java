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


    private JavaSparkContext sparkContext;
    //private StreamingContext streamingContext; //sono diversi anche se si chiamano uguali
    private JavaStreamingContext streamingContext;
    private Map<String, Object> kafkaParams;

    ///////////////////////// constructor ///////////////////////////
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


    ///////////////////////////////// main ////////////////////////////////////
    //TODO preparare connettore e topic incrementing_compact since still doesn't work
    public void spark_start() throws InterruptedException {
        //TODO test on living db (updates\insert\delete)

//        test_bulk_json_delete_AAAEsempio bulk_delete = new test_bulk_json_delete_AAAEsempio(streamingContext, kafkaParams);
//        bulk_delete.average();
        // a ruota
        //bd reduced Count: (PALAZZO A      ,(448901,2965))
        //bd reduced Count: (PALAZZO B      ,(649335,5930))
        //bd reduced Count: (PALAZZO D      ,(454238,2965))
        //bd reduced Count: (PALAZZO C      ,(1097050,5930))
        //bd average pair: (PALAZZO A      ,(151,2965))
        //bd average pair: (PALAZZO B      ,(109,5930))
        //bd average pair: (PALAZZO D      ,(153,2965))
        //bd average pair: (PALAZZO C      ,(185,5930))
        //bd Key=PALAZZO A       Average=151 Total=2965
        //bd Key=PALAZZO B       Average=109 Total=5930
        //bd Key=PALAZZO D       Average=153 Total=2965
        //bd Key=PALAZZO C       Average=185 Total=5930
        //
        //bd reduced Count: (PALAZZO A      ,(2271,15))
        //bd reduced Count: (PALAZZO B      ,(3285,30))
        //bd reduced Count: (PALAZZO D      ,(2298,15))
        //bd reduced Count: (PALAZZO C      ,(5550,30))
        //bd average pair: (PALAZZO A      ,(151,15))
        //bd average pair: (PALAZZO B      ,(109,30))
        //bd average pair: (PALAZZO D      ,(153,15))
        //bd average pair: (PALAZZO C      ,(185,30))
        //bd Key=PALAZZO A       Average=151 Total=15
        //bd Key=PALAZZO B       Average=109 Total=30
        //bd Key=PALAZZO D       Average=153 Total=15
        //bd Key=PALAZZO C       Average=185 Total=30

        //should be compact, so no new IDs no party
//        test_bulk_json_compact_AAAEsempio bulk_compact = new test_bulk_json_compact_AAAEsempio(streamingContext, kafkaParams);
//        bulk_compact.average();
        //
        //bc reduced Count: (PALAZZO A      ,(34822,230))
        //bc reduced Count: (PALAZZO B      ,(50370,460))
        //bc reduced Count: (PALAZZO D      ,(35236,230))
        //bc reduced Count: (PALAZZO C      ,(85100,460))
        //bc average pair: (PALAZZO A      ,(151,230))
        //bc average pair: (PALAZZO B      ,(109,460))
        //bc average pair: (PALAZZO D      ,(153,230))
        //bc average pair: (PALAZZO C      ,(185,460))
        //bc Key=PALAZZO A       Average=151 Total=230
        //bc Key=PALAZZO B       Average=109 Total=460
        //bc Key=PALAZZO D       Average=153 Total=230
        //bc Key=PALAZZO C       Average=185 Total=460
        //
        //bc reduced Count: (PALAZZO A      ,(2271,15))
        //bc reduced Count: (PALAZZO B      ,(3285,30))
        //bc reduced Count: (PALAZZO D      ,(2298,15))
        //bc reduced Count: (PALAZZO C      ,(5550,30))
        //bc average pair: (PALAZZO A      ,(151,15))
        //bc average pair: (PALAZZO B      ,(109,30))
        //bc average pair: (PALAZZO D      ,(153,15))
        //bc average pair: (PALAZZO C      ,(185,30))
        //bc Key=PALAZZO A       Average=151 Total=15
        //bc Key=PALAZZO B       Average=109 Total=30
        //bc Key=PALAZZO D       Average=153 Total=15
        //bc Key=PALAZZO C       Average=185 Total=30

//        test_incrementing_json_delete_AAAEsempio incrementing_delete = new test_incrementing_json_delete_AAAEsempio(streamingContext, kafkaParams);
//        incrementing_delete.average();
        // solo una volta
        //id reduced Count: (PALAZZO A      ,(757,5))
        //id reduced Count: (PALAZZO B      ,(1095,10))
        //id reduced Count: (PALAZZO D      ,(766,5))
        //id reduced Count: (PALAZZO C      ,(1850,10))
        //id average pair: (PALAZZO A      ,(151,5))
        //id average pair: (PALAZZO B      ,(109,10))
        //id average pair: (PALAZZO D      ,(153,5))
        //id average pair: (PALAZZO C      ,(185,10))
        //id Key=PALAZZO A       Average=151 Total=5
        //id Key=PALAZZO B       Average=109 Total=10
        //id Key=PALAZZO D       Average=153 Total=5
        //id Key=PALAZZO C       Average=185 Total=10

        test_incrementing_json_compact_AAAEsempio incrementing_compact = new test_incrementing_json_compact_AAAEsempio(streamingContext, kafkaParams);
        incrementing_compact.average();

        streamingContext.start();
        streamingContext.awaitTermination();

    }

    public void RDD_from_topic() {
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

//        stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));

        System.out.println("1direct count: " + stream.count());

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

        System.out.println("2direct count: " + stream.count());

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
