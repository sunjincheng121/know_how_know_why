package prometheus;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.concurrent.TimeUnit;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.call;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - prometheus
 * 功能描述: Flink集成Prometheus
 * 操作步骤:
 * 1. 直接运行程序，控制台能正常输出即可。
 * 2. 打开pom中scope的注释
 * 3. 运行 mvn clean package -DskipTests
 * 4. cp /Users/jincheng.sunjc/work/know_how_know_why/khkw/flink-prometheus/target/flink-prometheus-0.1.jar ~/flinkDeploy/
 * 5. cp -rf /Users/jincheng.sunjc/work/know_how_know_why/khkw/flink-prometheus/prometheus ~/flinkDeploy/
 * 6. build docker (省略，我已经为大家build好了，大家可以跳过）
 *    docker build -t khkw/flink_prometheus:1.11.2 .
 *    上传 docker push khkw/flink_prometheus:1.11.2
 * 7. 运行容器 docker-compose up -d
 * 8. 一切正常的化，查看：
 *  Flink：http://localhost:4000/#/overview
 *  Prometheus: http://localhost:9090/
 *  grafana: http://localhost:3000/ 用户名 admin 密码 flink
 *  9. 提交作业
 *   docker exec -it jobmanager /bin/bash
 *   bin/flink run /opt/flinkDeploy/flink-prometheus-0.1.jar -d
 *  10.如果一切正常查看print数据
 *  docker logs --since 30m 《CONTAINER—ID》
 *  11.查看和配置Prometheus&grafana ：）
 *
 *
 *
 * <p>
 * 作者： 孙金城
 * 日期： 2020/9/19
 */
public class Demo {
    public static void main(String[] args) throws Exception {
        // datagen
        String sourceDDL = "CREATE TABLE gen_source (\n" +
                " event INT \n" +
                " ) WITH ( \n" +
                " 'connector' = 'datagen'\n" +
                " )";

        // Mysql
        String sinkDDL = "CREATE TABLE print_sink (\n" +
                " event INT \n" +
                " ) WITH (\n" +
                " 'connector' = 'print'\n" +
                " )";

        // 创建执行环境
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();

        sEnv.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(3, Time.of(1, TimeUnit.SECONDS) ));

        sEnv.enableCheckpointing(1000);

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(sEnv, settings);

        //注册source和sink
        tEnv.executeSql(sourceDDL);
        tEnv.executeSql(sinkDDL);

        tEnv.createFunction("myudf", MyUDFWithMetric.class);

        //数据提取
        Table sourceTab = tEnv.from("gen_source");
        //这里我们暂时先使用 标注了 deprecated 的API, 因为新的异步提交测试有待改进...
        sourceTab.select(call("myudf", $("event"))).insertInto("print_sink");
        //执行作业
        tEnv.execute("flink-prometheus");
    }
}
