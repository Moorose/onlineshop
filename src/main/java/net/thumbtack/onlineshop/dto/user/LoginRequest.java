package net.thumbtack.onlineshop.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.onlineshop.validator.annotation.LoginContentValidation;
import net.thumbtack.onlineshop.validator.annotation.MinPasswordLength;
import net.thumbtack.onlineshop.validator.annotation.NameLengthValidation;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "Field could not be empty")
    @NameLengthValidation
    @LoginContentValidation
    private String login;

    @NotNull(message = "Field could not be empty")
    @MinPasswordLength
    private String password;
}
