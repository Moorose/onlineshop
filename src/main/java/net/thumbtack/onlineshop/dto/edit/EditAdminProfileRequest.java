package net.thumbtack.onlineshop.dto.edit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.onlineshop.validator.annotation.MinPasswordLength;
import net.thumbtack.onlineshop.validator.annotation.NameContentValidation;
import net.thumbtack.onlineshop.validator.annotation.NameLengthValidation;
import net.thumbtack.onlineshop.validator.annotation.Patronymic;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditAdminProfileRequest {

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
    @MinPasswordLength
    private String oldPassword;

    @NotNull(message = "Field could not be empty")
    @MinPasswordLength
    private String newPassword;

}
