package com.myproject.emailverifierrestservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank(message = "Password in required.")
@Size(min = 4, max = 60, message = "Password must be between 4 and 60 characters.")

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface PasswordConstraints {

    String message() default "Invalid password.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
