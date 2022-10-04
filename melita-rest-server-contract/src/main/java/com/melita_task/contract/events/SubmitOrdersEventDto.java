package com.melita_task.contract.events;

import com.melita_task.contract.OrderDto;
import com.melita_task.contract.enums.EventTypes;
import lombok.Getter;

public class SubmitOrdersEventDto extends EventDto {

    @Getter
    private final OrderDto order;

    public SubmitOrdersEventDto(final OrderDto order) {
        super(EventTypes.ORDER_SUBMIT);
        this.order = order;
    }

}
