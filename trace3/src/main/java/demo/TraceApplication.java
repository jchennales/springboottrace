package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.jms.JmsTracing;
import brave.propagation.ThreadLocalCurrentTraceContext;

@SpringBootApplication
@EnableJms
@EnableFeignClients
public class TraceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraceApplication.class, args);
	}

	@Bean
	public Tracing awsTracing() {
		return Tracing.newBuilder()
				.currentTraceContext(
						ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(MDCScopeDecorator.get()).build())
				.build();
	}

	@Bean
	public JmsTracing jmsTracing(Tracing tracing) {
		return JmsTracing.newBuilder(tracing).build();
	}

}
