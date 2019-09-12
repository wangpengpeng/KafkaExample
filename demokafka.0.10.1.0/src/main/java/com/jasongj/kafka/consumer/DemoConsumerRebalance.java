package com.jasongj.kafka.consumer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import com.jasongj.kafka.ConstantConf;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

public class DemoConsumerRebalance {

	public static void main(String[] args) {
		args = new String[] { ConstantConf.KAFKA_BROKER, ConstantConf.TOPIC, "group1", "consumer1" };

		if (args == null || args.length != 4) {
			System.err.println(
					"Usage:\n\tjava -jar kafka_consumer.jar ${bootstrap_server} ${topic_name} ${group_name} ${client_id}");
			System.exit(1);
		}
		String bootstrap = args[0];
		String topic = args[1];
		String groupid = args[2];
		String clientid = args[3];

		Properties props = new Properties();
		props.put("bootstrap.servers", bootstrap);
		props.put("group.id", groupid);
		props.put("client.id", clientid);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(topic), new ConsumerRebalanceListener(){

			@Override
			public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
				partitions.forEach(topicPartition -> {
					System.out.printf("Revoked partition for client %s : %s-%s %n", clientid, topicPartition.topic(), topicPartition.partition());
				});
			}

			@Override
			public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
				partitions.forEach(topicPartition -> {
					System.out.printf("Assigned partition for client %s : %s-%s %n", clientid, topicPartition.topic(), topicPartition.partition());
				});
			}});
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			records.forEach(record -> {
				System.out.printf("client : %s , topic: %s , partition: %d , offset = %d, key = %s, value = %s%n", clientid, record.topic(),
						record.partition(), record.offset(), record.key(), record.value());
			});
		}
	}

}
