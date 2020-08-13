package logexample;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - sql
 * 功能描述: 首先操作 No40-Business-data ， 然后从Kafka读取消息数据，并在控制台进行打印。
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2020/8/13
 */
public class Kafka2Print {
    public static void main(String[] args) throws Exception {
        // Kafka
        String sourceDDL = "CREATE TABLE kafka_source (\n" +
                " log_msg STRING\n" +
                ") WITH (\n" +
                " 'connector' = 'kafka-0.11',\n" +
                " 'topic' = 'payment_msg',\n" +
                " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
                " 'format' = 'json',\n" +
                " 'scan.startup.mode' = 'latest-offset'\n" +
                ")";

        // Mysql
        String sinkDDL = "CREATE TABLE print_sink (\n" +
                " f_random_str STRING \n" +
                ") WITH (\n" +
                " 'connector' = 'print'\n" +
                ")";

        // 创建执行环境
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        TableEnvironment tEnv = TableEnvironment.create(settings);

        //注册source和sink
        tEnv.executeSql(sourceDDL);
        tEnv.executeSql(sinkDDL);

        //数据提取
        Table sourceTab = tEnv.from("kafka_source");
        //这里我们暂时先使用 标注了 deprecated 的API, 因为新的异步提交测试有待改进...
        sourceTab.insertInto("print_sink");
        //执行作业
        tEnv.execute("Flink Hello World");
    }
}