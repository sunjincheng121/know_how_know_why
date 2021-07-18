package khkw;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

/**
 * 1. 先命令行 nc -lk 9999
 * 2. 启动WordCount
 * 3. 输入 word 从命令行
 * 4. flink run模式部署
 *  - mvn clean package
 *  - 修改配置 conf/flink-conf.yaml
 *    env.java.opts: -Xms2048m -Xmx2048m
 *    rest.port: 4000
 *    taskmanager.memory.process.size: 1024m
 *    taskmanager.numberOfTaskSlots: 2
 *  启动集群
 *    cd /Users/jincheng/work/flink/flink-dist/target/flink-1.14-SNAPSHOT-bin/flink-1.14-SNAPSHOT
 *    bin/start-cluster.sh
 *    http://localhost:4000/#/overview (当前1个JobManager进程，一个TaskManager进程）
 *    ./bin/taskmanager.sh start （我们再多启动一个TaskManager进程）
 *  - nc -lk 9999
 *  - bin/flink run -d -m localhost:4000 -c khkw.WordCount /Users/jincheng/work/know_how_know_why/khkw/No52-Ser~DeSer/target/No52-Ser-DeSer-0.1.jar
 **/
public class WordCount {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(
                RestartStrategies.failureRateRestart(  3, Time.of(5, TimeUnit.MINUTES), Time.of(10, TimeUnit.SECONDS)));

        DataStream<Tuple2<String, Integer>> dataStream = env
                .socketTextStream("localhost", 9999)
                .flatMap(new Splitter()).setParallelism(4)
                .keyBy(value -> value.f0)
                .reduce(new Reducer()).setParallelism(3);
        dataStream.print().setParallelism(3);

        String plan = env.getExecutionPlan();
        System.out.println(plan);
        env.execute("khkw.WordCount");
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word: sentence.split(" ")) {
                out.collect(new Tuple2<>(word, 1));
            }
        }
    }

    public static class Reducer implements ReduceFunction<Tuple2<String, Integer>> {

        @Override
        public Tuple2<String, Integer> reduce(Tuple2<String, Integer> t2, Tuple2<String, Integer> t1) throws Exception {
            return new Tuple2<>(t1.f0, t1.f1 + t2.f1);
        }
    }

}
