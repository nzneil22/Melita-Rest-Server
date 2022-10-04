package com.melita_task.contract.events;

import com.melita_task.contract.OrderDto;
import com.melita_task.contract.enums.EventTypes;
import lombok.Getter;

public class UpdateOrderEventDto extends EventDto {

    @Getter
    private final OrderDto order;

    public UpdateOrderEventDto(final OrderDto order) {
        super(EventTypes.ORDER_UPDATED);
        this.order = order;
    }
}
