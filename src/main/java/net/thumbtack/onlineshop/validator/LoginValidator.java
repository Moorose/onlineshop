package net.thumbtack.onlineshop.validator;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.validator.annotation.LoginContentValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LoginValidator implements ConstraintValidator<LoginContentValidation, String> {


    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        log.debug("Validate property login : " + login);
        if (login == null) {
            return false;
        }
        Pattern p = Pattern.compile("[а-яёА-ЯЁA-Za-z]+");
        Matcher m = p.matcher(login);
        return m.matches();
    }
}
