package com.melita_task.melita;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
public class Service {

    @NotBlank
    private final Integer serviceId;

    @NotNull
    private final Services lobType;

    @NotNull
    private final Date installationDate;
}
