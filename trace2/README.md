# trace2  - Tracing prpoagation on Spring 2.7.5 with Sleuth

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

A sample log output is shown here. The trace id is properly propagated through all the channels with no explicit configuration in the app.

    Basic endpoint call:
    2022-11-18 09:34:09,767 INFO  demo.TraceController       [http-nio-8080-exec-5          ] [0f90036fb1dee6ed,0f90036fb1dee6ed] - say: hello
    Call through a RestTemplate
    2022-11-18 09:34:14,155 INFO  demo.TraceController       [http-nio-8080-exec-7          ] [0179fd23b92964d1,0179fd23b92964d1] - rest-say: hello
    2022-11-18 09:34:14,205 INFO  demo.TraceController       [http-nio-8080-exec-8          ] [0179fd23b92964d1,393d9e2b8ffc9e85] - say: hello
    Call through a Feign Client
    2022-11-18 09:34:19,551 INFO  demo.TraceController       [http-nio-8080-exec-10         ] [426c8bb3fb3fcc3a,426c8bb3fb3fcc3a] - feign-say: hello
    2022-11-18 09:34:19,557 INFO  demo.TraceController       [http-nio-8080-exec-1          ] [426c8bb3fb3fcc3a,e098d86df090cab0] - say: hello
    Call through a JMS message
    2022-11-18 09:34:22,495 INFO  demo.TraceController       [http-nio-8080-exec-3          ] [812b4ed968ce471b,812b4ed968ce471b] - jms-say: hello
    2022-11-18 09:34:22,520 INFO  demo.TraceJmsListener      [sListenerEndpointContainer#0-1] [812b4ed968ce471b,b169f904b50bcee8] - jms-listener-say: hello
    Call through a Kafka message
    2022-11-18 09:34:26,039 INFO  demo.TraceController       [http-nio-8080-exec-5          ] [d82c829109b56f87,d82c829109b56f87] - kafka-say: hello
    2022-11-18 09:34:26,046 INFO  demo.TraceKafkaConsumer    [tenerEndpointContainer#0-0-C-1] [d82c829109b56f87,9f1e4e1c99d7894e] - kafka-listener-say: hello

