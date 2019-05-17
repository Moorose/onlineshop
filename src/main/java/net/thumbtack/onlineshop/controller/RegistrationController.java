package net.thumbtack.onlineshop.controller;


import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.service.interfaces.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping(value = "/admins")
    public ResponseEntity adminRegistration(@Valid @RequestBody AdminRegistrationRequest request, HttpServletResponse response) {
        AdminRegistrationResponse registrationResponse = registrationService.adminRegistration(request);
        response.addCookie(new Cookie(COOKIE_NAME, registrationResponse.getJavaSessionId()));
        return ResponseEntity.ok().body(registrationResponse);
    }

    @PostMapping(value = "/clients")
    public ResponseEntity clientRegistration(@Valid @RequestBody ClientRegistrationRequest request, HttpServletResponse response) {
        ClientRegistrationResponse registrationResponse = registrationService.clientRegistration(request);
        response.addCookie(new Cookie(COOKIE_NAME, registrationResponse.getJavaSessionId()));
        return ResponseEntity.ok().body(registrationResponse);
    }

}
