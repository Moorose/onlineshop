package net.thumbtack.onlineshop.service.implementation;

import net.thumbtack.onlineshop.database.dao.AdministratorDao;
import net.thumbtack.onlineshop.database.dao.ClientDao;
import net.thumbtack.onlineshop.database.dao.UserDao;
import net.thumbtack.onlineshop.dto.edit.EditAdminProfileRequest;
import net.thumbtack.onlineshop.dto.edit.EditClientProfileRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.GetClientsInfoResponse;
import net.thumbtack.onlineshop.dto.user.LoginResponse;
import net.thumbtack.onlineshop.entity.Admin;
import net.thumbtack.onlineshop.entity.Client;
import net.thumbtack.onlineshop.entity.User;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;

@Service
public class UserServiceImpl extends BaseService implements UserService {

    @Autowired
    UserServiceImpl(AdministratorDao administratorDao, ClientDao clientDao, UserDao userDao) {
        super(administratorDao, clientDao, userDao, null, null);
    }

    @Override
    public LoginResponse getCurrentUserInfo(String javaSessionId) throws OnlineShopException {
        User user = userDao.findUserByToken(javaSessionId);
        Admin admin = administratorDao.findAdminById(user.getId());
        if (admin != null) {
            return new LoginResponse(responseBuilder(admin, javaSessionId), null);
        }
        Client client = clientDao.findClientById(user.getId());
        if (client != null) {
            return new LoginResponse(null, responseBuilder(client, javaSessionId));
        }
        throw new OnlineShopException(COOKIE_NAME, OnlineShopErrorCode.UNKNOWN_USER);
    }

    @Override
    public List<GetClientsInfoResponse> getClientsInfo(String javaSessionId) throws OnlineShopException {
        getAdminByCookie(javaSessionId);
        List<Client> clients = clientDao.findAllClient();
        List<GetClientsInfoResponse> responses = new ArrayList<>();
        for (Client client : clients) {
            responses.add(GetClientsInfoResponse.builder()
                    .id(client.getId())
                    .firstName(client.getFirstName())
                    .lastName(client.getLastName())
                    .patronymic(client.getPatronymic())
                    .email(client.getEmail())
                    .address(client.getAddress())
                    .phone(client.getPhone())
                    .userType("client")
                    .build());
        }
        return responses;
    }

    @Override
    public AdminRegistrationResponse editAdmin(String javaSessionId, EditAdminProfileRequest request) throws OnlineShopException {
        Admin admin = getAdminByCookie(javaSessionId);
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new OnlineShopException("password", OnlineShopErrorCode.IDENTICAL_PASSWORD);
        }
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setPatronymic(request.getPatronymic());
        admin.setPosition(request.getPosition());
        admin.setPassword(request.getNewPassword());
        admin = administratorDao.updateAdmin(admin);
        return responseBuilder(admin, javaSessionId);
    }

    @Override
    public ClientRegistrationResponse editClient(String javaSessionId, EditClientProfileRequest request) throws OnlineShopException {
        Client client = getClientByCookie(javaSessionId);
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new OnlineShopException("password", OnlineShopErrorCode.IDENTICAL_PASSWORD);
        }
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPatronymic(request.getPatronymic());
        client.setEmail(request.getEmail());
        client.setAddress(request.getAddress());
        client.setPhone(request.getPhone());
        client.setPassword(request.getNewPassword());
        client = clientDao.updateClient(client);
        return responseBuilder(client, javaSessionId);
    }

}
