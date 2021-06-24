package serdeser.network.fixedlen;

import serdeser.Engineer;
import serdeser.network.ByteUtils;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - Ser/DeSer
 * 功能描述: 测试代码，仅限于示例，不能投产
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2021/06/24
 */
public class EngineerSerDeser {
    public static void main(String[] args) {
        EngineerSerDeser serDeser = new EngineerSerDeser();

        Engineer e = new Engineer();
        e.name = "Sunny";
        e.age = 10;
        e.transientData = "transientData";
        e.tel = "18158190225";

        byte[] serData = serDeser.serialize(e);
        System.out.println(String.format("SerDataLen = [%d] ", serData.length));

        Engineer e2 = serDeser.deserialize(serData);
        System.out.println(e2.name);
        System.out.println(e2.tel);
        System.out.println(e2.age);
        e2.hello();

    }

    public byte[] serialize(Engineer engineer) {
        byte[] data = new byte[30];
        byte[] name = ByteUtils.stringToBytes(engineer.name);
        byte[] tel = ByteUtils.stringToBytes(engineer.tel);
        byte[] age = ByteUtils.intToBytes(engineer.age);
        int pos = 0;
        int intLen = ByteUtils.intToBytes(1).length;

        data = ByteUtils.append(ByteUtils.intToBytes(name.length), data, pos);
        pos += intLen;
        data = ByteUtils.append(name, data, pos);
        pos += name.length;
        data = ByteUtils.append(ByteUtils.intToBytes(tel.length), data, pos);
        pos += intLen;
        data = ByteUtils.append(tel, data, pos);
        pos += tel.length;
        data = ByteUtils.append(ByteUtils.intToBytes(age.length), data, pos);
        pos += intLen;
        data = ByteUtils.append(age, data, pos);
        int dataLen = pos + age.length;
        byte[] result = new byte[dataLen];
        System.arraycopy(data, 0, result, 0, dataLen);
        return result;
    }

    public Engineer deserialize(byte[] data) {
        Engineer engineer = new Engineer();
        int pos = 0;
        byte[] len = new byte[4];
        System.arraycopy(data, pos, len, 0, 4);
        byte[] name = new byte[ByteUtils.bytesToInt(len)];
        pos += len.length;
        System.arraycopy(data, pos, name, 0, name.length);

        pos += name.length;
        System.arraycopy(data, pos, len, 0, 4);
        byte[] tel = new byte[ByteUtils.bytesToInt(len)];
        pos += len.length;
        System.arraycopy(data, pos, tel, 0, tel.length);

        pos += tel.length;
        System.arraycopy(data, pos, len, 0, 4);
        byte[] age = new byte[ByteUtils.bytesToInt(len)];
        pos += len.length;
        System.arraycopy(data, pos, age, 0, age.length);

        engineer.name = ByteUtils.bytesToString(name);
        engineer.tel = ByteUtils.bytesToString(tel);
        engineer.age = ByteUtils.bytesToInt(age);
        return engineer;
    }
}
