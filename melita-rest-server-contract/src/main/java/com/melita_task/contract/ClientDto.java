package com.melita_task.contract;

import lombok.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
}
