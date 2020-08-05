from pyflink.datastream import StreamExecutionEnvironment
from pyflink.table import EnvironmentSettings, StreamTableEnvironment, DataTypes

def kafka_to_mysql():
    """
    从Kafka Source读取Json数据，然后导入到Mysql。{"msg": "welcome flink users..."}
    """
    settings = EnvironmentSettings.new_instance().in_streaming_mode().use_blink_planner().build()
    env = StreamExecutionEnvironment.get_execution_environment()
    t_env = StreamTableEnvironment.create(stream_execution_environment=env, environment_settings=settings)
    t_env.get_config().get_configuration().set_boolean("python.fn-execution.memory.managed", True)

    #JARS_DIR=/Users/jincheng.sunjc/work/PlaygroundEnv/myJars/
    #wget -P ${JARS_DIR} https://repo.maven.apache.org/maven2/org/apache/flink/flink-json/1.11.1/flink-json-1.11.1.jar; \
    #wget -P ${JARS_DIR} https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka-0.11_2.11/1.11.1/flink-sql-connector-kafka-0.11_2.11-1.11.1.jar; \
    #wget -P ${JARS_DIR} https://repo.maven.apache.org/maven2/org/apache/flink/flink-connector-jdbc_2.11/1.11.1/flink-connector-jdbc_2.11-1.11.1.jar; \
    #wget -P ${JARS_DIR} https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/5.1.40/mysql-connector-java-5.1.40.jar; \
    #wget -P ${JARS_DIR} https://repo.maven.apache.org/maven2/org/apache/flink/flink-table-common/1.11.1/flink-table-common-1.11.1.jar ; \

    # 添加依赖
    base_dir = "file:///Users/jincheng.sunjc/work/PlaygroundEnv/myJars/"

    kafka_jar = f"{base_dir}flink-sql-connector-kafka-0.11_2.11-1.11.1.jar"
    jdbc_jar = f"{base_dir}flink-connector-jdbc_2.11-1.11.1.jar"
    mysql_jar = f"{base_dir}mysql-connector-java-5.1.40.jar"
    json_format_jar = f"{base_dir}flink-json-1.11.1.jar"
    table_common_jar = f"{base_dir}flink-table-common-1.11.1.jar"

    jar_seq = [kafka_jar, jdbc_jar, mysql_jar, json_format_jar, table_common_jar]
    jars = ";".join(jar_seq)

    t_env.get_config().get_configuration().set_string("pipeline.jars",jars)

    source_ddl = """
                    CREATE TABLE kafka_source (
                        msg STRING
                    ) WITH (
                        'connector' = 'kafka-0.11',
                        'topic' = 'cdn-log',
                        'properties.bootstrap.servers' = 'localhost:9092',
                        'format' = 'json',
                        'scan.startup.mode' = 'latest-offset'
                    )
                    """

    sink_ddl = """
                  CREATE TABLE mysql_sink (
                    msg STRING 
                ) WITH (
                   'connector' = 'jdbc',
                   'url' = 'jdbc:mysql://localhost:3306/flinkdb?characterEncoding=utf-8&useSSL=false',
                   'table-name' = 'cdn_log',
                   'username' = 'root',
                   'password' = '123456',
                   'sink.buffer-flush.max-rows' = '1'
                )
        """

    # 注册source和sink
    t_env.execute_sql(source_ddl)
    t_env.execute_sql(sink_ddl)

    # 数据提取
    tab = t_env.from_path("kafka_source")
    # 这里我们暂时先使用 标注了 deprecated 的API, 因为新的异步提交测试有待改进...
    tab.insert_into("mysql_sink")
    # 执行作业
    t_env.execute("kafka_to_mysql")

if __name__ == '__main__':
    kafka_to_mysql()
