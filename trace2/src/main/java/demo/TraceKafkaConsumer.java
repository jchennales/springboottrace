package demo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TraceKafkaConsumer {

	@KafkaListener(topics = "topic")
	public void kafkaListenerSay(String message) {
		log.info("kafka-listener-say: {}", message);
	}
	
}
