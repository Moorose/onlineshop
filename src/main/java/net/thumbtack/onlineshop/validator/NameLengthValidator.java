package net.thumbtack.onlineshop.validator;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.validator.annotation.NameLengthValidation;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class NameLengthValidator implements ConstraintValidator<NameLengthValidation, String> {

    @Value("${max_name_length}")
    private int max_name_length;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        log.debug("Validate max length property: " + name);
        if (name == null) {
            return false;
        }
        return name.length() <= max_name_length;
    }
}
