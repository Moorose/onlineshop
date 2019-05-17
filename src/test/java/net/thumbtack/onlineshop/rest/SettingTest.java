package net.thumbtack.onlineshop.rest;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.SettingResponse;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SettingTest {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    @Value("${min_password_length}")
    private int min_password_length;
    @Value("${max_name_length}")
    private int max_name_length;

    private String adminCookie;
    private String clientCookie;

    @Before
    public void before() {
        commonClearDatabaseNode.clearDatabase();
        AdminRegistrationRequest adminRegistrationRequest = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "Adminlogin", "password");
        ResponseEntity<AdminRegistrationResponse> adminRegistrationResponseResponseEntity = restTemplate.postForEntity("/api/admins", adminRegistrationRequest, AdminRegistrationResponse.class);
        adminCookie = adminRegistrationResponseResponseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        adminCookie = adminCookie.substring(adminCookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, adminRegistrationResponseResponseEntity.getStatusCode());
        assertNotNull(adminCookie);
        assertTrue(adminRegistrationResponseResponseEntity.getBody().getId() != 0);
        assertEquals(adminRegistrationRequest.getFirstName(), adminRegistrationResponseResponseEntity.getBody().getFirstName());
        assertEquals(adminRegistrationRequest.getPosition(), adminRegistrationResponseResponseEntity.getBody().getPosition());

        ClientRegistrationRequest clientRegistrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        ResponseEntity<ClientRegistrationResponse> clientRegistrationResponseResponseEntity = restTemplate.postForEntity("/api/clients", clientRegistrationRequest, ClientRegistrationResponse.class);
        clientCookie = clientRegistrationResponseResponseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        clientCookie = clientCookie.substring(clientCookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, clientRegistrationResponseResponseEntity.getStatusCode());
    }

    @Test
    public void get_setting_by_client() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<SettingResponse> rootCategoryResponse = restTemplate.exchange("/api/settings", HttpMethod.GET, requestEntity, SettingResponse.class);
        assertEquals(HttpStatus.OK, rootCategoryResponse.getStatusCode());
        assertEquals(min_password_length, rootCategoryResponse.getBody().getMinPasswordLength());
        assertEquals(max_name_length, rootCategoryResponse.getBody().getMaxNameLength());
    }

    @Test
    public void get_setting_by_admin() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + adminCookie);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<SettingResponse> rootCategoryResponse = restTemplate.exchange("/api/settings", HttpMethod.GET, requestEntity, SettingResponse.class);
        assertEquals(HttpStatus.OK, rootCategoryResponse.getStatusCode());
        assertEquals(min_password_length, rootCategoryResponse.getBody().getMinPasswordLength());
        assertEquals(max_name_length, rootCategoryResponse.getBody().getMaxNameLength());
    }

    @Test
    public void get_setting_without_cookie() {
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<SettingResponse> rootCategoryResponse = restTemplate.exchange("/api/settings", HttpMethod.GET, requestEntity, SettingResponse.class);
        assertEquals(HttpStatus.OK, rootCategoryResponse.getStatusCode());
        assertEquals(min_password_length, rootCategoryResponse.getBody().getMinPasswordLength());
        assertEquals(max_name_length, rootCategoryResponse.getBody().getMaxNameLength());
    }

}
