package khkw;

import khkw.functions.SideOutputProcessFunction;
import khkw.functions.SimpleSourceFunction;
import khkw.functions.StateProcessFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.OutputTag;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - Topology
 * 功能描述: 演示业务需要进行topology变化时候，如何升级。
 * 操作步骤:
 * 0. 采用No.25课程使用的flink-conf.yaml文件。
 * 1. 打包： mvn clean package
 * 2. 启动作业：bin/flink run -d -m localhost:4000 -c khkw.TopologyChanges /Users/jincheng.sunjc/work/know_how_know_why/khkw/No27-Topology/target/No27-Topology-0.1.jar
 * 3. 创建savepoint并停止作业：  bin/flink cancel -s 834b7e0c414ac7d4c771c13bdbcbcf60
 * 4. 修改作业，将 version2 注释打开 并注释掉version1, 并在source function增加一个key2
 * 5. 打包： mvn clean package
 * 6. bin/flink run -m localhost:4000 -s file:///tmp/chkdir/savepoint-834b7e-47b8ba0c40ae
 * -c khkw.TopologyChanges /Users/jincheng.sunjc/work/know_how_know_why/khkw/No27-Topology/target/No27-Topology-0.1.jar
 * 预期会恢复失败，需要添加 --allowNonRestoredState 选项
 * 7. 携带--allowNonRestoredState，启动：bin/flink run -d -m localhost:4000 -s file:///tmp/chkdir/savepoint-834b7e-47b8ba0c40ae --allowNonRestoredState
 * -c khkw.TopologyChanges /Users/jincheng.sunjc/work/know_how_know_why/khkw/No27-Topology/target/No27-Topology-0.1.jar
 *  预期 作业能启动，但是state恢复的值不对。。。:(
 * 8.修改作业，状态节点设置uid [.sum(1).uid("khkw_sum")]，注释version2，使用version3。
 * 9. mvn clean package
 * 10. 启动 bin/flink run -d -m localhost:4000 -c khkw.TopologyChanges /Users/jincheng.sunjc/work/know_how_know_why/khkw/No27-Topology/target/No27-Topology-0.1.jar
 * 11. 创建savepoint并停止作业： bin/flink cancel -s 4ca10c0402ddba3b9455e80bdea7a7bd
 * 12. 修改作业，sourcefunction 增加 key3， 使用version4。
 * 13. 携带--allowNonRestoredState，启动：bin/flink run -d -m localhost:4000 -s file:///tmp/chkdir/savepoint-2bba7a-cc9506fd8052 --allowNonRestoredState
 *    -c khkw.TopologyChanges /Users/jincheng.sunjc/work/know_how_know_why/khkw/No27-Topology/target/No27-Topology-0.1.jar
 * 作者： 孙金城
 * 日期： 2020/6/29
 */
public class TopologyChanges {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        final OutputTag<Tuple3<String, Integer, Long>> outputTag =
                new OutputTag<Tuple3<String, Integer, Long>>("side-output") {};

        // 打开Checkpoint
        env.enableCheckpointing(20);

         // 业务版本1
        version1(env, outputTag);
        // 业务版本2
//        version2(env);
        // 业务版本3
//        version3(env, outputTag);
        // 业务版本4
//        version4(env);

        // 大家可以在 https://flink.apache.org/visualizer/ 生成拓扑图
        String planJson = env.getExecutionPlan();

        env.execute("TopologyChanges");
    }

    /**
     * 第一个业务版本
     */
    private static void version1(StreamExecutionEnvironment env, OutputTag<Tuple3<String, Integer, Long>> outputTag) {
        SingleOutputStreamOperator<Tuple3<String, Integer, Long>> mainResult = env
                .addSource(new SimpleSourceFunction())
                .keyBy(0)
                .sum(1)
                .process(new SideOutputProcessFunction<>(outputTag));
        mainResult.print().name("main-result");
        mainResult.getSideOutput(outputTag).print().name("side-output");
    }

    /**
     * 升级后的业务版本
     */
    private static void version2(StreamExecutionEnvironment env) {
        SingleOutputStreamOperator<Tuple3<String, Integer, Long>> mainResult = env
                .addSource(new SimpleSourceFunction())
                .keyBy(0)
                .process(new StateProcessFunction())
                .keyBy(0)
                .sum(1);
        mainResult.print().name("main-result");
    }

    /**
     * 第三个业务版本
     */
    private static void version3(StreamExecutionEnvironment env, OutputTag<Tuple3<String, Integer, Long>> outputTag) {
        SingleOutputStreamOperator<Tuple3<String, Integer, Long>> mainResult = env
                .addSource(new SimpleSourceFunction())
                .keyBy(0)
                .sum(1).uid("khkw_sum")
                .process(new SideOutputProcessFunction<>(outputTag));
        mainResult.print().name("main-result");
        mainResult.getSideOutput(outputTag).print().name("side-output");
    }

    /**
     * 第四个业务版本
     */
    private static void version4(StreamExecutionEnvironment env) {
        SingleOutputStreamOperator<Tuple3<String, Integer, Long>> mainResult = env
                .addSource(new SimpleSourceFunction())
                .keyBy(0)
                .process(new StateProcessFunction())
                .keyBy(0)
                .sum(1).uid("khkw_sum");
        mainResult.print().name("main-result");
    }
}
