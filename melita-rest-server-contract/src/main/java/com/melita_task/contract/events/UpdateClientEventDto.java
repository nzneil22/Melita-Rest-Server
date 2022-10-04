package com.melita_task.contract.events;

import com.melita_task.contract.ClientDto;
import com.melita_task.contract.enums.EventTypes;
import lombok.Getter;

public class UpdateClientEventDto extends EventDto {

    @Getter
    private final ClientDto client;

    public UpdateClientEventDto(final ClientDto client) {
        super(EventTypes.CLIENT_UPDATED);
        this.client = client;
    }

}
