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
   
   a) 进入命令行
   `docker exec -it kafka /bin/bash`
   
   b) 查看现有Topic
   `/opt/kafka/bin/kafka-topics.sh --zookeeper zookeeper:2181 --list`
   
   c) 如果没有你需要的，创建一个,2个分区
   ```
   /opt/kafka/bin/kafka-topics.sh --zookeeper zookeeper:2181 --create --topic checkIdle --partitions 2 --replication-factor 1
   ```
   
4. MySQL 5.7 环境和结果表
 a) 安装
`docker pull mysql:5.7`

 b) 运行MySQL
 `docker run -p 3306:3306 --name flink_mysql -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.7`
 
 c) 连接
 `docker exec -it flink_mysql bash` 
 `mysql -h localhost -u root -p`
 d) 创建数据库和表
 ```
create database flink_db
use flink_db

CREATE TABLE tab_string_long( id VARCHAR(20), win_start TIMESTAMP(3), cnt BIGINT,  PRIMARY KEY ( id, win_start ));
```

5. 生产数据
a) ProducerTestData 会向topic2个partition中谢数据，其中first partition数据很少，进而造成idle的
问题。
b) 运行 DiscoverIdlePartition，消费数据并且定义5秒钟的tumble窗口。

6. 如果你上面的步骤没有成功，注意关注我《Apache Flink知其然，知其所以然》视频课程，里面会有视频演示。



