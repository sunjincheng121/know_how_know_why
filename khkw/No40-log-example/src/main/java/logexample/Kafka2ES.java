package logexample;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - logexample
 * 功能描述: 应用了SQL/Table API 统计移动端和PC网页端以1分钟为窗口的实时成交总额，并将统计结果发往ElasticSearch存储。
 * 操作步骤:
 * 1. 启动所有环境 docker-compose up -d
 * 2. 查看kibana ui界面，http://localhost:5601/
 * 3. 打包项目，将 pom的<scope>provided</scope>注释打开，执行 mvn clean package -DskipTests
 * 4. Copy 业务JRA到 映射目录：
 * cp /Users/jincheng/work/know_how_know_why/khkw/No40-log-example/target/No40-log-example-0.1.jar ~/flinkDeploy
 * 5. 部署作业
 *
 * <p>
 * 作者： 孙金城
 * 日期： 2020/8/13
 */
public class Kafka2ES {
    public static void main(String[] args) throws Exception {

        String sourceDDL = "CREATE TABLE payment_msg(\n" +
                "                createTime VARCHAR,\n" +
                "                rt as TO_TIMESTAMP(createTime),\n" +
                "                orderId BIGINT,\n" +
                "                payAmount DOUBLE,\n" +
                "                payPlatform INT,\n" +
                "                provinceId INT,\n" +
                "                WATERMARK FOR rt as rt - INTERVAL '2' SECOND\n" +
                "            ) WITH (\n" +
                "              'connector' = 'kafka-0.11',\n" +
                "              'topic' = 'payment_msg',\n" +
                "              'properties.bootstrap.servers' = 'kafka:9092',\n" +
                "              'scan.startup.mode' = 'latest-offset',\n" +
                "              'format' = 'json'\n" +
                "            )";

        String sinkDDL = "CREATE TABLE es_sink (\n" +
                "            province VARCHAR,\n" +
                "            pay_amount DOUBLE,\n" +
                "            rowtime TIMESTAMP(3)\n" +
                "            ) with (\n" +
                "                'connector.type' = 'elasticsearch',\n" +
                "                'connector.version' = '7',\n" +
                "                'connector.hosts' = 'http://elasticsearch:9200',\n" +
                "                'connector.index' = 'platform_pay_amount_1',\n" +
                "                'connector.document-type' = 'payment',\n" +
                "                'update-mode' = 'append',\n" +
                "                'connector.flush-on-checkpoint' = 'true',\n" +
                "                'connector.key-delimiter' = '$',\n" +
                "                'connector.key-null-literal' = 'n/a',\n" +
                "                'connector.bulk-flush.max-size' = '42mb',\n" +
                "                'connector.bulk-flush.max-actions' = '32',\n" +
                "                'connector.bulk-flush.interval' = '1000',\n" +
                "                'connector.bulk-flush.backoff.delay' = '1000',\n" +
                "                'format.type' = 'json'\n" +
                "            )";

        // 创建执行环境
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(sEnv, settings);

        //注册source和sink
        tEnv.executeSql(sourceDDL);
        tEnv.executeSql(sinkDDL);

        tEnv.createFunction("provinceIdToName", ProvinceIdToName.class);

        String query = " SELECT " +
                "  provinceIdToName(provinceId) as province, " +
                "  sum(payAmount) as pay_amount, " +
                "  tumble_start(rt, interval '5' seconds) as rowtime " +
                "FROM payment_msg " +
                "GROUP BY tumble(rt, interval '5' seconds), provinceId";
        //这里我们暂时先使用 标注了 deprecated 的API, 因为新的异步提交测试有待改进...
        tEnv.sqlQuery(query).insertInto("es_sink");
        tEnv.execute("统计移动端和PC网页端-时间窗口实时成交总额");
    }
}

