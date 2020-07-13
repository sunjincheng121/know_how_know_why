package khkw.correctness.functions;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.correctness.functions
 * <p>
 * 作者： 孙金城
 * 日期： 2020/7/13
 */
public class ParallelCheckpointedSource
        extends RichParallelSourceFunction<Tuple3<String, Long, Long>>
        implements CheckpointedFunction {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ParallelCheckpointedSource.class);
    // 标示数据源一直在取数据
    protected volatile boolean running = true;

    // 数据源的消费offset
    private transient long offset;

    // offsetState
    private transient ListState<Long> offsetState;

    // offsetState name
    private static final String OFFSETS_STATE_NAME = "offset-states";

    // 当前任务实例的编号
    private transient int indexOfThisTask;

    @Override
    public void run(SourceContext<Tuple3<String, Long, Long>> ctx) throws Exception {
        while(running){
            ctx.collect(new Tuple3<>("key", ++offset, System.currentTimeMillis()));
            Thread.sleep(100 * (indexOfThisTask + 1));
        }
    }

    @Override
    public void snapshotState(FunctionSnapshotContext ctx) throws Exception {
        if (!running) {
            LOG.error("snapshotState() called on closed source");
        } else {
            // 清除上次的state
            this.offsetState.clear();
            // 持久化最新的offset
            this.offsetState.add(offset);
        }
    }

    @Override
    public void initializeState(FunctionInitializationContext ctx) throws Exception {
        indexOfThisTask = getRuntimeContext().getIndexOfThisSubtask();

        offsetState = ctx
                .getOperatorStateStore()
                .getListState(new ListStateDescriptor<>(OFFSETS_STATE_NAME, Types.LONG));

        for (Long offsetValue : offsetState.get()) {
            offset = offsetValue;
            // 跳过10和20的循环失败
            if (offset == 9 || offset == 19) {
                offset += 1;
            }
            // user error, just for test
            LOG.error(String.format("Current Task [%d] Restore from offset [%d]", indexOfThisTask, offset));
        }

    }

    @Override
    public void cancel() {
    }
}
