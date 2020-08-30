################################################################################
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
# limitations under the License.
################################################################################

import random
import time, calendar
from random import randint
from kafka import KafkaProducer
from json import dumps
from time import sleep

'''
1. python -m pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple 本地
2. Copy: cp -rf /Users/jincheng/work/knowhkw/khkw/No40-log-example-python/* ~/flinkDeploy/
3. 启动所有环境 docker-compose up -d
4. http://localhost:4000/#/overview 
5. 测试数据发送，启动kafka消费，
  cd /Users/jincheng/work/knowhkw/khkw/No40-log-example-python
  docker-compose exec kafka kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic payment_msg 
5. 发送数据： 
    cd /Users/jincheng/work/knowhkw/khkw/No40-log-example-python
    安装依赖：
    docker-compose exec jobmanager python -m pip install -r /opt/flinkDeploy/requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
    启动：
    docker-compose exec jobmanager python /opt/flinkDeploy/data_gen.py
    如果能够成数据发送，那么我们就停止上面的消息消费，启动作业。
6. 启动作业。
cd /Users/jincheng/work/knowhkw/khkw/No40-log-example-python
docker-compose exec jobmanager flink run -py  /opt/flinkDeploy/log_example.py

7. 启动数据生成： docker-compose exec jobmanager python /opt/flinkDeploy/data_gen.py
8. 查看作业运行。
'''

def write_data():
    data_cnt = 20000
    order_id = calendar.timegm(time.gmtime())
    max_price = 1000

    topic = "payment_msg"
    producer = KafkaProducer(bootstrap_servers=['kafka:9092'],
                             value_serializer=lambda x: dumps(x).encode('utf-8'))

    for i in range(data_cnt):
        ts = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
        rd = random.random()
        order_id += 1
        pay_amount = max_price * rd
        pay_platform = 0 if random.random() < 0.9 else 1
        province_id = randint(0, 6)
        cur_data = {"createTime": ts, "orderId": order_id, "payAmount": pay_amount, "payPlatform": pay_platform, "provinceId": province_id}
        producer.send(topic, value=cur_data)
        sleep(0.5)


if __name__ == '__main__':
    write_data()