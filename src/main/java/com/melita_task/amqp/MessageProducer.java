//package com.melita_task.amqp;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Service;
//
//@Service
//@EnableBinding(AMQPBindings.class)
//public class MessageProducer {
//
//	@Autowired
//	private AMQPBindings cs;
//
//	public void sendMessage(MessagePayload mp){
//		System.out.println("AMQP Send Message - " + mp.getAlteration());
//		cs.crmQueueSend().send(MessageBuilder.withPayload(mp).build());
//	}
//
//}