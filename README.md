# Flume相关配置

## Flume 日志模拟
```
java -jar LogGenerator.jar /Users/noprom/Documents/Dev/Spark/servers/node-1/appserver-2/logs/debug.log  /Users/noprom/Documents/Dev/Spark/servers/node-1/appserver-1/logs/debug.log
```

## 开启一个Flume agent
```
./bin/flume-ng agent --conf conf --conf-file conf/spark-flume.conf --name a1
```

# Flume 与 Kafka 对接

##  

# Kafka 配置

## 开启zookeeper

```
# 1.单个zookeeper节点
bin/zookeeper-server-start.sh config/zookeeper.properties
```

## 开启broker
```
# 1.开启单个broker
bin/kafka-server-start.sh config/server.properties
# 2.开启多个broker
bin/kafka-server-start.sh config/server-1.properties
bin/kafka-server-start.sh config/server-2.properties
```

## 创建topic
```
# 1.单个replica
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic kafkatopic
# 2.多个replica
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic replicated-kafkatopic

# 列出当前topic
bin/kafka-topics.sh --list --zookeeper localhost:2181 kafkatopic
```

## 获得topic详情
```
bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic kafkatopic
```

## 创建多个replica
```
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic replicatedkafkatest
```

## 生产消息
```
＃ 1.单个broker
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic kafkatopic
# 2.多个broker
bin/kafka-console-producer.sh --broker-list localhost:9092, localhost:9093 --topic replicated-kafkatopic
```

## 消费数据
```
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic kafkatopic --from-beginning
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic replicated-kafkatopic --from-beginning
```

# Spark 配置

## 提交job
```
cd /usr/local/spark
./bin/spark-submit --class com.huntdreams.spark.UserClickCountAnalytics \
    --master spark://nopromdeMacBook-Pro.local:7077 \
    --executor-memory 1G --total-executor-cores 2 \
    --packages "org.apache.spark:spark-streaming-kafka_2.10:1.6.0,redis.clients:jedis:2.8.0" \
    /Users/noprom/Documents/Dev/Kafka/Pro/LearningKafka/out/artifacts/UserClickCountAnalytics_jar/LearningKafka.jar
```

# Kafka 和 Spark Streaming 整合
```
# 启动kafka
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
bin/kafka-server-start.sh config/server-1.properties
bin/kafka-server-start.sh config/server-2.properties

# 创建App统计的topic
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 2 --partitions 2 --topic user_events
bin/kafka-topics.sh --list --zookeeper localhost:2181 user_events

# 消费数据
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic user_events --from-beginning

# 提交spark job
./bin/spark-submit --class com.huntdreams.spark.UserClickCountAnalytics \
    --master spark://nopromdeMacBook-Pro.local:7077 \
    --executor-memory 1G --total-executor-cores 2 \
    --packages "org.apache.spark:spark-streaming-kafka_2.10:1.6.0,redis.clients:jedis:2.8.0" \
    /Users/noprom/Documents/Dev/Kafka/Pro/LearningKafka/out/artifacts/UserClickCountAnalytics_jar/LearningKafka.jar
```