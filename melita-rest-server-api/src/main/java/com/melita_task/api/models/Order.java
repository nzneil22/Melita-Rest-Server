package com.melita_task.api.models;

import com.melita_task.api.models.requests.CreateOrderRequest;
import com.melita_task.api.models.requests.UpdateOrderRequest;
import com.melita_task.contract.enums.LobTypes;
import com.melita_task.contract.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Type(type = "uuid-char")
    @Column(columnDefinition = "char(36)")
    private final UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private final Client client;

    @NotNull
    private Integer serviceId;

    @NotNull
    private LobTypes lobType;

    @NotNull
    private Date installationDateAndTime;

    private OrderStatus status;

    public Order(final Client client,
                 final @Valid CreateOrderRequest request) {
        this.client = client;
        this.serviceId = request.getServiceId();
        this.lobType = request.getLobType();
        this.installationDateAndTime = request.getInstallationDateAndTime();
        this.status = OrderStatus.CREATED;
    }

    public void updateOrder(final @Valid UpdateOrderRequest orderUpdate) {
        orderUpdate.getServiceId().ifPresent(this::setServiceId);
        orderUpdate.getLobType().ifPresent(this::setLobType);
        orderUpdate.getInstallationDate().ifPresent(this::setInstallationDateAndTime);
    }

}
