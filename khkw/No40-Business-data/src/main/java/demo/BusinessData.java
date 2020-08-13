package demo;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - demo
 * 功能描述:
 * 1. 利用当前项目中的docker-compose.yml启动kafka服务: docker-compose up -d
 * 2. 命令行执行：docker-compose exec kafka kafka-console-consumer.sh --bootstrap-server kafka:9092 -from-beginning --topic payment_msg
 * 3. 运行本程序...看到消息可以发送和消费,当然你也可以用No40-log-example的Kafka2Print进行测试接收消息
 * 4. 停止docker容器 docker-compose down, 打开dataGen()
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2020/8/13
 */
public class BusinessData {
    private static Random rand = new Random();
    private static int count = 20000;
    private static Logger LOG = LoggerFactory.getLogger(BusinessData.class);

    public static void main(String[] args) throws Exception {
//        test();
        dataGen();
    }

    public static void dataGen() throws InterruptedException {
        for (int i = 0; i < count; i++) {
            try {
                final int platform = rand.nextInt(2);
                final OrderMessage orderMessage = (platform == 0 ? OrderMessage.createTbaoMessage() : OrderMessage.createTmallMessage());
                orderMessage.setCreateTime(System.currentTimeMillis());
                PaymentMessage paymentMessage = PaymentMessage.createPaymentMsg(orderMessage);
                LOG.info(JSON.toJSONString(paymentMessage));
            } catch (Exception e) {
                continue;
            }
            Thread.sleep(500);
        }
    }

    public static void test() {
        for (int i = 0; i < 10; i++) {
            LOG.info("{\"msg\": \"welcome flink users...\"}");
        }
    }
}
