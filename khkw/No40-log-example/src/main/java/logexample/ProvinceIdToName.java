package logexample;
import org.apache.flink.table.functions.ScalarFunction;
/**
 * 项目名称: Apache Flink 知其然，知其所以然 - logexample
 * 功能描述: 根据省ID获取省名称
 * <p>
 * 作者： 孙金城
 * 日期： 2020/8/13
 */
public class ProvinceIdToName extends ScalarFunction {
    static String[] provinces = new String[]{"北京", "上海", "杭州", "深圳", "江西", "重庆", "西藏"};
    public String eval(Integer provinceId) {
        System.err.println(String.format("[%d] to [%s]", provinceId, provinces[provinceId]));
        return provinces[provinceId];
    }
}
