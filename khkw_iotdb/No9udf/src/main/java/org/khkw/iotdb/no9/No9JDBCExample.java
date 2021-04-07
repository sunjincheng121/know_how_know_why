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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.khkw.iotdb.no9;

import org.apache.iotdb.jdbc.Config;
import org.apache.iotdb.jdbc.IoTDBSQLException;

import java.sql.*;

public class No9JDBCExample {

  /**
   * 第一次运行 init(),如果一切顺利，正面环境和数据准备完成
   * 第二次运行，按照udf方法注释修改，iotdb-engine.properties，重新启动IoTDB实例， 打开 udf() 注释，运行。
   * 第三步骤  打开 plusOp() 注释，发现错误，目前还不支持在SELECT中直接进行 m1 + m2
   */
  public static void main(String[] args) throws Exception {
    Class.forName("org.apache.iotdb.jdbc.IoTDBDriver");
    try (Connection connection =
            DriverManager.getConnection("jdbc:iotdb://127.0.0.1:6667/", "root", "root");
        Statement statement = connection.createStatement()) {
        init(statement);
//      udf(statement);
//      plusOp(statement);
    } catch (IoTDBSQLException e) {
      e.printStackTrace();
    }
  }

  public static void init(Statement statement) throws Exception {
    setStorageGroup(statement);
    statement.execute(
            "CREATE TIMESERIES root.lemming.device1.m1 with datatype=FLOAT,encoding=RLE");
    statement.execute(
            "CREATE TIMESERIES root.lemming.device1.m2 with datatype=FLOAT,encoding=RLE");
    statement.execute("INSERT INTO root.lemming.device1(timestamp,m1, m2) VALUES (1,3333,4444)");

    ResultSet resultSet = statement.executeQuery("SELECT timestamp, m1 as m1, m2 as m2 FROM root.lemming.device1");
    outputResult(resultSet);
  }

  /**
   *  修改iotdb-engine.properties文件，如下：
   * # Uncomment following fields to configure the udf root directory.
   * # For Window platform, the index is as follows:
   * udf_root_dir=/Users/jincheng/work/know_how_know_why/khkw_iotdb/No9udf/target
   * # For Linux platform
   * # If its prefix is "/", then the path is absolute. Otherwise, it is relative.
   * index_root_dir=/Users/jincheng/work/know_how_know_why/khkw_iotdb/No9udf/target
   */
  public static void udf(Statement statement) {
    try{

      statement.execute("CREATE FUNCTION plus AS \"org.khkw.iotdb.no9.AddFunc\"");
      statement.execute("SHOW FUNCTIONS");
      ResultSet resultSet = statement.executeQuery("SELECT timestamp, plus(m1, m2) FROM root.lemming.device1");
      outputResult(resultSet);
    }catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void plusOp(Statement statement) {
    try{
      ResultSet resultSet = statement.executeQuery("SELECT timestamp, m1 + m2 FROM root.lemming.device1");
      outputResult(resultSet);
    }catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void setStorageGroup(Statement statement) throws Exception{
    try {
      statement.execute("SET STORAGE GROUP TO root.lemming");
    }catch (Exception e){
      statement.execute("DELETE STORAGE GROUP root.lemming");
      statement.execute("SET STORAGE GROUP TO root.lemming");
    }
  }

  private static void outputResult(ResultSet resultSet) throws SQLException {
    if (resultSet != null) {
      System.out.println("--------------------------");
      final ResultSetMetaData metaData = resultSet.getMetaData();
      final int columnCount = metaData.getColumnCount();
      for (int i = 0; i < columnCount; i++) {
        System.out.print(metaData.getColumnLabel(i + 1) + " ");
      }
      System.out.println();
      while (resultSet.next()) {
        for (int i = 1; ; i++) {
          System.out.print(resultSet.getString(i));
          if (i < columnCount) {
            System.out.print(", ");
          } else {
            System.out.println();
            break;
          }
        }
      }
      System.out.println("--------------------------\n");
    }
  }

}
