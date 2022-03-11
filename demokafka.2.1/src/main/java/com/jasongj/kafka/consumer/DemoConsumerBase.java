package com.jasongj.kafka.consumer;

import com.jasongj.kafka.ConstantConf;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.connect.json.JsonDeserializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class DemoConsumerBase {

	public static void main(String[] args) throws InterruptedException{
//		args = new String[] { ConstantConf.KAFKA_BROKER, ConstantConf.TOPIC, "group239", "consumer2" };

		if (args == null || args.length != 3) {
			System.err.println(
					"Usage:\n\tjava -jar kafka_consumer.jar ${bootstrap_server} ${topic_name} ${group_name}");
			System.exit(1);
		}
		String bootstrap = args[0];
		String topic = args[1];
		String groupid = args[2];

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
		props.put("group.id", groupid);
//		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
//		props.put("key.deserializer", StringDeserializer.class.getName());
//		props.put("value.deserializer", StringDeserializer.class.getName());

		props.put("key.deserializer", JsonDeserializer.class.getName());
		props.put("value.deserializer", JsonDeserializer.class.getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(topic));
		int idx =0;
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			records.forEach(record -> {
				System.out.printf("topic: %s , partition: %d , offset = %d, key = %s, value = %s%n", record.topic(),
						record.partition(), record.offset(), record.key(), record.value());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			Thread.sleep(2000);

		}

//		consumer.close();

	}

}
