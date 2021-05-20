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

import org.apache.iotdb.jdbc.IoTDBSQLException;

import java.sql.*;

public class Funs {

  /**
   * 第一次运行 init(),如果一切顺利，正面环境和数据准备完成
   */
  public static void main(String[] args) throws Exception {
    Class.forName("org.apache.iotdb.jdbc.IoTDBDriver");
    try (Connection connection =
            DriverManager.getConnection("jdbc:iotdb://127.0.0.1:6667/", "root", "root");
        Statement statement = connection.createStatement()) {
//        init(statement);
        last(statement);
//        last_value(statement);
//        max(statement);
//        min(statement);
//      first(statement);
//      limit(statement);
//      count(statement);
//      avg(statement);
//      fill(statement);

    } catch (IoTDBSQLException e) {
      e.printStackTrace();
    }
  }

  public static void fill(Statement statement) throws Exception {
    System.out.println("fill======IoTDBGroupByFillIT");
    ResultSet resultSet = statement.executeQuery("SELECT m1,m2 FROM root.lemming.device1 where time = 3 fill(float[previous, 1m])");
    outputResult(resultSet);

    resultSet = statement.executeQuery("SELECT m1,m2 FROM root.lemming.device1 where time = 3 fill(float[previous, 1m])");
    outputResult(resultSet);
  }

  public static void count(Statement statement) throws Exception {
    System.out.println("count======");
    ResultSet resultSet = statement.executeQuery("SELECT count(m1),count(m2)  FROM root.lemming.device1");
    outputResult(resultSet);
  }

  public static void sample(Statement statement) throws Exception {
    System.out.println("sample======");
    ResultSet resultSet = statement.executeQuery("SELECT sample(m1,'method'='reservoir','k'='5')  FROM root.lemming.device1");
    outputResult(resultSet);
  }


  public static void avg(Statement statement) throws Exception {
    System.out.println("avg======");
    ResultSet resultSet = statement.executeQuery("SELECT avg(m1),avg(m2)  FROM root.lemming.device1");
    outputResult(resultSet);
  }

  public static void min(Statement statement) throws Exception {
    System.out.println("min======");
    ResultSet resultSet = statement.executeQuery("SELECT min_time(m1),min_value(m2)  FROM root.lemming.device1");
    outputResult(resultSet);
  }

  public static void max(Statement statement) throws Exception {
    System.out.println("max======");
    ResultSet resultSet = statement.executeQuery("SELECT max_time(m1),max_value(m2)  FROM root.lemming.device1");
    outputResult(resultSet);
  }

  public static void last_value(Statement statement) throws Exception {
    System.out.println("last======");
    ResultSet resultSet = statement.executeQuery("SELECT LAST_VALUE(m1), LAST_VALUE(m2)  FROM root.lemming.device1");
    outputResult(resultSet);
  }

  public static void first(Statement statement) throws Exception {
    System.out.println("first======");
    ResultSet resultSet = statement.executeQuery("SELECT FIRST_VALUE(m1), FIRST_VALUE(m2)  FROM root.lemming.device1");
    outputResult(resultSet);
  }

  public static void limit(Statement statement) throws Exception {
    System.out.println("first======");
    ResultSet resultSet = statement.executeQuery("SELECT m1, m2 FROM root.lemming.device1 limit 1");
    outputResult(resultSet);
  }

  public static void last(Statement statement) throws Exception {
    System.out.println("last======");
    ResultSet resultSet = statement.executeQuery("SELECT last m1, m2 FROM root.lemming.device1");
    outputResult(resultSet);
  }

  public static void init(Statement statement) throws Exception {
    setStorageGroup(statement);
    statement.execute(
            "CREATE TIMESERIES root.lemming.device1.m1 with datatype=FLOAT,encoding=RLE");
    statement.execute(
            "CREATE TIMESERIES root.lemming.device1.m2 with datatype=FLOAT,encoding=RLE");
    statement.execute("INSERT INTO root.lemming.device1(timestamp,m1, m2) VALUES (1,3333,4444)");

    statement.execute("INSERT INTO root.lemming.device1(timestamp,m1, m2) VALUES (2,32,42)");

    ResultSet resultSet = statement.executeQuery("SELECT timestamp, m1 as m1, m2 as m2 FROM root.lemming.device1");
    outputResult(resultSet);
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
