package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.SettingResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;

@RestController
@RequestMapping("/api")
public class SettingController {

    @Autowired
    private SettingService settingService;

    @GetMapping("/settings")
    public ResponseEntity getAccountsInfo(@CookieValue(value = COOKIE_NAME, required = false) String javaSessionId) throws OnlineShopException {
        SettingResponse settingResponse = settingService.getSetting(javaSessionId);
        return ResponseEntity.ok().body(settingResponse);
    }
}
