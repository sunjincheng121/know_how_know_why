package khkw.correctness;

import khkw.correctness.functions.*;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.correctness.functions
 * 功能描述:
 * 操作步骤:
 *    1. 直接运行程序，作业会不停的重复失败，直至退出, 观察恢复时候的offset值。
 *    2. 修改SimpleCheckpointedSource的initializeState的offset值，如果是9，19变成10，20，观察效果
 *    3. 注释 nonParallel(env) 打开 parallel(env)。观察source多并发时候的效果。
 *    4. 打开parallelFromTaskIndex(env);观察恢复的时候从上次index相同的task的offset开始消费。
 * <p>
 * 作者： 孙金城
 * 日期： 2020/7/13
 */
public class CheckpointedSourceTestCase {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(3, Time.of(2, TimeUnit.SECONDS)));

        env.enableCheckpointing(20);

        nonParallel(env);
//        parallel(env);
//        parallelFromTaskIndex(env);

        env.execute("NonParallelCheckpointedSource");
    }

    private static void nonParallel(StreamExecutionEnvironment env) {
        env.setParallelism(1);
        env.addSource(new NonParallelCheckpointedSource())
                .map(new MapFunctionWithException())
                .keyBy(new Tuple3KeySelector())
                .sum(1).print();
    }

    private static void parallel(StreamExecutionEnvironment env) {
        env.setParallelism(2);
        env.addSource(new ParallelCheckpointedSource())
                .map(new MapFunctionWithException())
                .keyBy(new Tuple3KeySelector())
                .sum(1).print();
    }

    private static void parallelFromTaskIndex(StreamExecutionEnvironment env) {
        env.setParallelism(2);
        env.addSource(new ParallelCheckpointedSourceRestoreFromTaskIndex())
                .map(new MapFunctionWithException())
                .keyBy(new Tuple3KeySelector())
                .sum(1).print();
    }
}
