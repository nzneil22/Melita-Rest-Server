package com.melita_task.contract.events;

import com.melita_task.contract.enums.EventTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class EventDto {
    private final EventTypes type;
}
