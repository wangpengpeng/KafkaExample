package com.jasongj.kafka.consumer;

import com.jasongj.kafka.ConstantConf;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class DemoConsumerBase {

	public static void main(String[] args) {
		args = new String[] { ConstantConf.KAFKA_BROKER, ConstantConf.TOPIC, "group239", "consumer2" };

		if (args == null || args.length != 4) {
			System.err.println(
					"Usage:\n\tjava -jar kafka_consumer.jar ${bootstrap_server} ${topic_name} ${group_name} ${client_id}");
			System.exit(1);
		}
		String bootstrap = args[0];
		String topic = args[1];
		String groupid = args[2];

		Properties props = new Properties();
		props.put("bootstrap.server", bootstrap);
		props.put("group.id", groupid);
//		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
		props.put("auto.offset.reset", "earliest");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

		consumer.subscribe(Arrays.asList(topic));
		int idx =0;
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			records.forEach(record -> {
				System.out.printf("topic: %s , partition: %d , offset = %d, key = %s, value = %s%n", record.topic(),
						record.partition(), record.offset(), record.key(), record.value());
			});
			idx++;
			if(idx == 2){
//				break;
			}
		}

//		consumer.close();

	}

}
