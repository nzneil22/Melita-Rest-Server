package com.melita_task.contract.events;

import com.melita_task.contract.OrderDto;
import com.melita_task.contract.enums.EventTypes;
import lombok.Getter;

public class CreateOrderEventDto extends EventDto {

    @Getter
    private final OrderDto order;

    public CreateOrderEventDto(final OrderDto order) {
        super(EventTypes.ORDER_CREATED);
        this.order = order;
    }

}
