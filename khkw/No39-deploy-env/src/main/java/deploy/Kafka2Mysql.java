package deploy;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - deploy
 * 功能描述: No37的作业Copy过来
 * 操作步骤:
 * 1. 打包 mvn clean package -DskipTest
 * 2. 查看JAR包内容，只包含业务代码: jar -tvf /Users/jincheng/work/know_how_know_why/khkw/No39-deploy-env/target/No39-deploy-env-0.1.jar
 * 3. Copy JAR 到 配置的部署目录：  cp /Users/jincheng/work/know_how_know_why/khkw/No39-deploy-env/target/No39-deploy-env-0.1.jar ~/flinkDeploy/
 * 4. 启动 docker-compose up -d
 * 5. 初始化 topic/mysql 数据表
 * 6. bin/flink run /opt/flinkDeploy/No39-deploy-env-0.1.jar -d
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
                " 'properties.bootstrap.servers' = 'kafka:9092',\n" +
                " 'format' = 'json',\n" +
                " 'scan.startup.mode' = 'latest-offset'\n" +
                ")";

        // Mysql
        String sinkDDL = "CREATE TABLE mysql_sink (\n" +
                " msg STRING \n" +
                ") WITH (\n" +
                "  'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://mysql:3306/flinkdb?characterEncoding=utf-8&useSSL=false',\n" +
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