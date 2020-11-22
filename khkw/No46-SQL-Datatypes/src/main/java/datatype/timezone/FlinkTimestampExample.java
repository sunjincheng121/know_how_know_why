package datatype.timezone;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.ZoneOffset;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - datatype.timezone
 * 功能描述: 体验Flink SQL 对timestamp的使用和WWATERMARK的设置
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2020/11/22
 */
public class FlinkTimestampExample {
    public static void main(String[] args) throws Exception {
        String sourceDDL = "CREATE TABLE cvs_source (\n" +
                " ts TIMESTAMP(3),\n" +
                " ts_v BIGINT, \n" +
                " WATERMARK FOR ts AS ts - INTERVAL '5' SECOND\n" +
                ") WITH (\n" +
                " 'connector' = 'filesystem' ,\n" +
                " 'path' = 'file:///Users/jincheng.sunjc/work/know_how_know_why/khkw/No46-SQL-Datatypes/src/main/resources/tsdata2.csv',\n" +
                " 'format' = 'csv' \n" +
                ")";

        String sinkDDL = "CREATE TABLE print_sink (\n" +
                " ts BIGINT,\n" +
                " c_v BIGINT,\n" +
                " s_ts TIMESTAMP(3), \n" +
                " e_ts TIMESTAMP(3) \n" +
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
        String querySql = "SELECT ts_v ," +
                " COUNT(1), " +
                "TUMBLE_START(ts, INTERVAL '15' MINUTE) AS s, " +
                "TUMBLE_END(ts, INTERVAL '15' MINUTE) AS e " +
                "FROM cvs_source " +
                "GROUP BY ts_v, TUMBLE(ts, INTERVAL '15' MINUTE)";

        String sql = "INSERT INTO print_sink " + querySql;
        tEnv.executeSql(sql);
    }
}
