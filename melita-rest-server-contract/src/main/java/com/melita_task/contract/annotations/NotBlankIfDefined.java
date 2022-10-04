package com.melita_task.contract.annotations;

import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = NotBlankIfDefined.NotBlankIfDefinedValidator.class)
@Documented
public @interface NotBlankIfDefined {

    String message() default "{org.hibernate.validator.referenceguide.chapter06.CheckCase." +
            "message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class NotBlankIfDefinedValidator implements ConstraintValidator<NotBlankIfDefined, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {

            if (value == null) {
                return true;
            }

            return StringUtils.isNotBlank(value);
        }
    }
}

