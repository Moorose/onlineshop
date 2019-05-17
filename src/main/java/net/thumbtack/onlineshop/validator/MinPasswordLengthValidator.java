package net.thumbtack.onlineshop.validator;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.validator.annotation.MinPasswordLength;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class MinPasswordLengthValidator implements ConstraintValidator<MinPasswordLength, String> {

    @Value("${min_password_length}")
    private int min_password_length;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        log.debug("Validate min password length: " + s);
        if (s == null || s.isEmpty()) {
            return false;
        }
        return s.length() >= min_password_length;
    }
}
