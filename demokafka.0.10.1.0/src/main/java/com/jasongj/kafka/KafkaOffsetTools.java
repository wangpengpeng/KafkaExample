package com.jasongj.kafka;

import kafka.api.OffsetRequest;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;

import java.util.*;

/**
 * 获取kafka的offset信息。
 *
 */

public class KafkaOffsetTools {

    public static void main(String[] args) {

//        String topic = "e_system_events";
//        String topic = "e_page_open_entertainmentpage";
        String topic = "kafkaProducer5";
        int port = 9092;

        List<String> brokerList = new ArrayList<String>();
        brokerList.add("gtdata-test04");
        brokerList.add("gtdata-test05");
        brokerList.add("gtdata-test06");

        KafkaOffsetTools kot = new KafkaOffsetTools();
        TreeMap<Integer,PartitionMetadata> metadatas = kot.findLeader(brokerList, port, topic);

        int sum = 0;

        for (Map.Entry<Integer,PartitionMetadata> entry : metadatas.entrySet()) {
            int partition = entry.getKey();
            String leadBroker = entry.getValue().leader().host();
            String clientName = "Client_" + topic + "_" + partition;
            SimpleConsumer consumer = new SimpleConsumer(leadBroker, port, 100000,64* 1024, clientName);
            long readOffset = getLogSize(consumer, topic, partition, OffsetRequest.LatestTime(), clientName);


            sum += readOffset;
//            同时算出偏移量
            System.out.println("partition: "+partition+" ,logSize: "+readOffset);
            if(consumer!=null)consumer.close();
        }
        System.out.println("总和："+sum);

    }


//    public static long getLastOffset(SimpleConsumer consumer, String topic,
    public static long getLogSize(SimpleConsumer consumer, String topic,
                                     int partition, long whichTime, String clientName) {

        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(   whichTime, 1));

        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
                requestInfo, kafka.api.OffsetRequest.CurrentVersion(),
                clientName);
        //获取 logsize大小
        OffsetResponse response = consumer.getOffsetsBefore(request);

        if (response.hasError()) {
            System.out.println("Error fetching data Offset Data the Broker. Reason: "
                            + response.errorCode(topic, partition));
            return 0;
        }
        long[] offsets = response.offsets(topic, partition);
        return offsets[0];
    }

    private TreeMap<Integer,PartitionMetadata> findLeader(List<String> a_seedBrokers, int a_port, String a_topic) {
        TreeMap<Integer, PartitionMetadata> map = new TreeMap<Integer, PartitionMetadata>();
        loop: for (String seed : a_seedBrokers) {
            SimpleConsumer consumer = null;
            try {
                consumer = new SimpleConsumer(seed, a_port, 100000, 64 * 1024,
                        "leaderLookup"+new Date().getTime());
                List<String> topics = Collections.singletonList(a_topic);
                TopicMetadataRequest req = new TopicMetadataRequest(topics);
                kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);

                List<TopicMetadata> metaData = resp.topicsMetadata();
                for (TopicMetadata item : metaData) {
                    for (PartitionMetadata part : item.partitionsMetadata()) {
                        map.put(part.partitionId(), part);
//						if (part.partitionId() == a_partition) {
//							returnMetaData = part;
//							break loop;
//						}
                    }
                }
            } catch (Exception e) {
                System.out.println("Error communicating with Broker [" + seed
                        + "] to find Leader for [" + a_topic + ", ] Reason: " + e);
            } finally {
                if (consumer != null)
                    consumer.close();
            }
        }
//		if (returnMetaData != null) {
//			m_replicaBrokers.clear();
//			for (kafka.cluster.Broker replica : returnMetaData.replicas()) {
//				m_replicaBrokers.add(replica.host());
//			}
//		}
        return map;
    }

}