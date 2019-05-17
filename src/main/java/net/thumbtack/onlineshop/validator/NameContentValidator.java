package net.thumbtack.onlineshop.validator;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.validator.annotation.NameContentValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class NameContentValidator implements ConstraintValidator<NameContentValidation, String> {

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        log.debug("Validate content property: " + name);
        if (name == null) {
            return true;
        }
        Pattern p = Pattern.compile("[а-яёА-ЯЁ -]+");
        Matcher m = p.matcher(name);
        return m.matches();
    }
}
