package com.melita_task.contract.events;

import com.melita_task.contract.ClientDto;
import com.melita_task.contract.enums.EventTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
public class SubmitOrdersEventDto extends EventDto {

    @Getter
    private final ClientDto client;

    public SubmitOrdersEventDto(final ClientDto client) {
        super(EventTypes.ORDER_SUBMIT);
        this.client = client;
    }

    public SubmitOrdersEventDto(final EventTypes type, final ClientDto client) {
        super(EventTypes.ORDER_SUBMIT);
        this.client = client;
    }

}
