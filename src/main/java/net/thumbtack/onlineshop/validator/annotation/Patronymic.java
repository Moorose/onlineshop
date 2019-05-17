package net.thumbtack.onlineshop.validator.annotation;

import net.thumbtack.onlineshop.validator.PatronymicValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PatronymicValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Patronymic {
    String message() default "Patronymic is incorrect";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
