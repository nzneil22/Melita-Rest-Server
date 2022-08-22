package com.melita_task.amqp;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface ClientSource {

    @Output("amqpMicroService")
    MessageChannel amqpMicroServiceSend();

}
