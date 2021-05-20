1. 创建 hello.thrift
2. 利用thrift生server文件
   $THRIFT_HOME/bin/thrift -gen java hello.thrift
   用tree命令查看生成的文件和目录机构如下：
   .
   ├── gen-java
   │ └── service
   │     └── iotdb
   │         └── Hello.java
   └── hello.thrift
   
3. 将生成文件copy到工程中，并创建HelloImpl实现Hello.IFace接口
4. 编写服务类：HelloService
5. 编写客户端：HelloClient
6. 启动Server/运行Cl  ient
