package com.huntdreams.kafka.consumer;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * SimpleHLConsumer
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 16/3/10 上午8:01.
 */
public class SimpleHLConsumer {

    private final ConsumerConnector consumer;
    private final String topic;

    public SimpleHLConsumer(String zookeeper, String groupId, String topic) {
        consumer = kafka.consumer.Consumer
                .createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
        this.topic = topic;
    }

    private static ConsumerConfig createConsumerConfig(String zookeeper,
                                                       String groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeper);
        props.put("group.id", groupId);
        props.put("zookeeper.session.timeout.ms", "500");
        props.put("zookeeper.sync.time.ms", "250");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }

    public void testConsumer() {
        Map<String, Integer> topicMap = new HashMap<String, Integer>();
        // Define single thread for topic
        topicMap.put(topic, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreamsMap =
                consumer.createMessageStreams(topicMap);
        List<KafkaStream<byte[], byte[]>> streamList = consumerStreamsMap
                .get(topic);
        for (final KafkaStream<byte[], byte[]> stream : streamList) {
            ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();
            while (consumerIte.hasNext())
                System.out.println("Message from Single Topic :: "
                        + new String(consumerIte.next().message()));
        }
        if (consumer != null)
            consumer.shutdown();
    }

    public static void main(String[] args) {
        String zooKeeper = args[0];
        String groupId = args[1];
        String topic = args[2];
        SimpleHLConsumer simpleHLConsumer = new SimpleHLConsumer(
                zooKeeper, groupId, topic);
        simpleHLConsumer.testConsumer();
    }
}