
package com.melita_task.api.amqp;

import org.springframework.messaging.MessageChannel;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.stereotype.Service;

@Service
public interface AMQPBindings {

    @Output("crmQueue")
    MessageChannel crmQueueSend();

}