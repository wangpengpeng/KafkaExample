package com.jasongj.kafka.producer;

import java.util.Properties;

import com.jasongj.kafka.ConstantConf;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducerDemo {

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();

		props.put("bootstrap.servers", ConstantConf.KAFKA_BROKER);
		props.put("acks", "all");
		props.put("retries", 3);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", StringSerializer.class.getName());
		props.put("value.serializer", StringSerializer.class.getName());
		props.put("partitioner.class", HashPartitioner.class.getName());
		props.put("interceptor.classes", EvenProducerInterceptor.class.getName());

		System.out.println( EvenProducerInterceptor.class.getName() +  HashPartitioner.class.getName());
		Producer<String, String> producer = new KafkaProducer<String, String>(props);
		for (int i = 0; i < 10; i++)
			producer.send(new ProducerRecord<String, String>(ConstantConf.TOPIC, Integer.toString(i), Integer.toString(i)));
		producer.close();
	}

}
