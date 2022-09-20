package com.melita_task.api.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melita_task.contract.LobTypes;
import com.melita_task.contract.OrderStatus;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Data
@Entity
@Builder
@Table(name="orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @Type(type = "uuid-char")
    @Column(columnDefinition = "char(36)")
    private final UUID id = UUID.randomUUID();

    @NotNull
    private Integer serviceId;

    @NotNull
    private LobTypes lobType;

    @NotNull
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private Date installationDateAndTime;

    private OrderStatus status = OrderStatus.CREATED;

    public void updateOrder(final OrdersUpdate orderUpdate){
        if(nonNull(orderUpdate.getServiceId()))this.serviceId = orderUpdate.getServiceId();
        if(nonNull(orderUpdate.getLobType()))this.lobType = orderUpdate.getLobType();
        if(nonNull(orderUpdate.getInstallationDateAndTime()))this.installationDateAndTime = orderUpdate.getInstallationDateAndTime();
    }

}
