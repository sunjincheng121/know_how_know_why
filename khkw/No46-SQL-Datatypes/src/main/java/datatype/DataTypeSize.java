package datatype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - datatype
 * 功能描述:
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2020/10/11
 */
public class DataTypeSize {
    public static void main(String[] args) {
        // byte
        System.out.println("1. byte 二进制位数：" + Byte.SIZE);
        System.out.println("Byte.MIN_VALUE=" + Byte.MIN_VALUE);
        System.out.println("Byte.MAX_VALUE=" + Byte.MAX_VALUE);
        System.out.println();

        // char
        System.out.println("2. char 二进制位数：" + Character.SIZE);
        // 以数值形式而不是字符形式将Character.MIN_VALUE输出到控制台
        System.out.println("Character.MIN_VALUE="
                + (int) Character.MIN_VALUE);
        // 以数值形式而不是字符形式将Character.MAX_VALUE输出到控制台
        System.out.println("Character.MAX_VALUE="
                + (int) Character.MAX_VALUE);

        // short
        System.out.println("3. short 二进制位数：" + Short.SIZE);
        System.out.println("Short.MIN_VALUE=" + Short.MIN_VALUE);
        System.out.println("Short.MAX_VALUE=" + Short.MAX_VALUE);
        System.out.println();

        // int
        System.out.println("4. int 二进制位数：" + Integer.SIZE);
        System.out.println("Integer.MIN_VALUE=" + Integer.MIN_VALUE);
        System.out.println("Integer.MAX_VALUE=" + Integer.MAX_VALUE);
        System.out.println();

        // float
        System.out.println("5. float 二进制位数：" + Float.SIZE);
        System.out.println("Float.MIN_VALUE=" + Float.MIN_VALUE);
        System.out.println("Float.MAX_VALUE=" + Float.MAX_VALUE);
        System.out.println();

        // double
        System.out.println("6. double 二进制位数：" + Double.SIZE);
        System.out.println("Double.MIN_VALUE=" + Double.MIN_VALUE);
        System.out.println("Double.MAX_VALUE=" + Double.MAX_VALUE);
        System.out.println();

        // long
        System.out.println("7. long 二进制位数：" + Long.SIZE);
        System.out.println("Long.MIN_VALUE=" + Long.MIN_VALUE);
        System.out.println("Long.MAX_VALUE=" + Long.MAX_VALUE);
        System.out.println();

        // boolean
        System.out.println("8. boolean 二进制位数 ？");
        System.out.println();

        // String
        System.out.println("String 类型的特点 ？");
        System.out.println();
    }
}
