/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cdc

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.scala._

/**
 * Test for sink data to Kafka with retract mode.
 */
object UpsertKafka {
  def main(args: Array[String]): Unit = {
    val sourceData = "file:///Users/jincheng.sunjc/work/know_how_know_why/QA/upsertKafka/src/main/scala/cdc/id_cnt_data.csv"
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = StreamTableEnvironment.create(env)

    val sourceDDL = "CREATE TABLE csvSource (" +
      "  id VARCHAR," +
      "  cnt INT" +
      ") WITH (" +
      "'connector.type' = 'filesystem'," +
      "'connector.path' = '" + sourceData + "'," +
      "'format.type' = 'csv'" +
      ")"

    val sinkDDL = "CREATE TABLE kafkaSink (" +
      "  id VARCHAR," +
      "  cnt INT " +
      ") WITH (" +
      "'connector.type' = 'kafka'," +
      "'connector.version' = '0.10'," +
      "'connector.topic' = 'test'," +
      "'connector.properties.zookeeper.connect' = 'localhost:2181'," +
      "'connector.properties.bootstrap.servers' = 'localhost:9092'," +
      "'connector.properties.group.id' = 'data_Group'," +
      "'format.type' = 'json')"

    tEnv.sqlUpdate(sourceDDL)
    tEnv.sqlUpdate(sinkDDL)

    val sql = "INSERT INTO kafkaSink SELECT id, SUM(cnt) FROM csvSource GROUP BY id"
    tEnv.sqlUpdate(sql)

    env.execute("RetractKafka")
  }
}
