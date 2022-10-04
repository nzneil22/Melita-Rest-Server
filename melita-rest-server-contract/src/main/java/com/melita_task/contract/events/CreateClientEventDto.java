package com.melita_task.contract.events;

import com.melita_task.contract.ClientDto;
import com.melita_task.contract.enums.EventTypes;
import lombok.Getter;

public class CreateClientEventDto extends EventDto {

    @Getter
    private final ClientDto client;

    public CreateClientEventDto(final ClientDto client) {
        super(EventTypes.CLIENT_CREATED);
        this.client = client;
    }

}
