package com.huntdreams.kafka.partition;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 16/3/9 上午9:43.
 */

public class SimplePartitioner implements Partitioner {
    public SimplePartitioner(VerifiableProperties props) {

    }

    /**
     * The method takes the key, which in this case is the IP address,
     * It finds the last octet and does a modulo operation on the number
     * of partitions defined within Kafka for the topic.
     *
     * @param key
     * @param a_numPartitions
     * @return
     * @see kafka.producer.Partitioner#partition(java.lang.Object, int)
     */
    public int partition(Object key, int a_numPartitions) {
        int partition = 0;
        String partitionKey = (String) key;
        int offset = partitionKey.lastIndexOf('.');
        if (offset > 0) {
            partition = Integer.parseInt(partitionKey.substring(offset + 1))
                    % a_numPartitions;
        }
        return partition;
    }
}
