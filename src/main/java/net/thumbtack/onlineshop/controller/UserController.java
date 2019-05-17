package net.thumbtack.onlineshop.controller;


import net.thumbtack.onlineshop.dto.edit.EditAdminProfileRequest;
import net.thumbtack.onlineshop.dto.edit.EditClientProfileRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.GetClientsInfoResponse;
import net.thumbtack.onlineshop.dto.user.LoginResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/accounts")
    public ResponseEntity getAccountsInfo(@CookieValue(COOKIE_NAME) String javaSessionId) throws OnlineShopException {
        LoginResponse loginResponse = userService.getCurrentUserInfo(javaSessionId);
        if (loginResponse.getAdminRegistrationResponse() != null) {
            AdminRegistrationResponse adminRegistrationResponse = loginResponse.getAdminRegistrationResponse();
            return ResponseEntity.ok().body(adminRegistrationResponse);
        } else {
            ClientRegistrationResponse clientRegistrationResponse = loginResponse.getClientRegistrationResponse();
            return ResponseEntity.ok().body(clientRegistrationResponse);
        }
    }

    @GetMapping("/clients")
    public ResponseEntity<List<GetClientsInfoResponse>> getClients(@CookieValue(COOKIE_NAME) String javaSessionId) throws OnlineShopException {
        List<GetClientsInfoResponse> clientsInfo = userService.getClientsInfo(javaSessionId);
        return ResponseEntity.ok().body(clientsInfo);
    }

    @PutMapping("/admins")
    public ResponseEntity editAdminProfile(@CookieValue(COOKIE_NAME) String javaSessionId, @Valid @RequestBody EditAdminProfileRequest request) throws OnlineShopException {
        AdminRegistrationResponse registrationResponse = userService.editAdmin(javaSessionId, request);
        return ResponseEntity.ok().body(registrationResponse);
    }

    @PutMapping("/clients")
    public ResponseEntity editClientProfile(@CookieValue(COOKIE_NAME) String javaSessionId, @Valid @RequestBody EditClientProfileRequest request) throws OnlineShopException {
        ClientRegistrationResponse registrationResponse = userService.editClient(javaSessionId, request);
        return ResponseEntity.ok().body(registrationResponse);
    }

}
