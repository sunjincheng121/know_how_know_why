package sql;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - sql
 * 功能描述: 从Kafka读取消息数据，并在控制台进行打印。
 * 操作步骤:
 * 1. 先参考 订阅号文章 《Apache Flink 漫谈系列 - 搭建Flink 1.11 版本 Table API/SQL开发环境(需求驱动)》
 * https://mp.weixin.qq.com/s/Az8gqduAaQO-AD_MQaur7w 安装好Docker和Kafka环境，并创建 cdn-log Topic。
 * 2. 启动作业，并参考文章说明，向Topic里面发送一些测试数据，观看控制台打印的消息。
 * <p>
 * 作者： 孙金城
 * 日期： 2020/8/2
 */
public class Kafka2Print {
    public static void main(String[] args) throws Exception {
        // Kafka
        String sourceDDL = "CREATE TABLE kafka_source (\n" +
                " log_msg STRING\n" +
                ") WITH (\n" +
                " 'connector' = 'kafka-0.11',\n" +
                " 'topic' = 'cdn-log',\n" +
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