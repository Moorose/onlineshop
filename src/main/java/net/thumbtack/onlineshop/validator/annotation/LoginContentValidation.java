package net.thumbtack.onlineshop.validator.annotation;

import net.thumbtack.onlineshop.validator.LoginValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LoginValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginContentValidation {
    String message() default "LoginContentValidation contains invalid characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
