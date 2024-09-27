package com.melita_task.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class FullNameDto {

    @NotBlank
    private String firstName;

    private String middleName;

    @NotBlank
    private String lastName;
}