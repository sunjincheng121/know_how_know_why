package datatype;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - datatype.constructured
 * 功能描述: 测试 Constructured DataType
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2021/06/12
 */
public class JSONConstructured {
    public static void main(String[] args) {
        String sourceDDL = "CREATE TABLE json_source (\n" +
                " ts BIGINT,\n" +
                " name VARCHAR, \n" +
                " b_date DATE, \n" +
                " tag BOOLEAN, \n" +
                " strings ARRAY<VARCHAR>, \n" +
                " obj_map MAP<VARCHAR,VARCHAR>, \n" +
                " obj_set MULTISET<VARCHAR>, \n" +
                " obj_row ROW<booleanField BOOLEAN, intField INT> \n" +
                ") WITH (\n" +
                " 'connector' = 'filesystem' ,\n" +
                " 'path' = 'file:///Users/jincheng/work/know_how_know_why/khkw/No46-SQL-Datatypes-Constructured/src/main/resources/mydata.jsonl',\n" +
                " 'format' = 'json' ,\n" +
                "  'json.timestamp-format.standard' = 'SQL' , \n" + // https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/connectors/table/formats/json/
                "  'json.ignore-parse-errors' = 'false' \n" +
                ")";

        String sinkDDL = "CREATE TABLE print_sink (\n" +
                " ts BIGINT,\n" +
                " name VARCHAR, \n" +
                " b_date DATE, \n" +
                " tag BOOLEAN, \n" +
                " strings ARRAY<VARCHAR>, \n" +
                " obj_map MAP<VARCHAR,VARCHAR>, \n" +
                " obj_set MULTISET<VARCHAR>, \n" +
                " obj_row ROW<booleanField BOOLEAN, intField INT> \n" +
                ") WITH (\n" +
                " 'connector' = 'print'\n" +
                ")";

        // 创建执行环境
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(sEnv, settings);

        //注册source和sink
        tEnv.executeSql(sourceDDL);
        tEnv.executeSql(sinkDDL);

        // SQL逻辑
        String sql = "INSERT INTO print_sink SELECT * FROM json_source ";
        tEnv.executeSql(sql);

    }
}

