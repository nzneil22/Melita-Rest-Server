package com.melita_task.contract;

import lombok.*;

import javax.validation.Valid;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    @Valid
    @NonNull
    private String id;

    @Valid
    private FullNameDto fullName;

    @Valid
    private InstallationAddressDto installationAddress;

    @NonNull
    private ClientStatus status;
}
