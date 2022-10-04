package com.melita_task.contract.requests;

import com.melita_task.contract.FullNameDto;
import com.melita_task.contract.InstallationAddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;


@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRequestDto {

    @Valid
    private FullNameDto fullName;

    @Valid
    private InstallationAddressDto installationAddress;

}
