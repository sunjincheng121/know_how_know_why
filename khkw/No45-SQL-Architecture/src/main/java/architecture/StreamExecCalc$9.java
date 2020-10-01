package architecture;

public class StreamExecCalc$9 extends org.apache.flink.table.runtime.operators.AbstractProcessStreamOperator
        implements org.apache.flink.streaming.api.operators.OneInputStreamOperator {

    private final Object[] references;
    private transient org.apache.flink.table.runtime.typeutils.StringDataSerializer typeSerializer$4;
    org.apache.flink.table.data.BoxedWrapperRowData out = new org.apache.flink.table.data.BoxedWrapperRowData(3);
    private final org.apache.flink.streaming.runtime.streamrecord.StreamRecord outElement = new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(null);

    public StreamExecCalc$9(
            Object[] references,
            org.apache.flink.streaming.runtime.tasks.StreamTask task,
            org.apache.flink.streaming.api.graph.StreamConfig config,
            org.apache.flink.streaming.api.operators.Output output,
            org.apache.flink.streaming.runtime.tasks.ProcessingTimeService processingTimeService) throws Exception {
        this.references = references;
        typeSerializer$4 = (((org.apache.flink.table.runtime.typeutils.StringDataSerializer) references[0]));
        this.setup(task, config, output);
        if (this instanceof org.apache.flink.streaming.api.operators.AbstractStreamOperator) {
            ((org.apache.flink.streaming.api.operators.AbstractStreamOperator) this)
                    .setProcessingTimeService(processingTimeService);
        }
    }

    @Override
    public void open() throws Exception {
        super.open();

    }

    @Override
    public void processElement(org.apache.flink.streaming.runtime.streamrecord.StreamRecord element) throws Exception {
        org.apache.flink.table.data.RowData in1 = (org.apache.flink.table.data.RowData) element.getValue();

        org.apache.flink.table.data.binary.BinaryStringData field$3;
        boolean isNull$3;
        org.apache.flink.table.data.binary.BinaryStringData field$5;
        int field$6;
        boolean isNull$6;
        boolean isNull$7;
        int result$8;



        isNull$3 = in1.isNullAt(0);
        field$3 = org.apache.flink.table.data.binary.BinaryStringData.EMPTY_UTF8;
        if (!isNull$3) {
            field$3 = ((org.apache.flink.table.data.binary.BinaryStringData) in1.getString(0));
        }
        field$5 = field$3;
        if (!isNull$3) {
            field$5 = (org.apache.flink.table.data.binary.BinaryStringData) (typeSerializer$4.copy(field$5));
        }

        isNull$6 = in1.isNullAt(1);
        field$6 = -1;
        if (!isNull$6) {
            field$6 = in1.getInt(1);
        }

        out.setRowKind(in1.getRowKind());




        if (isNull$3) {
            out.setNullAt(0);
        } else {
            out.setNonPrimitiveValue(0, field$5);
        }





        isNull$7 = isNull$6 || false;
        result$8 = -1;
        if (!isNull$7) {

            result$8 = (int) (field$6 - ((int) 2));

        }

        if (isNull$7) {
            out.setNullAt(1);
        } else {
            out.setInt(1, result$8);
        }



        if (false) {
            out.setNullAt(2);
        } else {
            out.setInt(2, ((int) 37));
        }


        output.collect(outElement.replace(out));


    }



    @Override
    public void close() throws Exception {
        super.close();

    }


}
    