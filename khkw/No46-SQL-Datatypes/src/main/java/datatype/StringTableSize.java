package datatype;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - datatype
 * 功能描述:  public class StringTable extends Hashtable, hash表桶的数量越多,数据越分散,hashcode撞车的概率越小，速度越快。
 * 操作步骤:
 * 1. Run->Edit Configurations 设置 -XX:StringTableSize=1500 运行程序,运行时间[14296]
 * 2. 修改 -XX:StringTableSize=15000 运行程序,运行时间[1457]
 * 3. 修改 -XX:StringTableSize=150000 运行程序,运行时间[343]
 * 4. 修改 -XX:StringTableSize=1000000 运行程序,运行时间[284]
 * 5. 可以添加 -XX:+PrintStringTableStatistics 查看StringTable的统计信息
 * <p>
 * 作者： 孙金城
 * 日期： 2020/10/11
 */
public class StringTableSize {
    public static void main(String[] args) {
        long start = new Date().getTime();
       List<String> strList = new ArrayList<>();
        for(int i=0; i<1000000; i++){
            strList.add(("stringTable-" + i).intern());
        }
        long end = new Date().getTime();
        System.out.println(String.format("运行时间[%d]",end - start));
    }
}
