package serdeser.network.fixedlen;

import serdeser.Engineer;
import serdeser.network.ByteUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - Ser/DeSer
 * 功能描述: 测试代码，仅限于示例，不能投产
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2021/06/24
 */
public class FixedLenRMIClient {
    public static void main(String[] args) throws Exception {
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 7521);
            is = socket.getInputStream();
            byte[] welcome = new byte[13];
            is.read(welcome);
            System.out.println(String.format("Connect message: %s ", new String(welcome)));

            Engineer e = new Engineer();
            e.name = "Sunny";
            e.age = 10;
            e.transientData = "transientData";
            e.tel = "18158190225";
            EngineerSerDeser serDeser = new EngineerSerDeser();
            byte[] data = serDeser.serialize(e);
            os = socket.getOutputStream();
            os.write(ByteUtils.intToBytes(0)); // 表示 Engineer
            os.write(ByteUtils.intToBytes(data.length));
            os.write(data);

//            Manager m = new Manager();
//            m.name = "MSunny";
//            m.age = 20;
//            m.tel = "M18158190225";
//            ManagerSerDeser managerSerDeser = new ManagerSerDeser();
//            byte[] mData = managerSerDeser.serialize(m);
//            os.write(ByteUtils.intToBytes(1)); // 表示 Manager
//            os.write(ByteUtils.intToBytes(mData.length));
//            os.write(mData);

            os.flush();
        } finally {
            is.close();
            os.close();
            socket.close();
        }
    }
}