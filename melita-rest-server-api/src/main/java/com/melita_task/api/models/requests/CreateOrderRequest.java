package com.melita_task.api.models.requests;

import com.melita_task.contract.enums.LobTypes;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Validated
public class CreateOrderRequest {

    @Valid
    @NotNull
    private final Integer serviceId;

    @Valid
    @NotNull
    private final LobTypes lobType;

    @Valid
    @NotNull
    private final Date installationDateAndTime;
}
