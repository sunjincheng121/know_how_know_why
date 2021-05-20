package service.iotdb;

import org.apache.thrift.TException;

public class HelloImpl implements Hello.Iface{
    @Override
    public String helloString(String para) throws TException {
        return "[520] - " + para;
    }

    @Override
    public int helloInt(int para) throws TException {
        return para;
    }
}
