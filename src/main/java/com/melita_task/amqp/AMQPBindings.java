
package com.melita_task.amqp;

import org.springframework.messaging.MessageChannel;
import org.springframework.cloud.stream.annotation.Output;

public interface AMQPBindings {

    @Output("crmQueue")
    MessageChannel crmQueueSend();

}