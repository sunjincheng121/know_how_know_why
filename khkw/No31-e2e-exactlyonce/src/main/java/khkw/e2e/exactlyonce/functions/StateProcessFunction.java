package khkw.e2e.exactlyonce.functions;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Iterator;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.e2e.exactlyonce.functions
 * 功能描述: 记录所有处理过的数据，测试在AT_LEAST_ONCE时候重复消费的数据。
 * <p>
 * 作者： 孙金城
 * 日期：  2020/7/16
 */
public class StateProcessFunction
        extends KeyedProcessFunction<String, Tuple3<String, Long, String>, Tuple3<String, Long, String>> {

    private transient ListState<Tuple3<String, Long, String>> processData;
    private final String STATE_NAME = "processData";

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        processData = getRuntimeContext().getListState(
                        new ListStateDescriptor<>(STATE_NAME, Types.TUPLE(Types.STRING, Types.LONG, Types.STRING)));
    }

    @Override
    public void processElement(Tuple3<String, Long, String> event, Context ctx, Collector<Tuple3<String, Long, String>> out) throws Exception {
        boolean isDuplicate = false;
        Iterator<Tuple3<String, Long, String>> it = processData.get().iterator();
        while(it.hasNext()) {
            if(it.next().equals(event)) {
                isDuplicate = true;
                break;
            }
        }
        if(isDuplicate){
           out.collect(event);
        }else{
            processData.add(event);
        }

    }

}
