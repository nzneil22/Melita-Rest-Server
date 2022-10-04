package com.melita_task.contract.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melita_task.contract.enums.LobTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import java.util.Date;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequestDto {

    @Valid
    private Integer serviceId;

    @Valid
    private LobTypes lobType;

    @Valid
    @FutureOrPresent
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private Date installationDate;

}
