package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TraceController {
	
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;
	
	@Autowired
	private TraceService traceService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
	
	@GetMapping(value = "/say/{message}")
	public void say(@PathVariable String message) {
		log.info("say: {}", message);
	}

	@GetMapping(value = "/rest-say/{message}")
	public void restSay(@PathVariable String message) {
		log.info("rest-say: {}", message);
		RestTemplate restTemplate = restTemplateBuilder.build();
		restTemplate.getForEntity("http://127.0.0.1:8080/say/" + message, Object.class);
	}

	@GetMapping(value = "/feign-say/{message}")
	public void feignSay(@PathVariable String message) {
		log.info("feign-say: {}", message);
		traceService.say(message);
	}

	@GetMapping(value = "/jms-say/{message}")
	public void jmsSay(@PathVariable String message) {
		log.info("jms-say: {}", message);
		jmsTemplate.convertAndSend("queue", message);
	}

	@GetMapping(value = "/kafka-say/{message}")
	public void kafkaSay(@PathVariable String message) {
		log.info("kafka-say: {}", message);
		kafkaTemplate.send("topic", message);
	}

}
