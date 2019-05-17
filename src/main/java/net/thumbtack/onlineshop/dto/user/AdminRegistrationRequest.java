package net.thumbtack.onlineshop.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.onlineshop.validator.annotation.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRegistrationRequest {

    @NotNull(message = "Field could not be empty")
    @NameLengthValidation
    @NameContentValidation
    private String firstName;

    @NotNull(message = "Field could not be empty")
    @NameLengthValidation
    @NameContentValidation
    private String lastName;

    @Patronymic
    private String patronymic;

    @NotNull(message = "Field could not be empty")
    @NameLengthValidation
    private String position;

    @NotNull(message = "Field could not be empty")
    @NameLengthValidation
    @LoginContentValidation
    private String login;

    @NotNull(message = "Field could not be empty")
    @MinPasswordLength
    private String password;
}
