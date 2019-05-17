package net.thumbtack.onlineshop.service.interfaces;

import net.thumbtack.onlineshop.dto.SettingResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

public interface SettingService {
    SettingResponse getSetting(String javaSessionId) throws OnlineShopException;
}
