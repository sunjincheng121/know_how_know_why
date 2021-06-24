package serdeser.network;

import serdeser.network.fixedlen.FixedLenWorker;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - Ser/DeSer
 * 功能描述: 测试代码，仅限于示例，不能投产
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2021/06/24
 */
public class SocketServer {
    public static void main(String[] args) throws Exception {
        ServerSocket ss = null;
        ss = new ServerSocket(7521);
        while (true) {
            final Socket socket = ss.accept();
//            new ObjectWorker(socket).run();
            new BytesWorker(socket).run();
//            new FixedLenWorker(socket).run();
        }
    }
}