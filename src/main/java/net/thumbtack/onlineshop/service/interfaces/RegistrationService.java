package net.thumbtack.onlineshop.service.interfaces;

import net.thumbtack.onlineshop.dto.user.*;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

public interface RegistrationService {
    
     AdminRegistrationResponse adminRegistration(AdminRegistrationRequest request);

     ClientRegistrationResponse clientRegistration(ClientRegistrationRequest request);

    void logoutUser(String javaSessionId);

     LoginResponse loginUser(LoginRequest loginRequest) throws OnlineShopException;
}
