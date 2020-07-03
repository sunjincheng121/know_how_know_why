package khkw.functions;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.functions
 * 功能描述: 包含一个ValueState操作的ProcessFunction，仅供作业恢复测试。
 * <p>
 * 作者： 孙金城
 * 日期： 2020/7/3
 */
public class StateProcessFunction
        extends KeyedProcessFunction<Tuple, Tuple3<String, Integer, Long>, Tuple3<String, Integer, Long>> {

    private static Logger LOG = LoggerFactory.getLogger(StateProcessFunction.class);
    private transient ValueState<Long> indexState;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        indexState = getRuntimeContext()
                .getState(new ValueStateDescriptor<Long>("indexState", Long.class));
    }

    @Override
    public void processElement(Tuple3<String, Integer, Long> event, Context ctx, Collector<Tuple3<String, Integer, Long>> out) throws Exception {
        Long currentValue = indexState.value();
        if(null == currentValue){
            LOG.warn("Initialize when first run or failover...");
            currentValue = 0l;
        }
        LOG.debug(String.format("Current Value [%d]", currentValue));
        indexState.update(currentValue + 1);
        event.f2 = currentValue;
        out.collect(event);
    }

}
