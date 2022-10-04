package com.melita_task.api.models.requests;

import com.melita_task.api.models.FullNameUpdate;
import com.melita_task.api.models.InstallationAddressUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Optional;

@Data
@Validated
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UpdateClientRequest {

    @Valid
    private final FullNameUpdate fullName;

    @Valid
    private final InstallationAddressUpdate installationAddress;

    public Optional<FullNameUpdate> getFullName() {
        return Optional.ofNullable(this.fullName);
    }

    public Optional<InstallationAddressUpdate> getInstallationAddress() {
        return Optional.ofNullable(this.installationAddress);
    }
}