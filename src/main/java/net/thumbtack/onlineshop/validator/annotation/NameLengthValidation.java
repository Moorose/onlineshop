package net.thumbtack.onlineshop.validator.annotation;

import net.thumbtack.onlineshop.validator.NameLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NameLengthValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NameLengthValidation {
    String message() default "The field is very long";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
