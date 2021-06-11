package datatype;

import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.types.Row;

@FunctionHint(output = @DataTypeHint("ROW<name STRING, age INT>"))
public class ToRow extends ScalarFunction {
    public Row eval() {
        return Row.of("Sunny", 10);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ToRow;
    }
}