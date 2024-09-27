package com.melita_task.api.amqp;

import com.melita_task.contract.events.CreateClientEventDto;
import com.melita_task.contract.events.SubmitOrdersEventDto;
import com.melita_task.contract.events.UpdateClientEventDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableBinding(AMQPBindings.class)
@AllArgsConstructor
public class MessageProducer {

	@Autowired
	private AMQPBindings cs;

	public void sendEvent(CreateClientEventDto createClientEventDto){
		log.debug("Send Rabbit Message On Create Exchange with type [{}]", createClientEventDto.getType().toString());
		cs.crmQueueCreate().send(MessageBuilder.withPayload(createClientEventDto).build());
	}

	public void sendEvent(UpdateClientEventDto updateClientEventDto){
		log.debug("Send Rabbit Message On Update Exchange with type [{}]", updateClientEventDto.getType().toString());
		cs.crmQueueUpdate().send(MessageBuilder.withPayload(updateClientEventDto).build());
	}

	public void sendEvent(SubmitOrdersEventDto submitOrdersEventDto){
		log.debug("Send Rabbit Message On Submit Exchange with type [{}]", submitOrdersEventDto.getType().toString());
		cs.crmQueueSubmit().send(MessageBuilder.withPayload(submitOrdersEventDto).build());
	}

}