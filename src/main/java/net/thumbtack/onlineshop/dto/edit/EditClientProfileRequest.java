package net.thumbtack.onlineshop.dto.edit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.onlineshop.validator.annotation.MinPasswordLength;
import net.thumbtack.onlineshop.validator.annotation.NameContentValidation;
import net.thumbtack.onlineshop.validator.annotation.NameLengthValidation;
import net.thumbtack.onlineshop.validator.annotation.Patronymic;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditClientProfileRequest {

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
    @MinPasswordLength
    private String oldPassword;

    @NotNull(message = "Field could not be empty")
    @MinPasswordLength
    private String newPassword;

    public String getPatronymic() {
        if (patronymic == null) {
            return "";
        }
        return patronymic;
    }
}


