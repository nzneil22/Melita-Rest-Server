package com.melita_task.contract.requests;

import com.melita_task.contract.FullNameUpdateDto;
import com.melita_task.contract.InstallationAddressUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Data
@Validated
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UpdateClientRequestDto {

    private final FullNameUpdateDto fullName;

    private final InstallationAddressUpdateDto installationAddress;

    public Optional<FullNameUpdateDto> getFullName() {
        return Optional.ofNullable(this.fullName);
    }

    public Optional<InstallationAddressUpdateDto> getInstallationAddress() {
        return Optional.ofNullable(this.installationAddress);
    }
}