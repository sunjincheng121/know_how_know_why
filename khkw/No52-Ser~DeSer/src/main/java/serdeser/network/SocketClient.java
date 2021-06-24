package serdeser.network;

import serdeser.Engineer;

import java.io.InputStream;
import java.io.ObjectOutputStream;
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
public class SocketClient {
    public static void main(String[] args) throws Exception {
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 7521);
            is = socket.getInputStream();
            byte[] welcome = new byte[13];
            is.read(welcome);
            System.out.println(String.format("Connect message: %s ", new String(welcome)));

            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);
            Engineer e = new Engineer();
            e.name = "Sunny";
            e.age = 10;
            e.transientData = "transientData";
            e.tel = "18158190225";
            oos.writeObject(e);
        } finally {
            oos.close();
            is.close();
            os.close();
            socket.close();
        }
    }
}