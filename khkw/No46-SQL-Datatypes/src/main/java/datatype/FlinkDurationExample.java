package datatype;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.ZoneOffset;


/**
 * 项目名称: Apache Flink 知其然，知其所以然 - datatype
 * 功能描述: Flink SQL INTERVAL
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2020/10/11
 */
public class FlinkDurationExample {
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        String sourceDDL = "CREATE TABLE datagen_source (\n" +
                " hiredate TIMESTAMP,\n" +
                " ts BIGINT \n" +
                ") WITH (\n" +
                " 'connector' = 'filesystem' ,\n" +
                " 'path' = 'file:///Users/jincheng.sunjc/work/know_how_know_why/khkw/No46-SQL-Datatypes/src/main/resources/tsdata.csv',\n" +
                " 'format' = 'csv' \n" +
                ")";

        String sinkDDL = "CREATE TABLE print_sink (\n" +
                " seniority TIMESTAMP, \n" +
                " ts TIMESTAMP \n" +
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
        tEnv.getConfig().setLocalTimeZone(ZoneOffset.ofHours(0));

        //注册source和sink
        tEnv.executeSql(sourceDDL);
        tEnv.executeSql(sinkDDL);

        // SQL逻辑
        String querySql = "SELECT hiredate  + INTERVAL '999' DAY(3), TO_TIMESTAMP(FROM_UNIXTIME(ts / 1000)) FROM datagen_source ";

        String sql = "INSERT INTO print_sink " + querySql;
        tEnv.executeSql(sql);
    }
}
