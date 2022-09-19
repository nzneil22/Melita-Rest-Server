package com.melita_task.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @NonNull
    private Integer serviceId;

    @NonNull
    private LobTypes lobType;

    @NonNull
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private Date installationDateAndTime;

    @NonNull
    private String status;
}
