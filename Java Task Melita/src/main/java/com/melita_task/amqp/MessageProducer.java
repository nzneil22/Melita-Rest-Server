package com.melita_task.amqp;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

	@Autowired
	private AmqpTemplate amqpTemplate;
	
	@Value("${rabbitmq.exchange}")
	private String exchange;
	
	@Value("${rabbitmq.routingkey}")
	private String routingkey;
	
	public void send(MessagePayload messagePayload) {
		String payload = messagePayload.getAlteration() +": "+ messagePayload.getCustomer().toString();
		amqpTemplate.convertAndSend(exchange, routingkey, payload);
		System.out.println("Send msg: " + payload);
	}
}