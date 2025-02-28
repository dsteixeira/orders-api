package com.orders.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

import java.time.LocalDate;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class ConsistentDateParameterValidator
        implements ConstraintValidator<ConsistentDateParameters, Object[]> {

    @Override
    public boolean isValid(
            Object[] value,
            ConstraintValidatorContext context) {

        // Both dates should be filled if want to use date range
        if ((value[0] != null && value[1] == null) || (value[0] == null && value[1] != null)) {
            return false;
        }

        if (value[0] == null && value[1] == null) {
            return true;
        }

        if (!(value[0] instanceof LocalDate)
                || !(value[1] instanceof LocalDate)) {
            throw new IllegalArgumentException(
                    "Illegal method signature, expected two parameters of type LocalDate.");
        }

        return ((LocalDate) value[0]).isBefore((LocalDate) value[1]);
    }
}
