使用说明：
1. 如果你还没有docker，请参考：https://www.docker.com/get-started
2. 当你完成docker环境之后，本示例需要按照Kafka
  a) 安装kafka镜像
  `docker pull wurstmeister/kafka`
  
  b)安装zookeeper镜像
  `docker pull wurstmeister/zookeeper`
  
  c)运行zookeeper容器
  `docker run -d --name zookeeper  -p 2181:2181 -t wurstmeister/zookeeper`
  
  d)运行kafka容器
  ```
    docker run -d --name kafka --publish 9092:9092 \
    --link zookeeper \
    --env KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
    --env KAFKA_ADVERTISED_HOST_NAME=127.0.0.1 \
    --env KAFKA_ADVERTISED_PORT=9092 \
    wurstmeister/kafka
   ```

3. 环境安装完成，发送/接受消息测试
   
   a) 发送数据
   ```
   docker exec -it kafka /bin/bash
   /opt/kafka/bin/kafka-console-producer.sh --topic=test --broker-list localhost:9092
   ```
   b) 接收数据
   ```
   docker exec -it kafka /bin/bash
   /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 -from-beginning --topic test
   ```

