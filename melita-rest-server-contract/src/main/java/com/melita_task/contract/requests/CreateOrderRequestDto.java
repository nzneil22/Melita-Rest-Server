package com.melita_task.contract.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melita_task.contract.enums.LobTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {

    @NotNull
    private Integer serviceId;

    @NonNull
    private LobTypes lobType;

    @NonNull
    @FutureOrPresent
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private Date installationDateAndTime;

}
