package com.melita_task.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static java.util.Objects.nonNull;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @Column(columnDefinition = "char(36)")
    private String id = UUID.randomUUID().toString();

    @Valid
    @NotNull
    @Embedded
    private FullName fullName;

    @Valid
    @NotNull
    @Embedded
    private InstallationAddress installationAddress;

    public void updateClient(final FullName fullName){
        if(nonNull(fullName.getFirstName()))this.fullName.setFirstName(fullName.getFirstName());
        if(nonNull(fullName.getMiddleName()))this.fullName.setMiddleName(fullName.getMiddleName());
        if(nonNull(fullName.getLastName()))this.fullName.setLastName(fullName.getLastName());
    }

    public void updateClient(final InstallationAddress installationAddress){
        if(nonNull(installationAddress.getIsland()))this.installationAddress.setIsland(installationAddress.getIsland());
        if(nonNull(installationAddress.getTown()))this.installationAddress.setTown(installationAddress.getTown());
        if(nonNull(installationAddress.getStreet()))this.installationAddress.setStreet(installationAddress.getStreet());
        if(nonNull(installationAddress.getBuilding()))this.installationAddress.setBuilding(installationAddress.getBuilding());
    }

}
