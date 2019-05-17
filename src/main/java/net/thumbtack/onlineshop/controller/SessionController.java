package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.EmptyResponse;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.LoginRequest;
import net.thumbtack.onlineshop.dto.user.LoginResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;

@RestController
@RequestMapping("/api")
public class SessionController {


    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/sessions")
    public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) throws OnlineShopException {

        LoginResponse loginResponse = registrationService.loginUser(loginRequest);
        if (loginResponse.getAdminRegistrationResponse() != null) {
            AdminRegistrationResponse adminRegistrationResponse = loginResponse.getAdminRegistrationResponse();
            response.addCookie(new Cookie(COOKIE_NAME, loginResponse.getAdminRegistrationResponse().getJavaSessionId()));
            return ResponseEntity.ok().body(adminRegistrationResponse);
        } else {
            ClientRegistrationResponse clientRegistrationResponse = loginResponse.getClientRegistrationResponse();
            response.addCookie(new Cookie(COOKIE_NAME, loginResponse.getClientRegistrationResponse().getJavaSessionId()));
            return ResponseEntity.ok().body(clientRegistrationResponse);
        }
    }

    @DeleteMapping("/sessions")
    public ResponseEntity logout(@CookieValue(COOKIE_NAME) String javaSessionId) {
        registrationService.logoutUser(javaSessionId);
        return ResponseEntity.ok().body(new EmptyResponse());
    }
}
