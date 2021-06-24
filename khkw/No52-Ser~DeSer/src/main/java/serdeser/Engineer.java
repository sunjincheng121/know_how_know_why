package serdeser;

import java.io.Serializable;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - Ser/DeSer
 * 功能描述: 测试代码，仅限于示例，不能投产
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2021/06/24
 */
public class Engineer implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    public String tel;
    public int age;
    public transient String transientData;

    public void hello() {
        System.out.println(String.format("Engineer, I am %s !", name));
    }
}
