package net.thumbtack.onlineshop.rest;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.LoginRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SessionTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    @Before
    public void before() {
        commonClearDatabaseNode.clearDatabase();
    }

    @Test
    public void logoutAndLoginClient() {
        ClientRegistrationRequest registrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "Client_Address", "79131533464", "ClientLogin", "password123");
        ResponseEntity<ClientRegistrationResponse> registrationResponse = restTemplate.postForEntity("/api/clients", registrationRequest, ClientRegistrationResponse.class);
        String cookie = registrationResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        cookie = cookie.substring(cookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, registrationResponse.getStatusCode());
        assertNotNull(cookie);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<String> deleteResponseEntity = restTemplate.exchange("/api/sessions", HttpMethod.DELETE, requestEntity, String.class);
        assertEquals(HttpStatus.OK, deleteResponseEntity.getStatusCode());
        assertEquals("{}", deleteResponseEntity.getBody());

        LoginRequest loginRequest = new LoginRequest(registrationRequest.getLogin(), registrationRequest.getPassword());
        ResponseEntity<ClientRegistrationResponse> loginResponse = restTemplate.postForEntity("/api/sessions", loginRequest, ClientRegistrationResponse.class);
        String newCookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        newCookie = newCookie.substring(newCookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(newCookie);
        assertEquals(registrationResponse.getBody().getId(), loginResponse.getBody().getId());
    }

    @Test
    public void logoutAndLoginAdmin() {
        AdminRegistrationRequest registrationRequest = new AdminRegistrationRequest("Админ", "Фамилия", null, "admin", "Adminlogin", "password");
        ResponseEntity<ClientRegistrationResponse> registrationResponse = restTemplate.postForEntity("/api/admins", registrationRequest, ClientRegistrationResponse.class);
        String cookie = registrationResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        cookie = cookie.substring(cookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, registrationResponse.getStatusCode());
        assertNotNull(cookie);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<String> deleteResponseEntity = restTemplate.exchange("/api/sessions", HttpMethod.DELETE, requestEntity, String.class);
        assertEquals(HttpStatus.OK, deleteResponseEntity.getStatusCode());
        assertEquals("{}", deleteResponseEntity.getBody());

        LoginRequest loginRequest = new LoginRequest(registrationRequest.getLogin(), registrationRequest.getPassword());
        ResponseEntity<ClientRegistrationResponse> loginResponse = restTemplate.postForEntity("/api/sessions", loginRequest, ClientRegistrationResponse.class);
        String newCookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        newCookie = newCookie.substring(newCookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(newCookie);
        assertEquals(registrationResponse.getBody().getId(), loginResponse.getBody().getId());
    }
}