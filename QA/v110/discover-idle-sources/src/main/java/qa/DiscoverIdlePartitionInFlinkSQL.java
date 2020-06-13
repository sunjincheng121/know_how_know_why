package qa;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Rowtime;
import org.apache.flink.table.descriptors.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Test for discover idle partition.
 * Run this class first then run the ProducerTestData.java
 */
public class DiscoverIdlePartitionInFlinkSQL {
    public static void main(String[] args) throws Exception {
        // with idle check， 7 output
        StreamExecutionEnvironment env = createEnv(true);
        // without idle check, 4 output
//        StreamExecutionEnvironment env = createEnv(false);

        // Using Flink Planner(old)
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        tEnv.connect(new Kafka().version("0.10")
                .topic("checkIdle")
                .property("zookeeper.connect", "localhost:2181")
                .property("bootstrap.servers", "localhost:9092")
                .property("group.id", "data_Group")
                .property("connector.startup-mode", "earliest-offset")
                .startFromLatest())
                .withFormat(new Json().failOnMissingField(true)
                        .schema(Types.ROW_NAMED(
                                new String[]{"id", "cnt", "ts"},
                                Types.STRING,
                                Types.INT,
                                Types.SQL_TIMESTAMP)))
                .withSchema(new Schema()
                        .field("rowtime", DataTypes.TIMESTAMP(3))
                        .rowtime(new Rowtime()
                                .timestampsFromField("ts")
                                .watermarksPeriodicBounded(1000))
                        .field("id", DataTypes.STRING())
                        .field("cnt", DataTypes.INT()))
                .inAppendMode()
//                .createTemporaryTable("kafkaSource") but FLINK-16160
                .registerTableSource("kafkaSource");

        String sinkDDL = "CREATE TABLE jdbcSink (" +
                "  id VARCHAR," +
                "  win_start TIMESTAMP(3)," +
                "  cnt BIGINT" +
                ") WITH (" +
                "'connector.type' = 'jdbc'," +
                "'connector.url' = 'jdbc:mysql://localhost/flink_db?useUnicode=true&characterEncoding=utf-8&useSSL=false'," +
                "'connector.table' = 'tab_string_long'," +
                "'connector.driver' = 'com.mysql.jdbc.Driver'," +
                "'connector.username' = 'root'," +
                "'connector.password' = '123456'," +
                "'connector.write.flush.interval' = '1s'" +
                ")";

        tEnv.sqlUpdate(sinkDDL);

        String sql = "INSERT INTO jdbcSink " +
                "SELECT id, TUMBLE_START(rowtime, INTERVAL '5' SECOND), count(cnt) as cnt " +
                "FROM kafkaSource " +
                "GROUP BY id, TUMBLE(rowtime, INTERVAL '5' SECOND)";

        tEnv.sqlUpdate(sql);

        env.execute("IdleKafka");
    }

    private static StreamExecutionEnvironment createEnv(boolean idleCheck) throws Exception {
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

    /**
     * Print the data to csv file which ProducerTestData.java generate.
     */
    private static void print(StreamTableEnvironment tEnv) throws Exception {
        String rsFile = "/Users/jincheng.sunjc/work/know_how_know_why/QA/v110/discover-idle-sources/src/main/java/qa/rs.csv";
        String printSink = "CREATE TABLE printSink (" +
                "  id VARCHAR," +
                "  cnt INT, " +
                "  ts TIMESTAMP(3)" +
                ") WITH (" +
                "'connector.type' = 'filesystem'," +
                "'connector.path' = '" + rsFile + "'," +
                "'format.type' = 'csv'" +
                ")";

        tEnv.sqlUpdate(printSink);

        String sql = "INSERT INTO printSink " +
                "SELECT id, cnt, ts" +
                " FROM kafkaSource ";
        tEnv.sqlUpdate(sql);
    }
}
