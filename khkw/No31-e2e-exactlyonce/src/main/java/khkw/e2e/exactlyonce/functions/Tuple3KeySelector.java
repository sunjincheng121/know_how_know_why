package khkw.e2e.exactlyonce.functions;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.e2e.exactlyonce.functions
 * 作者： 孙金城
 * 日期： 2020/7/16
 */
public class Tuple3KeySelector implements KeySelector<Tuple3<String, Long, String>, String> {
    @Override
    public String getKey(Tuple3<String, Long, String> event) throws Exception {
        return event.f0;
    }
}
