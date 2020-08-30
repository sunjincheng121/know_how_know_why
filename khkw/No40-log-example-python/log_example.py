from pyflink.datastream import StreamExecutionEnvironment, TimeCharacteristic
from pyflink.table import StreamTableEnvironment, DataTypes, EnvironmentSettings
from pyflink.table.udf import udf


provinces = ("北京", "上海", "杭州", "深圳", "江西", "重庆", "西藏")


@udf(input_types=[DataTypes.STRING()], result_type=DataTypes.STRING())
def province_id_to_name(id):
    return provinces[id]


def log_processing():
    env = StreamExecutionEnvironment.get_execution_environment()
    env.set_stream_time_characteristic(TimeCharacteristic.EventTime)
    env_settings = EnvironmentSettings.Builder().use_blink_planner().build()
    t_env = StreamTableEnvironment.create(stream_execution_environment=env, environment_settings=env_settings)
    t_env.get_config().get_configuration().set_boolean("python.fn-execution.memory.managed", True)

    source_ddl = """
            CREATE TABLE payment_msg(
                createTime VARCHAR,
                rt as TO_TIMESTAMP(createTime),
                orderId BIGINT,
                payAmount DOUBLE,
                payPlatform INT,
                provinceId INT,
                WATERMARK FOR rt as rt - INTERVAL '2' SECOND
            ) WITH (
              'connector' = 'kafka-0.11',
              'topic' = 'payment_msg',
              'properties.bootstrap.servers' = 'kafka:9092',
              'scan.startup.mode' = 'latest-offset',
              'format' = 'json'
            )
            """

    es_sink_ddl = """
            CREATE TABLE es_sink (
            province VARCHAR,
            pay_amount DOUBLE,
            rowtime TIMESTAMP(3)
            ) with (
                'connector.type' = 'elasticsearch',
                'connector.version' = '7',
                'connector.hosts' = 'http://elasticsearch:9200',
                'connector.index' = 'platform_pay_amount_1',
                'connector.document-type' = 'payment',
                'update-mode' = 'append',
                'connector.flush-on-checkpoint' = 'true',
                'connector.key-delimiter' = '$',
                'connector.key-null-literal' = 'n/a',
                'connector.bulk-flush.max-size' = '42mb',
                'connector.bulk-flush.max-actions' = '32',
                'connector.bulk-flush.interval' = '1000',
                'connector.bulk-flush.backoff.delay' = '1000',
                'format.type' = 'json'
            )
    """

    t_env.sql_update(source_ddl)
    t_env.sql_update(es_sink_ddl)

    t_env.register_function('province_id_to_name', province_id_to_name)

    query = """
    select province_id_to_name(provinceId) as province, sum(payAmount) as pay_amount, tumble_start(rt, interval '5' seconds) as rowtime
    from payment_msg
    group by tumble(rt, interval '5' seconds), provinceId
    """

    t_env.sql_query(query).insert_into("es_sink")

    t_env.execute("payment_demo")


if __name__ == '__main__':
    log_processing()