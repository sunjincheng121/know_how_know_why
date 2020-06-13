    package qa.kafka;

import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

/**
 * We need create a topic with two partitions. make first partition data pretty slow.
 */
public class MakeIdlePartitioner extends FlinkKafkaPartitioner<Row> {

    private static final long serialVersionUID = -3785320239953858777L;
    private static int secondPartitionDataCount = 0;

    /**
     * Return the num of partitions.
     */
    @Override
    public int partition(Row record, byte[] key, byte[] value, String targetTopic, int[] partitions) {
        Preconditions.checkArgument(
                partitions != null && partitions.length > 0,
                "Partitions of the target topic is empty.");

        try {
            // Just for testing, 500ms pre record.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 20 records for second partition and 1 for first partition.
        if (secondPartitionDataCount < 20){
            secondPartitionDataCount ++;
            return 1;
        } else {
            secondPartitionDataCount = 0;
            System.err.println("To first Partition: ["+record+"]");
            return 0;
        }

    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof MakeIdlePartitioner;
    }

    @Override
    public int hashCode() {
        return MakeIdlePartitioner.class.hashCode();
    }
}
