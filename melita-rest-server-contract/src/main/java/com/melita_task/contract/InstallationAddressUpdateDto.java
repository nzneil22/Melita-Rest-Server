package com.melita_task.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class InstallationAddressUpdateDto {

    @Valid
    @NotBlank
    private String island;

    @Valid
    @NotBlank
    private String town;

    @Valid
    @NotBlank
    private String street;

    @Valid
    @NotBlank
    private String building;
}
