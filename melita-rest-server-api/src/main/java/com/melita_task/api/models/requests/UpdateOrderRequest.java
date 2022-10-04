package com.melita_task.api.models.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melita_task.contract.enums.LobTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;

@Data
@Validated
@AllArgsConstructor
public class UpdateOrderRequest {

    @Valid
    @NotNull
    private Integer serviceId;

    @Valid
    @NotNull
    private LobTypes lobType;

    @Valid
    @NotNull
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private Date installationDate;

    public Optional<Integer> getServiceId() {
        return Optional.ofNullable(this.serviceId);
    }

    public Optional<LobTypes> getLobType() {
        return Optional.ofNullable(this.lobType);
    }

    public Optional<Date> getInstallationDate() {
        return Optional.ofNullable(this.installationDate);
    }

    public Integer getServiceIdConcrete() {
        return serviceId;
    }

    public LobTypes getLobTypeConcrete() {
        return lobType;
    }

    public Date getInstallationDateConcrete() {
        return installationDate;
    }


}
