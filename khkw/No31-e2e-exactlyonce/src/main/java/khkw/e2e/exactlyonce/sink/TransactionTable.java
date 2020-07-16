package khkw.e2e.exactlyonce.sink;

import org.apache.flink.api.java.tuple.Tuple3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.e2e.exactlyonce.sink
 * 功能描述: 这是e2e Exactly-once 的临时表抽象。
 * 作者： 孙金城
 * 日期： 2020/7/16
 */
public class TransactionTable implements Serializable {
    private transient TransactionDB db;
    private final String transactionId;
    private final List<Tuple3<String, Long, String>> buffer = new ArrayList<>();

    public TransactionTable(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionTable insert(Tuple3<String, Long, String> value) {
        initDB();
        // 投产的话，应该逻辑应该写到远端DB或者文件系统等。
        buffer.add(value);
        return this;
    }

    public TransactionTable flush() {
        initDB();
        db.firstPhase(transactionId, buffer);
        return this;
    }

    public void close() {
        buffer.clear();
    }

    private void initDB() {
        if (null == db) {
            db = TransactionDB.getInstance();
        }
    }
}
