# trace3  - Tracing prpoagation on Spring 3.0.0-RC2

JMS is embedded but you need a local Kafka node. The simplest thing will do:

- Download Kafka and open archive in a directory (ex: https://downloads.apache.org/kafka/3.3.1/kafka_2.13-3.3.1.tgz)
- In a shell in that directory do:
	- mkdir logs
	- bin/zookeeper-server-start.sh config/zookeeper.properties > logs/zookeper.run.log 2>&1 &
	- bin/kafka-server-start.sh config/server.properties > logs/kafka.run.log 2>&1 &
	There's more to Kafka but this will be enough to run this demo.
- Run the Spring Boot app
- Use the following cURL commands to showcase the trace propagation across different 

		curl 'http://localhost:8080/say/hello'
		curl 'http://localhost:8080/rest-say/hello'
		curl 'http://localhost:8080/feign-say/hello'
		curl 'http://localhost:8080/jms-say/hello'
		curl 'http://localhost:8080/kafka-say/hello'

---

So, this version starts as a simple copy of the adjacent trace2 project for Spring Boot 2.7.5

First wave of changes are:

	- Upgrade Spring Boot and Spring Cloud versions
	- Add milestone repositories (since 3.0 is RC atm)
	- Remove deprecated Sleuth starter
	- Replace classic ActiveMQ with Artemis + Server
	
After doing this the app works but all trace propagation is completely lost.

---

After reading online we see we need to add Micrometer Tracing (BOM + module + a tracing bridge) and Spring Boot Actuator.
Fine, we add all of this, using the brave bridge.
Now some of the logs have trace and span ids but not the JMS or Kafka listeners. Furthermore, the trace id is propagated through the RestTemplate but not through any of the other RPC mechanisms chosen.

	Basic endpoint call:
	2022-11-18 14:13:37,153 INFO  demo.TraceController       [http-nio-8080-exec-4          ] [6377bd419bb7e03d32bf6dc800fb6468,32bf6dc800fb6468] - say: hello
	Call through a RestTemplate
	2022-11-18 14:13:39,665 INFO  demo.TraceController       [http-nio-8080-exec-6          ] [6377bd43d48473ce2e3e9e3c9f0128e5,2e3e9e3c9f0128e5] - rest-say: hello
	2022-11-18 14:13:39,671 INFO  demo.TraceController       [http-nio-8080-exec-7          ] [6377bd43d48473ce2e3e9e3c9f0128e5,019afaa1ce764601] - say: hello
	Call through a Feign Client
	2022-11-18 14:13:44,992 INFO  demo.TraceController       [http-nio-8080-exec-9          ] [6377bd484d999470d61bc94e3c874b55,d61bc94e3c874b55] - feign-say: hello
	2022-11-18 14:13:44,999 INFO  demo.TraceController       [http-nio-8080-exec-10         ] [6377bd486e77151a42c0f31f5474cbd5,42c0f31f5474cbd5] - say: hello
	Call through a JMS message
	2022-11-18 14:14:07,620 INFO  demo.TraceController       [http-nio-8080-exec-1          ] [6377bd5f6ee30090b829b1e99fda09eb,b829b1e99fda09eb] - jms-say: hello
	2022-11-18 14:14:07,662 INFO  demo.TraceJmsListener      [sListenerEndpointContainer#0-1] [,] - jms-listener-say: hello
	Call through a Kafka message
	2022-11-18 14:14:21,270 INFO  demo.TraceController       [http-nio-8080-exec-4          ] [6377bd6d9e6d5cd307b94302beeec9b1,07b94302beeec9b1] - kafka-say: hello
	2022-11-18 14:14:21,372 INFO  demo.TraceKafkaConsumer    [tenerEndpointContainer#0-0-C-1] [,] - kafka-listener-say: hello

---

So, reading further. JMS has its own instrumentation module: "brave-instrumentation-jms" and so does Kafka "brave-instrumentation-kafka-clients". I have not yet found a similar instrumentation package for OpenFeign.
Adding these modules to the POM is not enough though. They don't appear to be covered by auto-configuration.

I tried removing them both and adding "brave-instrumentation-messaging". Same behavior as with both modules independently

Reading more I see tutorials defining beans for Tracing and JmsTracer. The "brave-instrumentation-jms" needs to be explicitly added for JmsTracer to be available.

