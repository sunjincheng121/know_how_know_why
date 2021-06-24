package serdeser.network;

import serdeser.Engineer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
public class ObjectWorker implements Runnable {
    private Socket socket;

    public ObjectWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        ObjectInputStream ois = null;
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
            os.write("Welcome you..".getBytes());
            os.flush();

            ois = new ObjectInputStream(is);
            Object object = ois.readObject();
            if (object instanceof Engineer) {
                Engineer e = (Engineer) object;
                System.out.println(e.name);
                System.out.println(e.tel);
                System.out.println(e.age);
                System.out.println(e.transientData);
                e.hello();
            } else {
                System.out.println("Unknow object..");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
                os.close();
                is.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}