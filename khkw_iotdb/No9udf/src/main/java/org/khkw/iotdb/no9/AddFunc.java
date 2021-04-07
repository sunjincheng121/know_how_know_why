package org.khkw.iotdb.no9;

import org.apache.iotdb.db.query.udf.api.UDTF;
import org.apache.iotdb.db.query.udf.api.access.Row;
import org.apache.iotdb.db.query.udf.api.collector.PointCollector;
import org.apache.iotdb.db.query.udf.api.customizer.config.UDTFConfigurations;
import org.apache.iotdb.db.query.udf.api.customizer.parameter.UDFParameterValidator;
import org.apache.iotdb.db.query.udf.api.customizer.parameter.UDFParameters;
import org.apache.iotdb.db.query.udf.api.customizer.strategy.RowByRowAccessStrategy;
import org.apache.iotdb.tsfile.exception.write.UnSupportedDataTypeException;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddFunc implements UDTF {

    private static final Logger logger = LoggerFactory.getLogger(AddFunc.class);

    private double addend;

    @Override
    public void validate(UDFParameterValidator validator) throws Exception {
        validator
                .validateInputSeriesNumber(2)
                .validateInputSeriesDataType(
                        0, TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE)
                .validateInputSeriesDataType(
                        1, TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE);
    }

    @Override
    public void beforeStart(UDFParameters parameters, UDTFConfigurations configurations) {
        logger.debug("Adder#beforeStart");
        addend = parameters.getFloatOrDefault("addend", 0);
        configurations
                .setOutputDataType(TSDataType.INT64)
                .setAccessStrategy(new RowByRowAccessStrategy());
    }

    @Override
    public void transform(Row row, PointCollector collector) throws Exception {
        if (row.isNull(0) || row.isNull(1)) {
            return;
        }
        collector.putLong(
                row.getTime(), (long) (extractDoubleValue(row, 0) + extractDoubleValue(row, 1) + addend));
    }

    private double extractDoubleValue(Row row, int index) {
        double value;
        switch (row.getDataType(index)) {
            case INT32:
                value = row.getInt(index);
                break;
            case INT64:
                value = (double) row.getLong(index);
                break;
            case FLOAT:
                value = row.getFloat(index);
                break;
            case DOUBLE:
                value = row.getDouble(index);
                break;
            default:
                throw new UnSupportedDataTypeException(row.getDataType(index).toString());
        }
        return value;
    }

    @Override
    public void beforeDestroy() {
        logger.debug("Adder#beforeDestroy");
    }
}