package net.thumbtack.onlineshop.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminRegistrationResponse {

    private long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String position;

    @JsonIgnore
    private String javaSessionId;

}
