package com.huntdreams.kafka.producer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Date;
import java.util.Properties;

/**
 * SimpleProducer
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 16/3/9 上午8:20.
 */
public class SimpleProducer {
    private static Producer<String, String> producer;

    public SimpleProducer() {
        Properties props = new Properties();
        // Set the broker list for requesting metadata to find the lead broker
        props.put("metadata.broker.list", "localhost:9092, localhost:9093, " +
                "localhost:9094");
        // This specifies the serializer class for keys
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        // 1 means the producer receives an acknowledgment once the lead replica
        // has received the data. This option provides better durability as the
        // client waits until the server acknowledges the request as successful.
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
    }

    public static void main(String[] args) {
        int argsCount = args.length;
//        if (argsCount == 0 || argsCount == 1)
//            throw new IllegalArgumentException(
//                    "Please provide topic name and Message count as arguments");
        // Topic name and the message count to be published is passed from the
        // command line
        String topic =
//                "kafkatopic";
                (String) args[0];
        String count =
//                "100000";
                (String) args[1];
        int messageCount = Integer.parseInt(count);
        System.out.println("Topic Name - " + topic);
        System.out.println("Message Count - " + messageCount);
        SimpleProducer simpleProducer = new SimpleProducer();
        simpleProducer.publishMessage(topic, messageCount);
    }

    private void publishMessage(String topic, int messageCount) {
        for (int mCount = 0; mCount < messageCount; mCount++) {
            String runtime = new Date().toString();
            String msg = "Message Publishing Time - " + runtime;
            System.out.println(msg);
            // Creates a KeyedMessage instance
            KeyedMessage<String, String> data =
                    new KeyedMessage<String, String>(topic, msg);
            // Publish the message
            producer.send(data);
        }
        // Close producer connection with broker.
        producer.close();
    }
}
