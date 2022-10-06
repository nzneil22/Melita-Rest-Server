package com.melita_task.contract.events;

import com.melita_task.contract.ClientDto;
import com.melita_task.contract.enums.EventTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
public class CreateClientEventDto extends EventDto {

    @Getter
    private final ClientDto client;

    public CreateClientEventDto(final ClientDto client) {
        super(EventTypes.CLIENT_CREATED);
        this.client = client;
    }

    public CreateClientEventDto(final EventTypes type, final ClientDto client){
        super(EventTypes.CLIENT_CREATED);
        this.client = client;
    }

}