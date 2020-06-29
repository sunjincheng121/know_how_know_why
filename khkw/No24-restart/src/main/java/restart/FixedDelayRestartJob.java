package restart;

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
 * 项目名称: Apache Flink 知其然，知其所以然 - restart
 * 功能描述: 演示重启FixedDelay的重启作业，Flink作业异常后的行为以固定的频率重启作业。
 * 操作步骤:
 *        1. 直接运行程序，当作业打印出9之后，抛出异常，重启作业，当第四次打印9之后，作业终止。
 *        观察 重启之后的时间戳，感知 fixedDelayRestart(3, Time.of(2, TimeUnit.SECONDS)中2秒的含义。
 *        (key,9,2020-06-29 09:55:50.164)
 *        09:55:50,267 ERROR restart.NoRestartJob - Bad data [10]...
 *        (key,1,2020-06-29 09:55:52.295)
 *
 *        2. 将 Time.of(2, TimeUnit.SECONDS)， 变成 Time.of(3, TimeUnit.SECONDS) 观察昨天重启时间变化。
 *        (key,9,2020-06-29 09:59:55.277)
 *        09:59:55,382 ERROR restart.NoRestartJob                                          - Bad data [10]...
 *        (key,1,2020-06-29 09:59:58.41)
 * <p>
 * 作者： 孙金城
 * 日期： 2020/6/29
 */
public class FixedDelayRestartJob {
    public static void main(String[] args) throws Exception {
        Logger log = LoggerFactory.getLogger(NoRestartJob.class);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(3, Time.of(2, TimeUnit.SECONDS) ));

//        env.setRestartStrategy(
//                RestartStrategies.fixedDelayRestart(3, Time.of(3, TimeUnit.SECONDS) ));

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
        }).print();

        env.execute("FixedDelayRestart");
    }
}
