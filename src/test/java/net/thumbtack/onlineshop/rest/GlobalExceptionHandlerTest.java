package net.thumbtack.onlineshop.rest;


import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.OnlineShopExceptionResponse;
import net.thumbtack.onlineshop.dto.ValidationErrorResponse;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GlobalExceptionHandlerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    @Before
    public void before() {
        commonClearDatabaseNode.clearDatabase();
    }

    @Test
    public void handleOnlineShopException() throws Exception {
        AdminRegistrationRequest request = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "Админlogin", "password");
        ResponseEntity<AdminRegistrationResponse> responseAddMoney = restTemplate.postForEntity("/api/admins", request, AdminRegistrationResponse.class);
        String cookie = responseAddMoney.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        cookie = cookie.substring(cookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, responseAddMoney.getStatusCode());

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<OnlineShopExceptionResponse> response = restTemplate.exchange("/api/deposits", HttpMethod.GET, requestEntity, OnlineShopExceptionResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(OnlineShopErrorCode.CLIENT_ERROR_AUTHENTICATION, response.getBody().getErrorCode());
    }

    @Test
    public void handleNoHandlerFoundException() {
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity requestEntity = new HttpEntity(new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "791351533464", "ClientLogin", "password123"), requestHeaders);
        ResponseEntity<OnlineShopExceptionResponse> response = restTemplate.exchange("/api/users", HttpMethod.GET, requestEntity, OnlineShopExceptionResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(OnlineShopErrorCode.WRONG_URL, response.getBody().getErrorCode());
    }

    @Test
    public void handleMethodArgumentNotValidTest() {
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity requestEntity = new HttpEntity(new ClientRegistrationRequest("Клие+нт", "Фамiлия", "Отчество", "client@gmail.com", "ClientAddress", "79135153464", "ClientLogin", "password123"), requestHeaders);
        ResponseEntity<ValidationErrorResponse> response = restTemplate.exchange("/api/clients", HttpMethod.POST, requestEntity, ValidationErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(OnlineShopErrorCode.VALIDATION_ERROR, response.getBody().getResponses().get(0).getErrorCode());

    }

    @Test
    public void handleHttpMediaTypeNotSupportedTest() {
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity requestEntity = new HttpEntity("{}", requestHeaders);
        ResponseEntity<OnlineShopExceptionResponse> response = restTemplate.exchange("/api/clients", HttpMethod.POST, requestEntity, OnlineShopExceptionResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(OnlineShopErrorCode.EMPTY_REQUEST, response.getBody().getErrorCode());
    }

}