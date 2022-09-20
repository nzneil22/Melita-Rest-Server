package com.melita_task.api.amqp;

import com.melita_task.api.models.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePayload {
    private String alteration;
    private Client client;
}
