package datatype;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - datatype
 * 功能描述: https://oracle-base.com/articles/misc/oracle-dates-timestamps-and-intervals#interval
 *
 * 操作步骤:
 * 1. 下载oracle docker镜像 docker pull registry.cn-hangzhou.aliyuncs.com/helowin/oracle_11g
 * 2. 下载利用 docker images 查看oracle的镜像的repository，便于创建容器。
 * registry.cn-hangzhou.aliyuncs.com/helowin/oracle_11g   latest              3fa112fd3642        4 years ago         6.85GB
 * 3. 创建容器 - docker run -d -p 1521:1521 --name myOracle registry.cn-hangzhou.aliyuncs.com/helowin/oracle_11g
 * 4. 进入容器镜像 -  docker exec -it myOracle bash
 * 5. 编辑profile文件，su root 密码 helowin , vi /etc/profile
     export ORACLE_HOME=/home/oracle/app/oracle/product/11.2.0/dbhome_2
     export ORACLE_SID=helowin
     export PATH=$ORACLE_HOME/bin:$PATH
 *    保存之后，  source /etc/profile
 * 6. 创建软链 - ln -s $ORACLE_HOME/bin/sqlplus /usr/bin
 * 7. 回到 oracle用户， su oracle
 * 8. 登陆： sqlplus /nolog
 *   a. sqlplus /nolog
 *   b. conn /as sysdba
 *  9. sql plus 的使用参考 https://www.oracle.com/database/technologies/appdev/sqldeveloper-landing.html
 *  10. 查看一下所有数据库信息 select  *  from  v$instance;
 *  11. 创建测试表： create table stu( stuno number(4) not null) 如果一切正常，我们就可以开始今天的Declaration测试。
 *  12. 创建一个促销表， duration 天 精度为2, 秒 精度为5, 就是天存储2位数字, 秒最多可以在小数点后面有5位数字
 *      CREATE TABLE interval_tab ( duration INTERVAL DAY(2) TO SECOND (5) );
 *  13. 插入数据:
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '3' DAY);
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '3' DAY);
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '2' HOUR);
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '15' MINUTE);
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '25' SECOND);
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '3 2:25' DAY TO MINUTE);
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '3 2:25:45' DAY TO SECOND);
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '23 2:25:45.12' DAY(2) TO SECOND(2));
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '23 2:25:45.12' DAY(2) TO SECOND);
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '23 2:25:45.12324' DAY(2) TO SECOND(2)); // 只取了12为妙精度
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '23 2:25:45.123245' DAY(2) TO SECOND(6)); // 四舍五入了
 *    INSERT INTO interval_tab (duration) VALUES (INTERVAL '23 2:25:45.123242' DAY(2) TO SECOND(6));
 *    INSERT INTO interval_tab SELECT interval '5' day + interval '10' second FROM dual;
 *
 *    // 应用在什么场景呢？问你进入公司多少年了? 这个很容易答出来,问你进入公司多少天了? 这个就比较麻烦了，因为月份不同，天数不同，还涉及到闰年
 *    // 阿里内网有一个很好的信息，就是"亲爱的，这是你在阿里的第 3359 天" (2020。10.28)
 *
 *    CREATE TABLE  employee ( eid INT, hiredate DATE, PRIMARY KEY (eid));
 *    CREATE TABLE  employee_seniority ( eid INT, seniority INTERVAL DAY(6) TO SECOND(3) , PRIMARY KEY (eid));
 *
 *    // 插入我入职阿里的信息
 *    INSERT INTO employee(eid, hiredate) VALUES (1, to_date('2011-08-19','yyyy-mm-dd')) ;
 *
 *    // 计算入职多少天
 *    INSERT INTO employee_seniority SELECT eid, (sysdate - hiredate) DAY(6) TO SECOND(3) time FROM employee;
 *
 *    // 还有一个时间间隔的计算，比如 select sysdate + interval '5-11' YEAR TO MONTH from dual;
 *
 * 作者： 孙金城
 * 日期： 2020/10/28
 */
public class OracleDurationExample {
    public static void main(String[] args) {

    }
}
