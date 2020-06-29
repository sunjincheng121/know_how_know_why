package upgrade;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - upgrade
 * 功能描述: 演示开启Checkpoint之后,failover之后可以从失败之前的状态进行续跑。
 * 操作步骤:
 *        0. 修改 pom 文件的依赖配置，增加 <scope>provided</scope>
 *        1. mvn 打包
 *        2.下载flink发布包https://www.apache.org/dyn/closer.lua/flink/flink-1.10.1/flink-1.10.1-bin-scala_2.11.tgz
 *        3. 配置 flink-cong.yaml
 *          配置statebackend
 *          - state.backend: filesystem
 *          配置checkpoint&savepoint
 *          - state.checkpoints.dir: file:///tmp/chkdir
 *          - state.savepoints.dir: file:///tmp/chkdir
 *          配置失败重启策略
 *          - restart-strategy: fixed-delay
 *          - restart-strategy.fixed-delay.attempts: 3
 *          - restart-strategy.fixed-delay.delay: 2 s
 *          配置checkpoint保存个数
 *          - state.checkpoints.num-retained: 2
 *          配置local recovery for this state backend
 *          - state.backend.local-recovery: true
 *
 *        4. bin/start-cluster.sh local
 *        5. bin/flink run -m localhost:4000 -c upgrade.SavepointForRestore /Users/jincheng.sunjc/work/know_how_know_why/khkw/No25-upgrade/target/No25-upgrade-0.1.jar
 *        6. bin/flink run -m localhost:4000 -s file:///tmp/chkdir/caab8d0a04aa0ce718da5333cad10607/chk-364
 *        -c upgrade.SavepointForRestore /Users/jincheng.sunjc/work/know_how_know_why/khkw/No25-upgrade/target/No25-upgrade-0.1.jar \
 *        upgrade.SavepointForRestore
 *
 *        7. 将程序去除异常，运行之后，触发savepoint
 * 作者： 孙金城
 * 日期： 2020/6/29
 */
public class SavepointForRestore {
    public static void main(String[] args) throws Exception {
        Logger log = LoggerFactory.getLogger(SavepointForRestore.class);
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

        env.execute("SavepointForFailover");
    }
}
