package net.thumbtack.onlineshop.validator.annotation;

import net.thumbtack.onlineshop.validator.NameContentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NameContentValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NameContentValidation {
    String message() default "Field contain incorrect symbol";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
