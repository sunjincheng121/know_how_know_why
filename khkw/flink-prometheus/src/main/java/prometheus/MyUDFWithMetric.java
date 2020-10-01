package prometheus;

import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.runtime.metrics.DescriptiveStatisticsHistogram;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.ScalarFunction;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - prometheus
 * 功能描述: Metric打点
 * 作者： 孙金城
 * 日期： 2020/9/19
 */
public class MyUDFWithMetric extends ScalarFunction {
    private transient Counter eventCounter;
    private transient Histogram valueHistogram;

    public void open(FunctionContext context) throws Exception {
        eventCounter = context.getMetricGroup().counter("udf_events");
        valueHistogram = context
                .getMetricGroup()
                .histogram("udf_value_histogram", new DescriptiveStatisticsHistogram(10_000));

    }

    public Integer eval(Integer event) {
        eventCounter.inc();
        System.out.println(String.format("udf_events[%d]", eventCounter.getCount()));
        valueHistogram.update(event);
        return event;
    }

}
