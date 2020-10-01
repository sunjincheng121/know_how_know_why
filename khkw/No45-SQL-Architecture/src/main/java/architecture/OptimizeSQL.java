package architecture;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - architecture
 * 功能描述: 从Kafka读取消息数据，并在控制台进行打印。
 * 操作步骤: 1. 直接执行作业，看到输出
 * 2. 调试查看sql的plan
 * 3. 断点，查看codegen 代码
 * 作者： 孙金城
 * 日期： 2020/10/1
 */
public class OptimizeSQL {
    public static void main(String[] args) throws Exception {
        // DataGen
        String sourceDDL = "CREATE TABLE t1 (\n" +
                " id STRING , \n" +
                " value1 INT \n" +
                ") WITH (\n" +
                " 'connector' = 'datagen' ,\n" +
                " 'fields.name.length'='10'\n" +
                ")";

        // DataGen
        String sourceDDL2 = "CREATE TABLE t2 (\n" +
                " id STRING , \n" +
                " value2 INT \n" +
                ") WITH (\n" +
                " 'connector' = 'datagen' ,\n" +
                " 'fields.name.length'='10'\n" +
                ")";

        // Print
        String sinkDDL = "CREATE TABLE rst (\n" +
                " id STRING , \n" +
                " value2 INT  \n" +
                ") WITH (\n" +
                " 'connector' = 'print'\n" +
                ")";


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
        tEnv.executeSql(sourceDDL2);
        tEnv.executeSql(sinkDDL);

        String sql = "INSERT INTO rst \n" +
                "SELECT t1.id, 1 + 2 + t1.value1 AS v \n" +
                "FROM t1 JOIN t2 \n" +
                "ON t1.id = t2.id AND t2.id < 1000";

        String plan = tEnv.explainSql(sql);
        System.out.println(plan);

        tEnv.executeSql(sql);
    }
}