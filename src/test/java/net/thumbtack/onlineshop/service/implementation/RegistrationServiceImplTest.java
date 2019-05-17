package net.thumbtack.onlineshop.service.implementation;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.user.*;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.RegistrationService;
import net.thumbtack.onlineshop.service.interfaces.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RegistrationServiceImplTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    @Before
    public void before() {
        commonClearDatabaseNode.clearDatabase();
    }

    @Test
    public void adminRegistration() {
        AdminRegistrationRequest request = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "AdminLogin", "password");
        AdminRegistrationResponse response = registrationService.adminRegistration(request);
        assertTrue(response.getId() != 0);
        assertEquals(request.getFirstName(), response.getFirstName());
        assertEquals(request.getPatronymic(), response.getPatronymic());
        assertEquals(request.getPosition(), response.getPosition());
    }

    @Test
    public void clientRegistration() {
        ClientRegistrationRequest request = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        ClientRegistrationResponse response = registrationService.clientRegistration(request);
        assertTrue(response.getId() != 0);
        assertEquals(request.getFirstName(), response.getFirstName());
        assertEquals(request.getPatronymic(), response.getPatronymic());
        assertEquals(request.getPhone(), response.getPhone());
    }

    @Test
    public void loginClient() throws OnlineShopException {
        ClientRegistrationRequest request = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        ClientRegistrationResponse response = registrationService.clientRegistration(request);
        assertTrue(response.getId() != 0);
        LoginRequest loginRequest = new LoginRequest(request.getLogin(), request.getPassword());
        LoginResponse loginResponse = registrationService.loginUser(loginRequest);
        assertNull(loginResponse.getAdminRegistrationResponse());
        ClientRegistrationResponse clientRegistrationResponse = loginResponse.getClientRegistrationResponse();
        assertEquals(response.getId(), clientRegistrationResponse.getId());
    }

    @Test
    public void loginUserCheckNonCaseSensitiveLogin() throws OnlineShopException {
        ClientRegistrationRequest request = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        ClientRegistrationResponse response = registrationService.clientRegistration(request);
        assertTrue(response.getId() != 0);

        LoginResponse relogin = registrationService.loginUser(new LoginRequest("ClientLogin", request.getPassword()));
        assertNotEquals(response.getJavaSessionId(), relogin.getClientRegistrationResponse().getJavaSessionId());
        assertEquals(response.getId(), relogin.getClientRegistrationResponse().getId());

        LoginResponse relogin2 = registrationService.loginUser(new LoginRequest("clientlogin", request.getPassword()));
        assertNotEquals(relogin.getClientRegistrationResponse().getJavaSessionId(), relogin2.getClientRegistrationResponse().getJavaSessionId());
        assertEquals(response.getId(), relogin2.getClientRegistrationResponse().getId());

        LoginResponse relogin3 = registrationService.loginUser(new LoginRequest("CLIENTLOGIN", request.getPassword()));
        assertNotEquals(relogin2.getClientRegistrationResponse().getJavaSessionId(), relogin3.getClientRegistrationResponse().getJavaSessionId());
        assertEquals(response.getId(), relogin3.getClientRegistrationResponse().getId());

        LoginResponse relogin4 = registrationService.loginUser(new LoginRequest("cLIENTlOGIN", request.getPassword()));
        assertNotEquals(relogin3.getClientRegistrationResponse().getJavaSessionId(), relogin4.getClientRegistrationResponse().getJavaSessionId());
        assertEquals(response.getId(), relogin4.getClientRegistrationResponse().getId());
    }

    @Test
    public void doubleLogoutUser() {
        ClientRegistrationRequest request = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        ClientRegistrationResponse response = registrationService.clientRegistration(request);
        assertTrue(response.getId() != 0);
        registrationService.logoutUser(response.getJavaSessionId());
        try {
            registrationService.logoutUser(response.getJavaSessionId());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void logoutUser() {
        ClientRegistrationRequest request = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        ClientRegistrationResponse response = registrationService.clientRegistration(request);
        assertTrue(response.getId() != 0);
        registrationService.logoutUser(response.getJavaSessionId());
        try {
            userService.getClientsInfo(response.getJavaSessionId());
            fail();
        } catch (OnlineShopException e) {
            assertEquals(OnlineShopErrorCode.UNKNOWN_TOKEN, e.getOnlineShopErrorCode());
        }
    }
}