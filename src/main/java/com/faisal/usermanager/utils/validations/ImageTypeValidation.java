package com.faisal.usermanager.utils.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

@Target({FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ImageTypeValidator.class)
public @interface ImageTypeValidation {

    String message() default "Image type must be of (png/jpg/jpeg)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
