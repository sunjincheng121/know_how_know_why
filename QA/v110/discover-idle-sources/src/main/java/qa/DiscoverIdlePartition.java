package qa;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.io.jdbc.JDBCAppendTableSink;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.types.Row;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Test for discover idle partition.
 * Run this class first then run the ProducerTestData.java
 */
public class DiscoverIdlePartition {
    public static void main(String[] args) throws Exception {
        // with idle check， 7 output
        StreamExecutionEnvironment env = createEnv(true);
        // without idle check, 4 output
//        StreamExecutionEnvironment env = createEnv(false);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");

        TypeInformation<Row> rowType = Types.ROW_NAMED(
                new String[]{"id", "cnt", "ts"},
                Types.STRING,
                Types.INT,
                Types.SQL_TIMESTAMP);

        FlinkKafkaConsumer010<Row> consumer010 = new FlinkKafkaConsumer010<Row>(
                "checkIdle",
                new JsonRowDeserializationSchema(rowType),
                properties);
        consumer010.setStartFromLatest();

        DataStream<Row> stream = env.addSource(consumer010);
        DataStream<Row> ds = stream.assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<Row>() {
            @Nullable
            @Override
            public Watermark checkAndGetNextWatermark(Row lastElement, long extractedTimestamp) {
                Watermark wm = new Watermark(extractedTimestamp);
//                System.err.println(String.format("WM[%s]", new Timestamp(extractedTimestamp).toString()));
                return wm;
            }

            @Override
            public long extractTimestamp(Row element, long previousElementTimestamp) {
                return ((Timestamp) element.getField(2)).getTime();
            }
        }).map(new MapFunction<Row, Tuple3<String, Timestamp, Integer>>() {
            @Override
            public Tuple3<String, Timestamp, Integer> map(Row row) throws Exception {
                return new Tuple3<>((String) row.getField(0), (Timestamp) row.getField(2), (Integer) row.getField(1));
            }
        }).keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(5))).max(2).map(new MapFunction<Tuple3<String, Timestamp, Integer>, Row>() {
                    @Override
                    public Row map(Tuple3<String, Timestamp, Integer> data) throws Exception {
                        return Row.of(data.f0, data.f1, data.f2);
                    }
                });

        // You can also using print() for simple.

        JDBCAppendTableSink sink = JDBCAppendTableSink.builder()
                .setDrivername("com.mysql.jdbc.Driver")
                .setDBUrl("jdbc:mysql://localhost/flink_db?useUnicode=true&characterEncoding=utf-8&useSSL=false")
                .setUsername("root")
                .setPassword("123456")
                .setParameterTypes(
                        Types.STRING,
                        Types.SQL_TIMESTAMP,
                        Types.INT)
                .setQuery("insert into tab_string_long values(?,?,?)")
                .setBatchSize(1)
                .build();

        sink.emitDataStream(ds);

        env.execute("IdleKafka");
    }

    private static StreamExecutionEnvironment createEnv(boolean idleCheck) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.enableCheckpointing(1000, CheckpointingMode.AT_LEAST_ONCE);
        if (idleCheck) {
            Map<String, String> args = new HashMap<>();
            args.put("source.idle.timeout.ms", "5000");
            env.getConfig().setGlobalJobParameters(ParameterTool.fromMap(args));
        }
        env.setParallelism(2);
        return env;
    }
}
