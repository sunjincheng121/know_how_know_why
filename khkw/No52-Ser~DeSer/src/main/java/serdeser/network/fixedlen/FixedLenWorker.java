package serdeser.network.fixedlen;

import serdeser.Engineer;
import serdeser.network.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - Ser/DeSer
 * 功能描述: 测试代码，仅限于示例，不能投产
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2021/06/24
 */
public class FixedLenWorker implements Runnable {
    private Socket socket;

    public FixedLenWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        int blockSize = 2; //  just for test
        int dataSize = blockSize * 10; //  just for test
        InputStream is = null;
        OutputStream os = null;
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
            os.write("Welcome you..".getBytes());
            os.flush();

            byte[] lenBytes = new byte[ByteUtils.intToBytes(1).length];
            int len = 0;
            while (-1 != (len = is.read(lenBytes))) {
                len = ByteUtils.bytesToInt(lenBytes);
                switch (len) {
                    case 0:
                        is.read(lenBytes);
                        byte[] data = new byte[ByteUtils.bytesToInt(lenBytes)];
                        is.read(data);
                        Engineer e = new EngineerSerDeser().deserialize(data);
                        System.out.println("Receive Engineer info: ");
                        System.out.println(e.name);
                        System.out.println(e.tel);
                        System.out.println(e.age);
                        e.hello();
                        break;
                    case 1:
                        is.read(lenBytes);
                        data = new byte[ByteUtils.bytesToInt(lenBytes)];
                        is.read(data);
                        Manager m = new ManagerSerDeser().deserialize(data);
                        System.out.println("Receive Manager info: ");
                        System.out.println(m.name);
                        System.out.println(m.tel);
                        System.out.println(m.age);
                        m.hello();
                        break;
                    default:
                        System.out.println("UnKnow data type...");

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                is.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


