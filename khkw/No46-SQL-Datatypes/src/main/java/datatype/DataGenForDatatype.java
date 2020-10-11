package datatype;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - datatype
 * 功能描述: 用于测试SQL DataTypes
 * 操作步骤: 1.  直接运行程序，正常输出
 * 2. 将 TUMBLE_START(ts 和 TUMBLE(ts 中的ts变成 myts 查看报错信息。
 * <p>
 * 作者： 孙金城
 * 日期： 2020/10/11
 */
public class DataGenForDatatype {
    public static void main(String[] args) {
        String sourceDDL = "CREATE TABLE datagen_source (\n" +
                " f_int INT,\n" +
                " f_float FLOAT,\n" +
                " f_str STRING,\n" +
                " myts AS localtimestamp,\n" +
                " ts AS localtimestamp,\n" +
                " WATERMARK FOR ts AS ts - INTERVAL '5' SECOND" +
                ") WITH (\n" +
                " 'connector' = 'datagen',\n" +
                " 'fields.f_int.min'='1',\n" +
                " 'fields.f_int.max'='10',\n" +
                " 'fields.f_float.min'='1.0',\n" +
                " 'fields.f_float.max'='1000.0',\n" +
                " 'fields.f_random_str.length'='10' \n" +
                ")";

        String sinkDDL = "CREATE TABLE print_sink (\n" +
                " f_int BIGINT,\n" +
                " f_float FLOAT,\n" +
                " f_str BIGINT, \n" +
                " ts TIMESTAMP(3) \n" +
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
        sEnv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(sEnv, settings);
//        tEnv.getConfig().getConfiguration().setString("pipeline.time-characteristic","ProcessingTime");

        //注册source和sink
        tEnv.executeSql(sourceDDL);
        tEnv.executeSql(sinkDDL);

        // SQL逻辑
        String querySql = "SELECT " +
                "SUM(f_int), " +
                "SUM(f_float), " +
                "COUNT(f_str), " +
                "TUMBLE_START(ts, INTERVAL '5' SECOND) " +
                "FROM datagen_source " +
                "GROUP BY TUMBLE(ts, INTERVAL '5' SECOND)";

       String sql = "INSERT INTO print_sink " + querySql ;
       tEnv.executeSql(sql);
    }
}
