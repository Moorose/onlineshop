package net.thumbtack.onlineshop.rest;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    @Before
    public void before() {
        commonClearDatabaseNode.clearDatabase();
    }

    @Test
    public void adminRegistration() {
        AdminRegistrationRequest request = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "Adminlogin", "password");
        ResponseEntity<AdminRegistrationResponse> response = restTemplate.postForEntity("/api/admins", request, AdminRegistrationResponse.class);
        String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        cookie = cookie.substring(cookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(cookie);
        assertTrue(response.getBody().getId() != 0);
        assertEquals(request.getFirstName(), response.getBody().getFirstName());
        assertEquals(request.getPosition(), response.getBody().getPosition());
    }

    @Test
    public void clientRegistration() {
        ClientRegistrationRequest request = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        ResponseEntity<ClientRegistrationResponse> response = restTemplate.postForEntity("/api/clients", request, ClientRegistrationResponse.class);
        String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        cookie = cookie.substring(cookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(cookie);
        assertTrue(response.getBody().getId() != 0);
        assertEquals(request.getFirstName(), response.getBody().getFirstName());
        assertEquals(request.getEmail(), response.getBody().getEmail());
    }
}