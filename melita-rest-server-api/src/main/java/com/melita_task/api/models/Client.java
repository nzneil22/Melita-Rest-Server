package com.melita_task.api.models;

import com.melita_task.contract.ClientStatus;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @Type(type = "uuid-char")
    @Column(columnDefinition = "char(36)")
    private final UUID id = UUID.randomUUID();

    @Valid
    @NotNull
    @Embedded
    private FullName fullName;

    @Valid
    @NotNull
    @Embedded
    private InstallationAddress installationAddress;


    private ClientStatus status = ClientStatus.ACTIVE;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "client")
    private List<Order> orders;

    public void updateClient(final FullNameUpdate fullName){
        if(nonNull(fullName.getFirstName()))this.fullName.setFirstName(fullName.getFirstName());
        if(nonNull(fullName.getMiddleName()))this.fullName.setMiddleName(fullName.getMiddleName());
        if(nonNull(fullName.getLastName()))this.fullName.setLastName(fullName.getLastName());
    }

    public void updateClient(final InstallationAddressUpdate installationAddress){
        if(nonNull(installationAddress.getIsland()))this.installationAddress.setIsland(installationAddress.getIsland());
        if(nonNull(installationAddress.getTown()))this.installationAddress.setTown(installationAddress.getTown());
        if(nonNull(installationAddress.getStreet()))this.installationAddress.setStreet(installationAddress.getStreet());
        if(nonNull(installationAddress.getBuilding()))this.installationAddress.setBuilding(installationAddress.getBuilding());
    }

}
