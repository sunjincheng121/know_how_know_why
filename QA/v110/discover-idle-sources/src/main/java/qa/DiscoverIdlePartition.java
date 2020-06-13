package qa;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * Test for discover idle partition.
 */
public class DiscoverIdlePartition {
    public static void main(String[] args) throws Exception {
        // with idle check， 7 output
//        StreamExecutionEnvironment env = createEnv(true);
        // without idle check, 4 output
        StreamExecutionEnvironment env = createEnv(false);


        // using blink blanner due to https://issues.apache.org/jira/browse/FLINK-16693
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        String sourceDDL = "CREATE TABLE kafkaSource (" +
                "  id VARCHAR," +
                "  cnt INT, " +
                "  ts TIMESTAMP(3)," +
                "  WATERMARK FOR ts AS ts - INTERVAL '0' SECOND" +
                ") WITH (" +
                "'connector.type' = 'kafka'," +
                "'connector.version' = '0.10'," +
                "'connector.topic' = 'checkIdle'," +
                "'connector.properties.bootstrap.servers' = 'localhost:9092'," +
                "'connector.properties.group.id' = 'data_Group'," +
                "'connector.startup-mode' = 'latest-offset'," +
//                "'connector.startup-mode' = 'earliest-offset'," +
                "'format.type' = 'json')";
        tEnv.sqlUpdate(sourceDDL);

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
                "SELECT id, TUMBLE_START(ts, INTERVAL '5' SECOND), count(cnt) as cnt " +
                "FROM kafkaSource " +
                "GROUP BY id, TUMBLE(ts, INTERVAL '5' SECOND)";

        tEnv.sqlUpdate(sql);

        env.execute("IdleKafka");
    }

    private static StreamExecutionEnvironment createEnv(boolean idleCheck) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.enableCheckpointing(1000, CheckpointingMode.AT_LEAST_ONCE);
        if(idleCheck) {
            Map<String, String> args = new HashMap<>();
            args.put("source.idle.timeout.ms", "5000");
            env.getConfig().setGlobalJobParameters(ParameterTool.fromMap(args));
        }
        env.setParallelism(2);
        return env;
    }

    private static void print(StreamTableEnvironment tEnv) throws Exception{
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
