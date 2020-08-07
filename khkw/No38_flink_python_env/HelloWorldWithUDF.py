from pyflink.datastream import StreamExecutionEnvironment
from pyflink.table import EnvironmentSettings, StreamTableEnvironment, DataTypes
from pyflink.table.udf import udf

import logging

@udf(input_types=[DataTypes.STRING()], result_type=DataTypes.STRING())
def pass_by(str):
    logging.error("Some debugging infomation...")
    return "pass_by_" + str

def hello_world():
    """
    从随机Source读取数据，然后直接利用PrintSink输出。
    """
    settings = EnvironmentSettings.new_instance().in_streaming_mode().use_blink_planner().build()
    env = StreamExecutionEnvironment.get_execution_environment()
    t_env = StreamTableEnvironment.create(stream_execution_environment=env, environment_settings=settings)
    t_env.get_config().get_configuration().set_boolean("python.fn-execution.memory.managed", True)

    source_ddl = """
                    CREATE TABLE random_source (
                        f_sequence INT,
                        f_random INT,
                        f_random_str STRING
                    ) WITH (
                        'connector' = 'datagen',
                        'rows-per-second'='5',
                        'fields.f_sequence.kind'='sequence',
                        'fields.f_sequence.start'='1',
                        'fields.f_sequence.end'='1000',
                        'fields.f_random.min'='1',
                        'fields.f_random.max'='1000',
                        'fields.f_random_str.length'='10'
                    )
                    """

    sink_ddl = """
                  CREATE TABLE print_sink (
                    f_sequence INT,
                    f_random INT,
                    f_random_str STRING 
                ) WITH (
                  'connector' = 'print'
                )
        """

    # 注册source和sink
    t_env.execute_sql(source_ddl)
    t_env.execute_sql(sink_ddl)

    # 注册 UDF
    t_env.register_function('pass_by', pass_by)

    # 数据提取
    tab = t_env.from_path("random_source")
    # 这里我们暂时先使用 标注了 deprecated 的API, 因为新的异步提交测试有待改进...
    tab.select("f_sequence, f_random, pass_by(f_random_str) ").insert_into("print_sink")
    # 执行作业
    t_env.execute("Flink Hello World")

if __name__ == '__main__':
    hello_world()
