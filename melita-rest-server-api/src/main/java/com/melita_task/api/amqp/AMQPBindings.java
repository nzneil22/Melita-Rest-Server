
package com.melita_task.api.amqp;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
public interface AMQPBindings {

    @Output("crmQueueCreate")
    MessageChannel crmQueueCreate();

    @Output("crmQueueUpdate")
    MessageChannel crmQueueUpdate();

    @Output("crmQueueSubmit")
    MessageChannel crmQueueSubmit();

}