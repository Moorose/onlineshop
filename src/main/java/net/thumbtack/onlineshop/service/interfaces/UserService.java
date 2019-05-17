package net.thumbtack.onlineshop.service.interfaces;

import net.thumbtack.onlineshop.dto.edit.EditAdminProfileRequest;
import net.thumbtack.onlineshop.dto.edit.EditClientProfileRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.GetClientsInfoResponse;
import net.thumbtack.onlineshop.dto.user.LoginResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

import java.util.List;

public interface UserService {

    LoginResponse getCurrentUserInfo(String javaSessionId) throws OnlineShopException;

    List<GetClientsInfoResponse> getClientsInfo(String javaSessionId) throws OnlineShopException;

    AdminRegistrationResponse editAdmin(String javaSessionId, EditAdminProfileRequest request) throws OnlineShopException;

    ClientRegistrationResponse editClient(String javaSessionId, EditClientProfileRequest request) throws OnlineShopException;

}
