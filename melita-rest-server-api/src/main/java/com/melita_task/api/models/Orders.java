package com.melita_task.api.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melita_task.contract.LobTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @Column(columnDefinition = "char(36)")
    private String id = UUID.randomUUID().toString();

    private String customerUUID;

    @NotNull
    private Integer serviceId;

    @NotNull
    private LobTypes lobType;

    @NotNull
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private Date installationDateAndTime;

    private String status = "Created";
    public void updateOrder(final OrdersUpdate orderUpdate){
        if(nonNull(orderUpdate.getServiceId()))this.serviceId = orderUpdate.getServiceId();
        if(nonNull(orderUpdate.getLobType()))this.lobType = orderUpdate.getLobType();
        if(nonNull(orderUpdate.getInstallationDateAndTime()))this.installationDateAndTime = orderUpdate.getInstallationDateAndTime();
    }

}
