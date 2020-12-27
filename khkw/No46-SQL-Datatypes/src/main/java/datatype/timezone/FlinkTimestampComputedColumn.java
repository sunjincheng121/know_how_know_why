package datatype.timezone;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.ZoneOffset;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - datatype.timezone
 * 功能描述: 本节核心说一下如果转化外部无法直接提供timestamp的时候，如果利用计算列创建timestamp
 * <p>
操作步骤:
 * 1. 下载oracle docker镜像 docker pull registry.cn-hangzhou.aliyuncs.com/helowin/oracle_11g
 * 2. 下载利用 docker images 查看oracle的镜像的repository，便于创建容器。
 * registry.cn-hangzhou.aliyuncs.com/helowin/oracle_11g   latest              3fa112fd3642        4 years ago         6.85GB
 * 3. 创建容器 - docker run -d -p 1521:1521 --name myOracle registry.cn-hangzhou.aliyuncs.com/helowin/oracle_11g
 * 4. 进入容器镜像 -  docker exec -it myOracle bash
 * 5. 编辑profile文件，su root 密码 helowin , vi /etc/profile
 *      export ORACLE_HOME=/home/oracle/app/oracle/product/11.2.0/dbhome_2
 *      export ORACLE_SID=helowin
 *      export PATH=$ORACLE_HOME/bin:$PATH
 *   保存之后，  source /etc/profile
 *  6. 创建软链 - ln -s $ORACLE_HOME/bin/sqlplus /usr/bin
 *  7. 回到 oracle用户， su oracle
 *  8. 登陆：
 *   a. sqlplus /nolog
 *   b. conn /as sysdba
 *  9. sql plus 的使用参考 https://www.oracle.com/database/technologies/appdev/sqldeveloper-landing.html
 *  10. 查看一下所有数据库信息 select  *  from  v$instance;
 *  11. 创建测试表： create table stu( stuno number(4) not null) 如果一切正常，我们就可以开始今天的Declaration测试。
 *  12. 创建 时间属性的表：create table flink_ts(tme date,ts timestamp(3), ts_tz timestamp(3) with time zone, ts_tzl timestamp(3) with local time zone);
 *  13. 插入数据：insert into flink_ts values(sysdate,sysdate,sysdate,sysdate);
 *  14. 查询数据：select * from flink_ts;
 *  15. 我们改变一下时区： alter session set time_zone='+2:00';
 *  16. 查看一下： select dbtimezone, sessiontimezone from dual;
 *  语义：
 *  timestamp with time zone 表达的是 针对 标准时区(格林尼治时间)的差。
 *  timestamp with local time zone 表达的是 ，根据你当前所处时区，变化你和格林尼治时间的差。
 *
 *  17. Flink 中的问题...
 *
 * 作者： 孙金城
 * 日期： 2020/12/27
 */
public class FlinkTimestampComputedColumn {
    public static void main(String[] args) throws Exception {
        String sourceDDL = "CREATE TABLE cvs_source (\n" +
                " ts TIMESTAMP(3),\n" +
                " ts_v BIGINT, \n" +
                " ts_v2 as  ts_v * 2, \n" +
                " ts2 AS TO_TIMESTAMP(FROM_UNIXTIME(ts_v / 1000, 'yyyy-MM-dd HH:mm:ss')), \n" +
                " WATERMARK FOR ts2 AS ts2 - INTERVAL '5' SECOND\n" +
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
        String querySql = "SELECT ts_v2 ," +
                " COUNT(1), " +
                "TUMBLE_START(ts2, INTERVAL '15' MINUTE) AS s, " +
                "TUMBLE_END(ts2, INTERVAL '15' MINUTE) AS e " +
                "FROM cvs_source " +
                "GROUP BY ts_v2, TUMBLE(ts2, INTERVAL '15' MINUTE)";

        String sql = "INSERT INTO print_sink " + querySql;
        tEnv.executeSql(sql);
    }
}
