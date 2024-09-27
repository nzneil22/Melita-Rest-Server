package com.melita_task.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationAddressUpdate {

    @NotBlank
    private String island;

    @NotBlank
    private String town;

    @NotBlank
    private String street;

    @NotBlank
    private String building;
}
