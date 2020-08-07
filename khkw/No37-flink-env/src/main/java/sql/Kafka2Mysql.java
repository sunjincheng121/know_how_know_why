package sql;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - sql
 * 功能描述: 从Kafka读取消息数据，并在控制台进行打印。
 * 操作步骤:
 * 1. 先完成 Kafka2Print 的操作。
 * 2. 参考 订阅号文章 《Apache Flink 漫谈系列 - 搭建Flink 1.11 版本 Table API/SQL开发环境(需求驱动)》
 * https://mp.weixin.qq.com/s/Az8gqduAaQO-AD_MQaur7w 安装好MySQL环境，并创建 cdn_log 表。
 * 3. 启动作业，并参考文章说明，向Topic里面发送一些测试数据，然后在MySQL的shell命令行查询结果表。
 * <p>
 * 作者： 孙金城
 * 日期： 2020/8/2
 */
public class Kafka2Mysql {
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
                "   'sink.buffer-flush.max-rows' = '1'\n" +
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
        sourceTab.insertInto("mysql_sink");
        //执行作业
        tEnv.execute("Flink Hello World");
    }
}