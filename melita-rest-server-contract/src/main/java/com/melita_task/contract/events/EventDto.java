package com.melita_task.contract.events;

import com.melita_task.contract.enums.EventTypes;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventDto {
    private final EventTypes type;
}
