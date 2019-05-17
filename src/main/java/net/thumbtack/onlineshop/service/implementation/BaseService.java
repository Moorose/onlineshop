package net.thumbtack.onlineshop.service.implementation;

import net.thumbtack.onlineshop.database.dao.*;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.entity.Admin;
import net.thumbtack.onlineshop.entity.Client;
import net.thumbtack.onlineshop.entity.User;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

abstract class BaseService {

    final AdministratorDao administratorDao;
    final ClientDao clientDao;
    final UserDao userDao;
    final CategoryDao categoryDao;
    final ProductDao productDao;

    public BaseService(AdministratorDao administratorDao, ClientDao clientDao, UserDao userDao, CategoryDao categoryDao, ProductDao productDao) {
        this.administratorDao = administratorDao;
        this.clientDao = clientDao;
        this.userDao = userDao;
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    Admin getAdminByCookie(String javaSessionId) throws OnlineShopException {
        User user = userDao.findUserByToken(javaSessionId);
        Admin admin = administratorDao.findAdminById(user.getId());
        if (admin == null) {
            throw new OnlineShopException("id", OnlineShopErrorCode.ADMIN_ERROR_AUTHENTICATION);
        }
        return admin;
    }

    Client getClientByCookie(String javaSessionId) throws OnlineShopException {
        User user = userDao.findUserByToken(javaSessionId);
        Client client = clientDao.findClientById(user.getId());
        if (client == null) {
            throw new OnlineShopException("id", OnlineShopErrorCode.CLIENT_ERROR_AUTHENTICATION);
        }
        return client;
    }

    User getUserByCookie(String javaSessionId) throws OnlineShopException {
        User user = userDao.findUserByToken(javaSessionId);
        Client client = clientDao.findClientById(user.getId());
        if (client != null) {
            return client;
        }
        Admin admin = administratorDao.findAdminById(user.getId());
        if (admin != null) {
            return admin;
        }
        throw new OnlineShopException("javaSessionId", OnlineShopErrorCode.UNEXPECTED_SERVER_ERROR);
    }

    AdminRegistrationResponse responseBuilder(Admin admin, String javaSessionId) {
        return AdminRegistrationResponse.builder()
                .id(admin.getId())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .patronymic(admin.getPatronymic())
                .position(admin.getPosition())
                .javaSessionId(javaSessionId)
                .build();
    }

    ClientRegistrationResponse responseBuilder(Client client, String javaSessionId) {
        return ClientRegistrationResponse.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .patronymic(client.getPatronymic())
                .email(client.getEmail())
                .address(client.getAddress())
                .phone(client.getPhone())
                .deposit(client.getMoney())
                .javaSessionId(javaSessionId)
                .build();
    }

}
