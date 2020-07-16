package khkw.e2e.exactlyonce.functions;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.CheckpointListener;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.e2e.exactlyonce.functions
 * 功能描述: 触发异常
 * <p>
 * 作者： 孙金城
 * 日期： 2020/7/16
 */
public class MapFunctionWithException extends
        RichMapFunction<Tuple3<String, Long, String>, Tuple3<String, Long, String>>
        implements CheckpointListener {

    private long delay;
    private transient volatile boolean needFail = false;

    public MapFunctionWithException(long delay) {
        this.delay = delay;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public Tuple3<String, Long, String> map(Tuple3<String, Long, String> event) throws Exception {
        Thread.sleep(delay);
        if(needFail){
            throw new RuntimeException("Error for testing...");
        }
        return event;
    }

    @Override
    public void notifyCheckpointComplete(long l) throws Exception {
        this.needFail = true;
        System.err.println(String.format("MAP - CP SUCCESS [%d]", l));
    }
}
