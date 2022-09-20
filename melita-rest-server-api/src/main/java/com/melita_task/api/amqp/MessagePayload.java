package com.melita_task.api.amqp;

import com.melita_task.api.models.Client;
import com.melita_task.api.models.Orders;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePayload {
    private String alteration;
    private Client client;
    private Orders order;
}
