package net.thumbtack.onlineshop.validator.annotation;

import net.thumbtack.onlineshop.validator.MinPasswordLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinPasswordLengthValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MinPasswordLength {
    String message() default "Password is very short";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
