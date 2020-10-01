FROM flink:1.11.2

ARG FLINK_VERSION=1.11.2

RUN set -ex; \
  apt-get update; \
  apt-get -y install python3; \
  apt-get -y install python3-pip; \
  apt-get -y install python3-dev; \
  ln -s /usr/bin/python3 /usr/bin/python; \
  ln -s /usr/bin/pip3 /usr/bin/pip; \
  apt-get update; \
  python -m pip install --upgrade pip; \
  pip install apache-flink==${FLINK_VERSION}; \
  wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-json/${FLINK_VERSION}/flink-json-${FLINK_VERSION}.jar; \
  wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-csv/${FLINK_VERSION}/flink-csv-${FLINK_VERSION}.jar; \
  wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-avro/${FLINK_VERSION}/flink-avro-${FLINK_VERSION}.jar; \
  wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-connector-jdbc_2.11/${FLINK_VERSION}/flink-connector-jdbc_2.11-${FLINK_VERSION}.jar; \
  wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/5.1.40/mysql-connector-java-5.1.40.jar; \
  wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-connector-hive_2.11/${FLINK_VERSION}/flink-connector-hive_2.11-${FLINK_VERSION}.jar; \
  wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-hbase_2.11/1.10.1/flink-hbase_2.11-1.10.1.jar; \
  wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-connector-hive_2.11/${FLINK_VERSION}/flink-connector-hive_2.11-${FLINK_VERSION}.jar; \
  wget -P /opt/flink/lib/ https://repo1.maven.org/maven2/org/apache/hive/hive-exec/2.3.4/hive-exec-2.3.4.jar; \
  wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka-0.11_2.11/${FLINK_VERSION}/flink-sql-connector-kafka-0.11_2.11-${FLINK_VERSION}.jar; \
  wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-elasticsearch7_2.11/${FLINK_VERSION}/flink-sql-connector-elasticsearch7_2.11-${FLINK_VERSION}.jar; \
  echo "taskmanager.memory.jvm-metaspace.size: 512m" >> /opt/flink/conf/flink-conf.yaml; \
  echo "rest.port: 4000" >> /opt/flink/conf/flink-conf.yaml; \
  echo "metrics.reporters: prom" >> /opt/flink/conf/flink-conf.yaml; \
  echo "metrics.reporter.prom.class: org.apache.flink.metrics.prometheus.PrometheusReporter" >> /opt/flink/conf/flink-conf.yaml; \
  echo "metrics.reporter.prom.port: 9249,9250" >> /opt/flink/conf/flink-conf.yaml


WORKDIR /opt/flink