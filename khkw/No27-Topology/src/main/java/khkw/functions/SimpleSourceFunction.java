package khkw.functions;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.functions
 * 功能描述: 产生Tuple3<String, Integer, Long>的数据源，仅供测试使用。
 * <p>
 * 作者： 孙金城
 * 日期： 2020/7/3
 */
public class SimpleSourceFunction implements SourceFunction<Tuple3<String, Integer, Long>> {
    @Override
    public void run(SourceContext<Tuple3<String, Integer, Long>> ctx) throws Exception {
        int index = 1;
        while (true) {
            ctx.collect(new Tuple3<>("key", ++index, System.currentTimeMillis()));
//            ctx.collect(new Tuple3<>("key2", index, System.currentTimeMillis()));
//            ctx.collect(new Tuple3<>("key3", index, System.currentTimeMillis()));
            Thread.sleep(500);
        }
    }

    @Override
    public void cancel() {

    }
}
