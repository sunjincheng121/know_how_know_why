package khkw.e2e.exactlyonce.sink;

import org.apache.flink.api.java.tuple.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - khkw.e2e.exactlyonce.sink
 * 功能描述: 模拟远程存储或者业务本地为了实现端到端精准一次的载体。
 * <p>
 * 作者： 孙金城
 * 日期： 2020/7/16
 */
public class TransactionDB {
    private static Logger LOG = LoggerFactory.getLogger(TransactionDB.class);
    private final Map<String, List<Tuple3<String, Long, String>>> transactionRecords = new HashMap<>();

    private static TransactionDB instance;

    public static synchronized TransactionDB getInstance() {
        if (instance == null) {
            instance = new TransactionDB();
        }
        return instance;
    }

    private TransactionDB() {}

    /**
     * 创建当前事物的临时存储
     */
    public TransactionTable createTable(String transactionId) {
        LOG.error(String.format("Create Table for current transaction...[%s]", transactionId));
        transactionRecords.put(transactionId, new ArrayList<>());
        return new TransactionTable(transactionId);
    }

    /**
     *
     */
    public void secondPhase(String transactionId) {
        LOG.error(String.format("Persist current transaction...[%s] records...", transactionId));
        List<Tuple3<String, Long, String>> content = transactionRecords.get(transactionId);
        if(null == content){
            return;
        }
        content.forEach(this::print);
        // 提醒大家，这个非常重要，因为Notify 和 Recovery都会调用。
        removeTable("Notify or Recovery", transactionId);
        LOG.error(String.format("Persist current transaction...[%s] records...[SUCCESS]", transactionId));
    }

    private void print(Tuple3<String, Long, String> record){
        LOG.error(record.toString());
    }

    public void firstPhase(String transactionId, List<Tuple3<String, Long, String>> values) {
        List<Tuple3<String, Long, String>> content = transactionRecords.get(transactionId);
        content.addAll(values);
    }

    public void removeTable(String who, String transactionId){
        LOG.error(String.format("[%s], Remove table for transaction...[%s]", who, transactionId));
        transactionRecords.remove(transactionId);
    }
}

