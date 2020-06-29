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
 * 功能描述: 演示开启failureRate的重启策略，Flink作业异常后的行为。
 * 操作步骤:
 *        1. 直接运行程序，作业5分钟内5次失败，测终止作业，每次启动间隔5秒钟，当在5分钟内第5次异常后，终止作业。
 *        2. 修改策略failureRateRestart(5, Time.of(5, TimeUnit.MINUTES), Time.of(5, TimeUnit.SECONDS)))，
 *           也即是 5秒内如果又5次异常，则终止作业。本测试将会 不停的重启，因为，5秒内始终不会达到5次异常。
 * 作者： 孙金城
 * 日期： 2020/6/29
 */
public class FailureRateRestartJob {
    public static void main(String[] args) throws Exception {
        Logger log = LoggerFactory.getLogger(NoRestartJob.class);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

       env.setRestartStrategy(
               RestartStrategies.failureRateRestart(
                       5, Time.of(5, TimeUnit.SECONDS), Time.of(5, TimeUnit.SECONDS)));

        DataStream<Tuple3<String, Integer, Long>> source = env
                .addSource(new SourceFunction<Tuple3<String, Integer, Long>>() {
                    @Override
                    public void run(SourceContext<Tuple3<String, Integer, Long>> ctx) throws Exception {
                        int index = 1;
                        while(true){
                            ctx.collect(new Tuple3<>("key", index++, System.currentTimeMillis()));
                            // Just for testing
                            Thread.sleep(200);
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
