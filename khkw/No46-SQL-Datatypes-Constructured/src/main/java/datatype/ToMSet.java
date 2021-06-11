package datatype;

import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.functions.ScalarFunction;

import java.util.HashMap;
import java.util.Map;

@FunctionHint(output = @DataTypeHint("MULTISET<VARCHAR>"))
public class ToMSet extends ScalarFunction {
    public Map<String, Integer> eval(String[] datas) {
        Map<String, Integer> mset = new HashMap<>();
        for(String data:datas){
            if(mset.containsKey(data)){
                mset.put(data, mset.get(data) + 1);
            }else{
                mset.put(data, 1);
            }
        }
        return mset;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ToMSet;
    }
}
