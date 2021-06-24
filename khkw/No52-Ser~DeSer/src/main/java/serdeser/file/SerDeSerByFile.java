package serdeser.file;

import serdeser.Engineer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - Ser/DeSer
 * 功能描述: 测试代码，仅限于示例，不能投产
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2021/06/24
 */
public class SerDeSerByFile {
    public static void main(String[] args) throws Exception {
        String serPath = "/tmp/engineer.ser";
        Engineer e = new Engineer();
        e.name = "Sunny";
        e.age = 10;
        e.transientData = "transientData";
        e.tel = "18158190225";

        serialize(e, serPath);
        e.hello();

        Engineer e2 = deserialize(serPath);
        System.out.println(e2.name.equals(e.name));
        System.out.println(e2.tel.equals(e.tel));
        System.out.println(e2.age == e.age);
        System.out.println(e2.transientData == null);
        e2.hello();
    }

    public static void serialize(Engineer e, String serPath) throws Exception {
        FileOutputStream fileOut = new FileOutputStream(serPath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(e);
        out.close();
        fileOut.close();
        System.out.println("Serialized ...");
    }

    public static Engineer deserialize(String serPath) throws Exception {
        FileInputStream fileIn = new FileInputStream(serPath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Engineer e = (Engineer) in.readObject();
        in.close();
        fileIn.close();
        System.out.println("deserialized ...");
        return e;
    }
}
