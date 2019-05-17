package net.thumbtack.onlineshop.validator;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.validator.annotation.Patronymic;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PatronymicValidator implements ConstraintValidator<Patronymic, String> {
    @Value("${max_name_length}")
    private int max_name_length;

    @Override
    public boolean isValid(String patronymic, ConstraintValidatorContext constraintValidatorContext) {
        log.debug("Validate patronymic content and length: " + patronymic);
        if (patronymic == null) {
            return true;
        }
        if (patronymic.isEmpty()) {
            return true;
        }
        Pattern p = Pattern.compile("[а-яёА-ЯЁ -]+");
        Matcher m = p.matcher(patronymic);
        if (!m.matches()) {
            return false;
        }
        return patronymic.length() <= max_name_length;
    }
}
