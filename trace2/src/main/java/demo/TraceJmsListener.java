package demo;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TraceJmsListener {

	@JmsListener(destination = "queue")
	public void listenerSay(String message) {
		log.info("jms-listener-say: {}", message);
	}
	
}
