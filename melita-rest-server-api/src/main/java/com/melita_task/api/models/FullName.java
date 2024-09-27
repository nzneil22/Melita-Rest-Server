package com.melita_task.api.models;

import com.melita_task.contract.annotations.NotBlankIfDefined;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FullName{

    @NotBlank
    private String firstName;

    @NotBlankIfDefined
    private String middleName;

    @NotBlank
    private String lastName;
}