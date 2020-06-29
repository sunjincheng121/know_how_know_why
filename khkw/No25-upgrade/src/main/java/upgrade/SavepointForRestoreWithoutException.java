package upgrade;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - upgrade
 * 功能描述: 演示开启Checkpoint之后,failover之后可以从失败之前的状态进行续跑。
 * 操作步骤:
 *        0. 进行SavepointForRestore的测试之后，进行这个测试
 *        1. 打包： mvn clean package
 *        2. 其中作业：bin/flink run -d -m localhost:4000 -c upgrade.SavepointForRestoreWithoutException /Users/jincheng.sunjc/work/know_how_know_why/khkw/No25-upgrade/target/No25-upgrade-0.1.jar
 *        3.创建savepoint： bin/flink savepoint 1fb52d2d72906045dbba2ce4199f245b
 *        4. 停止以前的作业，然后从savepoint启动
 *        6. bin/flink run -m localhost:4000 -s file:///tmp/chkdir/savepoint-1fb52d-126a8a0e36c3
 *     -c upgrade.SavepointForRestoreWithoutException /Users/jincheng.sunjc/work/know_how_know_why/khkw/No25-upgrade/target/No25-upgrade-0.1.jar \
 * 作者： 孙金城
 * 日期： 2020/6/29
 */
public class SavepointForRestoreWithoutException {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 打开Checkpoint, 我们也可以用 -D <property=value> CLI设置
        env.enableCheckpointing(20);
        // 作业停止后保留CP文件
        env.getCheckpointConfig().enableExternalizedCheckpoints(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);


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
        source.keyBy(0).sum(1).print();

        env.execute("SavepointForFailoverWithoutException");
    }
}
