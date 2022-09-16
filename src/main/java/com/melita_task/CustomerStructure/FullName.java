package com.melita_task.CustomerStructure;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class FullName {

    @NotBlank
    private String firstName;

    private String middleName;

    @NotBlank
    private String lastName;
}