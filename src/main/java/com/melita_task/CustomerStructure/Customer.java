package com.melita_task.CustomerStructure;

import com.melita_task.melita.Service;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Data
@Builder
public class Customer {
    final int id;

    @Valid
    @NotNull
    FullName fullname;

    @Valid
    @NonNull
    InstallationAddress installationAddress;

    @Valid
    @NonNull
    List<@NotNull @Valid Service> services;

    public void updateCustomer(final String name,
                               final String middle_name,
                               final String surname,
                               final String island,
                               final String town,
                               final String street,
                               final String building){

        if(nonNull(name))fullname.setFirstName(name);
        if(nonNull(middle_name))fullname.setMiddleName(middle_name);
        if(nonNull(surname))fullname.setLastName(surname);

        if(nonNull(island))installationAddress.setIsland(island);
        if(nonNull(town))installationAddress.setTown(town);
        if(nonNull(street))installationAddress.setStreet(street);
        if(nonNull(building))installationAddress.setBuilding(building);
    }

    public void attachService(Service serv) {
        services.add(serv);
    }

}
