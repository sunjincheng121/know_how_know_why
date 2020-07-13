package khkw.correctness;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.correctness
 * 功能描述: 主要回顾Checkpoint可以在failover时候进行基于原来state进行续跑，
 *         但是如果Source没有进行消费位置的记录的话，failover之后数据源的数据
 *         会重复消费。
 * 操作步骤:
 *        1. 执行运行程序，恢复没有Checkpoint，失败重启Sum值会从0开始累计。
 *        2. env.enableCheckpointing(20),再执行作业失败重启Sum值会基于以前的State进行续跑。
 *        NOTE: 主要观察Source没有进行消费位置的记录的话，failover之后数据源的数据
 *  *         会重复消费。
 * <p>
 * 作者： 孙金城
 * 日期： 2020/7/12
 */
public class No25SourceWithoutCheckpointed {
    public static void main(String[] args) throws Exception {
        Logger log = LoggerFactory.getLogger(No25SourceWithoutCheckpointed.class);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(3, Time.of(2, TimeUnit.SECONDS) ));

//        env.enableCheckpointing(20);

        DataStream<Tuple3<String, Integer, Long>> source = env
                .addSource(new SourceFunction<Tuple3<String, Integer, Long>>() {
                    @Override
                    public void run(SourceContext<Tuple3<String, Integer, Long>> ctx) throws Exception {
                        int index = 1;
                        while(true){
                            ctx.collect(new Tuple3<>("key", index++, System.currentTimeMillis()));
                            // Just for testing
                            Thread.sleep(100);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
        source.map(new MapFunction<Tuple3<String, Integer, Long>, Tuple3<String, Integer, String>>() {
            @Override
            public Tuple3<String, Integer, String> map(Tuple3<String, Integer, Long> event) throws Exception {
                if(event.f1 % 10 == 0) {
                    String msg = String.format("Bad data [%d]...", event.f1);
                    log.error(msg);
                    // 抛出异常，作业根据 配置 的重启策略进行恢复，无重启策略作业直接退出。
                    throw new RuntimeException(msg);
                }
                return new Tuple3<>(event.f0, event.f1, new Timestamp(System.currentTimeMillis()).toString());
            }
        }).keyBy(0).sum(1).print();

        env.execute("CheckpointForFailover");
    }
}
