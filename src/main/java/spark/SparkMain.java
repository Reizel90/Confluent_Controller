package spark;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.entity.AAAEsempio;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.OffsetRange;
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

    // A lambda expression is an anonymous(nameless) function
    // which don’t have any name and does not belong to any class
    // i.e it is a block of code that can be passed around to execute.
    // it's a variable, not a method.
    private static PairFunction<Tuple2<String, Tuple2<Integer, Integer>>, String, Integer> getAverageByKey = (tuple) -> {
        Tuple2<Integer, Integer> val = tuple._2;
        int total = val._1;
        int count = val._2;
        Tuple2<String, Integer> averagePair = new Tuple2<String, Integer>(tuple._1, total / count);
        return averagePair;
    };

    // it's a variable, not a method.
    private static PairFunction<Tuple2<String, Tuple2<Integer, Integer>>, String, Tuple2> getAverageByKey2 = (tuple) -> {
        Tuple2<Integer, Integer> val = tuple._2;
        int total = val._1;
        int count = val._2;
        Tuple2<String, Tuple2> averagePair2 = new Tuple2<String, Tuple2>(tuple._1, new Tuple2(total / count, count));
        return averagePair2;
    };


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

    public void read_test_bulk_json_AAAEsempio_average() throws InterruptedException {

        // Collection<String> topics2 = Arrays.asList("test-string-ZONE", "test-string-AAAEsempio");
        //Collection<String> topics2 = Arrays.asList("test-json-AAAEsempio");
        Collection<String> topics2 = Arrays.asList("test-bulk-json-AAAEsempio");

        JavaInputDStream<ConsumerRecord<String, String>> stream2 =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics2, kafkaParams)
                );

        stream2.foreachRDD(rdd -> {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            JavaRDD<AAAEsempio> json_deserialized = rdd.map(p -> {
                //System.out.println("STAMPA tutto "+p.toString());
                // obtained with base jdbc connector on jdbc:sqlserver with org.apache.kafka.connect.json.JsonConverter on both key and value converter
                //ConsumerRecord(topic = test-json-AAAEsempio, partition = 0, leaderEpoch = 0, offset = 29,
                // CreateTime = 1567599629900, serialized key size = -1, serialized value size = 412,
                // headers = RecordHeaders(headers = [], isReadOnly = false), key = null,
                // value = {"schema":{"type":"struct","fields":[{"type":"int64","optional":false,"field":"ID"},
                // {"type":"int32","optional":true,"name":"org.apache.kafka.connect.data.Date","version":1,"field":"TIMESTAMP"},
                // {"type":"int64","optional":false,"field":"VALORE"},{"type":"string","optional":false,"field":"CLASSE"}],
                // "optional":false,"name":"AAAEsempio"},
                // "payload":{"ID":30,"TIMESTAMP":null,"VALORE":188,"CLASSE":"PALAZZO C      "}})
                return objectMapper
                        .readValue(p.value()
                                        .split(",\"payload\":")[1] //split and take the second part of the string (the data)
                                //.substring(0, p.value().split(",\"payload\":")[1].length() - 1) //cut the last char (not necessary)
                                , AAAEsempio.class);
            });

            JavaPairRDD<String, Integer> pairRDD = json_deserialized.mapToPair(p1 ->
                    new Tuple2<String, Integer>(p1.getCLASSE(), p1.getVALORE()));

            //count each values per key
            JavaPairRDD<String, Tuple2<Integer, Integer>> valueCount = pairRDD.mapValues(value ->
                    new Tuple2<Integer, Integer>(value, 1));
            //valueCount.foreach(x -> System.out.println("value Count: " + x));

            //add values by reduceByKey
            JavaPairRDD<String, Tuple2<Integer, Integer>> reducedCount = valueCount.reduceByKey((tuple1, tuple2) ->
                    new Tuple2<Integer, Integer>(tuple1._1 + tuple2._1, tuple1._2 + tuple2._2));
            reducedCount.foreach(x -> System.out.println("reduced Count: " + x));

//            //calculate average
//            JavaPairRDD<String, Integer> averagePair = reducedCount.mapToPair(getAverageByKey);
//            averagePair.foreach(x -> System.out.println("average pair: " + x));
//
//            //print averageByKey
//            averagePair.foreach(data -> {
//                System.out.println("Key=" + data._1() + " Average=" + data._2());
//            });

            //Mycalculate average
            JavaPairRDD<String, Tuple2> averagePair = reducedCount.mapToPair(getAverageByKey2);
            averagePair.foreach(x -> System.out.println("average pair: " + x));

            //print averageByKey
            averagePair.foreach(data -> {
                System.out.println("Key=" + data._1() + " Average=" + data._2()._1() + " Total=" + data._2()._2());
            });
            // depending on topic and connector settings
            // conector table loading mode bulk or incrementing (bulk always take the full data, incrementing only the new rows)
            // topic cleanup policies delete (deleting old messages time based) compact (saves last value foreach unique key)
            //Key=PALAZZO A       Average=151 Total=2865
            //Key=PALAZZO B       Average=109 Total=5730
            //Key=PALAZZO D       Average=153 Total=2865
            //Key=PALAZZO C       Average=185 Total=5730

            JavaPairRDD<String, Tuple2> x = json_deserialized.mapToPair(p1 ->
                    new Tuple2<String, Tuple2>(p1.getCLASSE(), new Tuple2<Integer, Integer>(p1.getVALORE(), 1))
            );

            Map<String, Long> x1 = x.countByKey();

            //JavaPairRDD<String, Iterable<Integer>> grouped = json_deserialized.mapToPair(p1 -> new Tuple2<String, Integer>(p1.getCLASSE(), p1.getVALORE()))
            //        .groupByKey();

            //grouped.foreach(tuple -> System.out.println("grouped " + tuple.toString() + " - KEY: " + tuple._1 + " - VALUE: " + tuple._2));
            //grouped (PALAZZO A      ,[152, 158, 144, 151, 152])
            //grouped (PALAZZO B      ,[112, 102, 116, 117, 101])
            //grouped (PALAZZO C      ,[194, 185, 179, 175, 192])

            System.out.println();

        });

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
