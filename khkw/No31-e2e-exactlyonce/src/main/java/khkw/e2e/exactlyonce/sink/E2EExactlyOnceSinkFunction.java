package khkw.e2e.exactlyonce.sink;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.base.VoidSerializer;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.streaming.api.functions.sink.TwoPhaseCommitSinkFunction;

import java.util.UUID;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.e2e.exactlyonce.functions
 * 功能描述: 端到端的精准一次语义sink示例（测试）
 * TwoPhaseCommitSinkFunction有4个方法:
 * - beginTransaction() Call on initializeState
 * - preCommit() Call on snapshotState
 * - commit()  Call on notifyCheckpointComplete()
 * - abort() Call on close()
 * <p>
 * 作者： 孙金城
 * 日期： 2020/7/16
 */
public class E2EExactlyOnceSinkFunction extends
        TwoPhaseCommitSinkFunction<Tuple3<String, Long, String>, TransactionTable, Void> {

    public E2EExactlyOnceSinkFunction() {
        super(new KryoSerializer<>(TransactionTable.class, new ExecutionConfig()), VoidSerializer.INSTANCE);
    }

    @Override
    protected void invoke(TransactionTable table, Tuple3<String, Long, String> value, Context context) throws Exception {
        table.insert(value);
    }

    /**
     * Call on initializeState
     */
    @Override
    protected TransactionTable beginTransaction() {
        return TransactionDB.getInstance().createTable(
                String.format("TransID-[%s]", UUID.randomUUID().toString()));
    }

    /**
     * Call on snapshotState
     */
    @Override
    protected void preCommit(TransactionTable table) throws Exception {
        table.flush();
        table.close();
    }

    /**
     * Call on notifyCheckpointComplete()
     */
    @Override
    protected void commit(TransactionTable table) {
        System.err.println(String.format("SINK - CP SUCCESS [%s]", table.getTransactionId()));
        TransactionDB.getInstance().secondPhase(table.getTransactionId());
    }

    /**
     * Call on close()
     */
    @Override
    protected void abort(TransactionTable table) {
        TransactionDB.getInstance().removeTable("Abort", table.getTransactionId());
        table.close();
    }
}
