package com.melita_task.CustomerStructure;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class InstallationAddress {

    @NotBlank
    private String island;

    @NotBlank
    private String town;

    @NotBlank
    private String street;

    @NotBlank
    private String building;
}
