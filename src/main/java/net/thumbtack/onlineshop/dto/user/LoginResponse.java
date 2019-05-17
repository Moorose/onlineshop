package net.thumbtack.onlineshop.dto.user;

import lombok.Data;

@Data
public class LoginResponse {
    private final AdminRegistrationResponse adminRegistrationResponse;
    private final ClientRegistrationResponse clientRegistrationResponse;
}
