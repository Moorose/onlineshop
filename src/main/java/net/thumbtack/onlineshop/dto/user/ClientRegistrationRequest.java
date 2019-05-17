package net.thumbtack.onlineshop.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.onlineshop.validator.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRegistrationRequest {

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

    @Email(message = "Field has error")
    private String email;

    @NotNull(message = "Field could not be empty")
    private String address;

    @NotNull(message = "Field could not be empty")
    @Pattern(regexp = "[+]?[8 7][-]?\\d{3}[-]?\\d{3}[-]?\\d{2}[-]?\\d{2}", message = "The phone does not match the pattern")
    private String phone;

    @NotNull(message = "Field could not be empty")
    @NameLengthValidation
    @LoginContentValidation
    private String login;

    @NotNull(message = "Field could not be empty")
    @MinPasswordLength
    private String password;

}
