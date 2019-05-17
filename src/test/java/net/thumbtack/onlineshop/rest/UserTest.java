package net.thumbtack.onlineshop.rest;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.edit.EditClientProfileRequest;
import net.thumbtack.onlineshop.dto.user.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    @Before
    public void before() {
        commonClearDatabaseNode.clearDatabase();
    }

    @Test
    public void getAccountsInfoAndEditProfileForClient() {
        ClientRegistrationRequest registrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "Client_Address", "79131533464", "ClientLogin", "password123");
        ResponseEntity<ClientRegistrationResponse> registrationResponse = restTemplate.postForEntity("/api/clients", registrationRequest, ClientRegistrationResponse.class);
        String cookie = registrationResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        cookie = cookie.substring(cookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, registrationResponse.getStatusCode());
        assertNotNull(cookie);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "JAVASESSIONID=" + cookie);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<ClientRegistrationResponse> deleteResponseEntity = restTemplate.exchange("/api/accounts", HttpMethod.GET, requestEntity, ClientRegistrationResponse.class);
        assertEquals(HttpStatus.OK, deleteResponseEntity.getStatusCode());
        assertEquals(registrationResponse.getBody().getId(), deleteResponseEntity.getBody().getId());
        assertEquals(registrationResponse.getBody().getFirstName(), deleteResponseEntity.getBody().getFirstName());
        assertEquals(registrationResponse.getBody().getEmail(), deleteResponseEntity.getBody().getEmail());

        EditClientProfileRequest editRequest = new EditClientProfileRequest("ККККлиент", "Фамилия", null, "clientnewMail@gmail.com", "ClientAddress", "+84564569895", "password123", "123password");
        HttpEntity editRequestEntity = new HttpEntity(editRequest, requestHeaders);
        ResponseEntity<ClientRegistrationResponse> editResponseEntity = restTemplate.exchange("/api/clients", HttpMethod.PUT, editRequestEntity, ClientRegistrationResponse.class);
        System.out.println(editResponseEntity.toString());
        assertEquals(HttpStatus.OK, editResponseEntity.getStatusCode());
        assertEquals(registrationResponse.getBody().getId(), editResponseEntity.getBody().getId());
        assertNotEquals(registrationResponse.getBody().getFirstName(), editResponseEntity.getBody().getFirstName());
        assertNotEquals(registrationResponse.getBody().getEmail(), editResponseEntity.getBody().getEmail());
    }


    @Test
    public void getClients() {
        ClientRegistrationRequest registrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "clientI@gmail.com", "Client_Address", "79131533467", "ClientLoginI", "password123");
        ResponseEntity<ClientRegistrationResponse> registrationResponse = restTemplate.postForEntity("/api/clients", registrationRequest, ClientRegistrationResponse.class);
        assertEquals(HttpStatus.OK, registrationResponse.getStatusCode());
        registrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "clientII@gmail.com", "Client_Address", "79131533469", "ClientLoginII", "password123");
        registrationResponse = restTemplate.postForEntity("/api/clients", registrationRequest, ClientRegistrationResponse.class);
        assertEquals(HttpStatus.OK, registrationResponse.getStatusCode());

        AdminRegistrationRequest request = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "Adminlogin", "password");
        ResponseEntity<AdminRegistrationResponse> response = restTemplate.postForEntity("/api/admins", request, AdminRegistrationResponse.class);
        String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        cookie = cookie.substring(cookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(cookie);
        assertTrue(response.getBody().getId() != 0);
        assertEquals(request.getFirstName(), response.getBody().getFirstName());
        assertEquals(request.getPosition(), response.getBody().getPosition());

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "JAVASESSIONID=" + cookie);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<List<GetClientsInfoResponse>> responseEntity =
                restTemplate.exchange("/api/clients", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<GetClientsInfoResponse>>() {
                });
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, responseEntity.getBody().size());
        for (GetClientsInfoResponse resp : responseEntity.getBody()) {
            assertTrue(resp.getId() != 0);
            assertEquals("client", resp.getUserType());
        }
    }
}