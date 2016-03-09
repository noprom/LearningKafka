## 开启zookeeper

```
# 1.单个zookeeper节点
bin/zookeeper-server-start.sh config/zookeeper.properties
```

## 开启broker
```
# 1.开启单个broker
bin/kafka-server-start.sh config/server.properties &
# 2.开启多个broker
bin/kafka-server-start.sh config/server-1.properties &
bin/kafka-server-start.sh config/server-2.properties &
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

