package com.melita_task.api.amqp;

import com.melita_task.api.models.Client;
import com.melita_task.contract.ClientDtoRabbit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePayload {
    private String alteration;
    private ClientDtoRabbit client;
}
