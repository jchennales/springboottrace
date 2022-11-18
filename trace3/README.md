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