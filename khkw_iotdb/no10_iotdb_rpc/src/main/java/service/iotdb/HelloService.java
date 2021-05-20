package service.iotdb;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

public class HelloService {
    public static void main(String[] args) throws TTransportException {

        TServerSocket socket = new TServerSocket(7521);
        TBinaryProtocol.Factory proFactory = new TBinaryProtocol.Factory();
        TProcessor processor = new Hello.Processor(new HelloImpl());
        TThreadPoolServer.Args arg1 = new TThreadPoolServer.Args(socket);
        arg1.processor(processor);
        arg1.protocolFactory(proFactory);

        TServer server = new TThreadPoolServer(arg1);
        System.out.println("Start server on port 7521 at 2021.05.20 :) ...");
        server.serve();
    }
}
