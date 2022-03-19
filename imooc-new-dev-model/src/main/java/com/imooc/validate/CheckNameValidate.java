package com.imooc.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckNameValidate implements ConstraintValidator<CheckName, String> {
    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
