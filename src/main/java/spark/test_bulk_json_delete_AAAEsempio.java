package spark;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.entity.AAAEsempio;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class test_bulk_json_delete_AAAEsempio {

    private JavaStreamingContext streamingContext;
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
        Tuple2<Integer, Integer> val2 = tuple._2;
        int total2 = val2._1;
        int count2 = val2._2;
        Tuple2<String, Tuple2> averagePair2 = new Tuple2<String, Tuple2>(tuple._1, new Tuple2(total2 / count2, count2));
        return averagePair2;
    };

    public test_bulk_json_delete_AAAEsempio(JavaStreamingContext jsc, Map<String, Object> params){
        streamingContext = jsc;
        kafkaParams = params;
    }

    public void average() throws InterruptedException {

        // Collection<String> topics2 = Arrays.asList("test-string-ZONE", "test-string-AAAEsempio");
        //Collection<String> topics2 = Arrays.asList("test-json-AAAEsempio");
        Collection<String> topics2 = Arrays.asList("test-bulk-json-delete-AAAEsempio");

        JavaInputDStream<ConsumerRecord<String, String>> stream2 =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics2, kafkaParams)
                );

        stream2.foreachRDD(rdd -> {
            if (rdd.isEmpty()){
                System.out.println("niente di nuovo");
                //nothing to do
            }
            else{
                System.out.println("ci sono " + rdd.count() + " nuovi elementi" );
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
            reducedCount.foreach(x -> System.out.println("bd reduced Count: " + x));

//            //calculate average
//            JavaPairRDD<String, Integer> averagePair = reducedCount.mapToPair(getAverageByKey);
//            averagePair.foreach(x -> System.out.println("average pair: " + x));
//
//            //print averageByKey
//            averagePair.foreach(data -> {
//                System.out.println("Key=" + data._1() + " Average=" + data._2());
//            });

            //Mycalculate average
            JavaPairRDD<String, Tuple2> averagePair2 = reducedCount.mapToPair(getAverageByKey2);
            averagePair2.foreach(x -> System.out.println("bd average pair: " + x));

            //print averageByKey
            averagePair2.foreach(data -> {
                System.out.println("bd Key=" + data._1() + " Average=" + data._2()._1() + " Total=" + data._2()._2());
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
            }
        });

    }
}
