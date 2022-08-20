package com.melita_task.melita;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer implements MessageListener {

    public void onMessage(Message message) {
        System.out.println("Melita Consumes Message - " + new String(message.getBody()));
    }

}