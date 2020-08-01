package sql;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.concurrent.TimeUnit;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - sql
 * 功能描述: 在SQL程序中设置 启动策略，开启Checkpoint，配置Statebackend等。
 * 操作步骤:
 * 1. 完成 Kafka2Mysql 操作之后，故意输入一条 异常数据，比如：xxxxxx, 程序会退出.
 * 2. 打开 sEnv.setRestartStrategy 的注释，再次启动作业，输入xxxx，之后在输入正常的消息，作业不会退出，
 * 查询mysql结果表，数据还能正常写入。
 * 3. 当然，也可以设置 enableCheckpointing和setStateBackend，感兴趣同学可以自行体验一下。
 * <p>
 * 作者： 孙金城
 * 日期： 2020/8/2
 */
public class WithExecutionEnvironment {
    public static void main(String[] args) throws Exception {
        // Kafka {"msg": "welcome flink users..."}
        String sourceDDL = "CREATE TABLE kafka_source (\n" +
                " msg STRING\n" +
                ") WITH (\n" +
                " 'connector' = 'kafka-0.11',\n" +
                " 'topic' = 'cdn-log',\n" +
                " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
                " 'format' = 'json',\n" +
                " 'scan.startup.mode' = 'latest-offset'\n" +
                ")";

        // Mysql
        String sinkDDL = "CREATE TABLE mysql_sink (\n" +
                " msg STRING \n" +
                ") WITH (\n" +
                "  'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://localhost:3306/flinkdb?characterEncoding=utf-8&useSSL=false',\n" +
                "   'table-name' = 'cdn_log',\n" +
                "   'username' = 'root',\n" +
                "   'password' = '123456',\n" +
                "   'sink.buffer-flush.max-rows' = '1',\n" +
                "   'sink.buffer-flush.interval' = '1s'\n" +
                ")";

        // 创建执行环境
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();

//        sEnv.setRestartStrategy(
//                RestartStrategies.fixedDelayRestart(3, Time.of(1, TimeUnit.SECONDS) ));
//
//        sEnv.enableCheckpointing(1000);
//        sEnv.setStateBackend(new FsStateBackend("file:///tmp/chkdir", false));

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(sEnv, settings);

        //注册source和sink
        tEnv.executeSql(sourceDDL);
        tEnv.executeSql(sinkDDL);

        //数据提取
        Table sourceTab = tEnv.from("kafka_source");
        //这里我们暂时先使用 标注了 deprecated 的API, 因为新的异步提交测试有待改进...
        sourceTab.insertInto("mysql_sink");
        //执行作业
        tEnv.execute("Flink Hello World");
    }
}
