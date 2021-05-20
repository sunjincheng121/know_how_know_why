import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import service.iotdb.Hello;

public class HelloClient {
    public static void main(String[] args) throws Exception {
        TTransport transport = new TSocket("localhost", 7521);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        Hello.Client client = new Hello.Client(protocol);

        String resp = client.helloString("Hi 简单/平凡/晴天...;");
        System.out.println(resp);
        transport.close();
    }
}
