package net.thumbtack.onlineshop.service.implementation;

import net.thumbtack.onlineshop.database.dao.AdministratorDao;
import net.thumbtack.onlineshop.database.dao.ClientDao;
import net.thumbtack.onlineshop.database.dao.UserDao;
import net.thumbtack.onlineshop.dto.user.*;
import net.thumbtack.onlineshop.entity.Admin;
import net.thumbtack.onlineshop.entity.Client;
import net.thumbtack.onlineshop.entity.User;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;

@Service
public class RegistrationServiceImpl extends BaseService implements RegistrationService {

    @Autowired
    public RegistrationServiceImpl(AdministratorDao administratorDao, ClientDao clientDao, UserDao userDao) {
        super(administratorDao, clientDao, userDao, null, null);
    }

    @Override
    public AdminRegistrationResponse adminRegistration(AdminRegistrationRequest request) {
        Admin admin = new Admin(0, request.getFirstName(), request.getLastName(), request.getPatronymic(), request.getLogin(), request.getPassword(), request.getPosition());
        admin = administratorDao.insertAdmin(admin);
        String javaSessionId = login(admin);
        return responseBuilder(admin, javaSessionId);
    }

    @Override
    public ClientRegistrationResponse clientRegistration(ClientRegistrationRequest request) {
        Client client = new Client(0, request.getFirstName(), request.getLastName(), request.getPatronymic(), request.getLogin(), request.getPassword(), request.getPhone(), request.getEmail(), request.getAddress(), 0);
        client = clientDao.insertClient(client);
        String javaSessionId = login(client);
        return responseBuilder(client, javaSessionId);
    }

    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) throws OnlineShopException {
        User user = userDao.findUserByLogin(loginRequest.getLogin().toUpperCase());
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new OnlineShopException("password", OnlineShopErrorCode.WRONG_PASSWORD);
        }
        String checkToken = userDao.findTokenByUserId(user.getId());
        if (checkToken != null) {
            userDao.deleteServerSession(checkToken);
        }
        Admin admin = administratorDao.findAdminById(user.getId());
        if (admin != null) {
            String token = login(admin);
            return new LoginResponse(responseBuilder(admin, token), null);
        }
        Client client = clientDao.findClientById(user.getId());
        if (client != null) {
            String token = login(client);
            return new LoginResponse(null, responseBuilder(client, token));
        }
        throw new OnlineShopException(COOKIE_NAME, OnlineShopErrorCode.UNKNOWN_USER);
    }

    private String login(User user) {
        String token = UUID.randomUUID().toString();
        return userDao.openServerSession(user, token);
    }

    @Override
    public void logoutUser(String javaSessionId) {
        userDao.deleteServerSession(javaSessionId);
    }

}
