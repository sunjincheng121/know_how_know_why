package datatype;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - datatype
 * 功能描述:
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2020/10/11
 */
public class StringIsObject {
    public static void main(String[] args) {
        String str = "Hello world!";
        String str1 = "Hello world!";
        String str2 = "Hello world!".intern();
        String str3 = new String("Hello world!");
        String str4 = "Hello " + "world!";
        String str5 = "Hello " + new String("world!");
        System.out.println(str.hashCode());
        System.out.println(str1.hashCode());
        System.out.println(str2.hashCode());
        System.out.println(str3.hashCode());
        System.out.println(str4.hashCode());
        System.out.println(str5.hashCode());

        System.out.println(str1 == str2);
        System.out.println(str1 == str3);
        System.out.println(str1.equals(str3));
        System.out.println(str1 == str4);
        System.out.println(str1 == str5);
    }
}
