package serdeser.network;

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
public class BytesWorker implements Runnable {
    private Socket socket;

    public BytesWorker(Socket socket) {
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

            byte[] block = new byte[blockSize];
            byte[] data = new byte[dataSize];
            int len = 0;
            int dataLen = 0;
            while (-1 != (len = is.read(block))) {
                data = ByteUtils.append(block, data, dataLen);
                dataLen += len;
            }
            System.out.println(String.format("The length of receive data is %d ", dataLen));
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


