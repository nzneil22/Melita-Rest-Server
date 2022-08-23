# Melita-Rest-Server Spring Boot Micro-Service

Run RabbitMQ on Docker using command:
* `docker run --rm -it -p 15672:15672 -p 5672:5672 rabbitmq:3-management`

Create IntelliJ Configuration to run `RestServer.java` using Environment > VM Options 
* `-Dspring.cloud.config.enabled=true`

The component in the following git repo must already be running since it provides the cloud configuration for this component:
* https://github.com/nzneil22/Melita-Config-Server


Components fom the following repositories must also be running for the full workflow:
* https://github.com/nzneil22/Melita-Amqp-Server
* https://github.com/nzneil22/Melita-Message-Consumer
