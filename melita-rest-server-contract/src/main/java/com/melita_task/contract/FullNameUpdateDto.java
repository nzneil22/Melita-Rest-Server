package com.melita_task.contract;

import com.melita_task.contract.annotations.NotBlankIfDefined;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FullNameUpdateDto {

    @NotBlankIfDefined
    private final String firstName;

    @NotBlankIfDefined
    private final String middleName;

    @NotBlankIfDefined
    private final String lastName;

}