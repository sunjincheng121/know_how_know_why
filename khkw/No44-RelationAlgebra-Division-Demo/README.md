操作步骤：
1. docker-compose up -d
2. docker exec -it mysql /bin/bash
3. mysql -h localhost -u root -p
4. 创建数据库
    create database flinkdb;
    use flinkdb;
5. 创建表结构
  create table student ( sno VARCHAR(20), age INT(11), name VARCHAR(20));
  create table sc ( sno VARCHAR(20), cno VARCHAR(20), score INT(11));
  create table course ( cno VARCHAR(20), cname VARCHAR(20));
6. 创建初始数据
insert into student values('S001', 9, 'Sunny');
insert into student values('S002', 21, 'Kevin');
insert into student values('S003', 22, 'Eden');
insert into student values('S004', 29, 'Pan');

insert into course values('C001', 'English');
insert into course values('C002', 'Flink');
insert into course values('C003', 'Spark');

insert into sc values('S001','C001', 95);
insert into sc values('S001','C002', 90);
insert into sc values('S001','C003', 88);
insert into sc values('S002','C002', 85);
insert into sc values('S003','C001', 92);
insert into sc values('S003','C003', 90);

7. 执行查询
select * from student s where s.sno in (
    select distinct sno from sc A where not exists (
        select cno from course B where not exists (
            select * from sc C where C.sno=A.sno and C.cno = B.cno
        )
    )
);
查看执行计划：
explain select distinct sno from sc A where not exists (
           select cno from course B where not exists (
               select * from sc C where C.sno=A.sno and C.cno = B.cno
            )
       ); 


=====分解
1. 学生和选课的组合
create view V1 as select distinct Course.cno,SC.sno from Course, SC;
2. 查询没有全修所有课程的学生
create view V2 as select * from V1 where not exists (
     select * from SC where SC.sno = V1.sno and SC.cno = V1.cno
 );
3. 查询全修了所有课程的学生
create view V3 as select distinct sno from SC where not exists (
   select * from V2 where V2.sno = SC.sno
)
4. 查询学生信息
select * from student where exists (
   select * from V3 where V3.sno = student.sno
)


