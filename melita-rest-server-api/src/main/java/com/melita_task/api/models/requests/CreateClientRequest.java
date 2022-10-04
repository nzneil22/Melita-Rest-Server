package com.melita_task.api.models.requests;

import com.melita_task.contract.FullNameDto;
import com.melita_task.contract.InstallationAddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRequest {

    @Valid
    private FullNameDto fullName;

    @Valid
    private InstallationAddressDto installationAddress;

}
