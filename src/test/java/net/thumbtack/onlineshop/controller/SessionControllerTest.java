package net.thumbtack.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.LoginRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    @Before
    public void before() {
        commonClearDatabaseNode.clearDatabase();
    }

    @Test
    public void logoutAndLoginClient() throws Exception {
        ClientRegistrationRequest registrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "Client_Address", "79131533464", "ClientLogin", "password123");
        MvcResult registrationResult = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        Cookie cookie = registrationResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(cookie);
        mockMvc.perform(delete("/api/sessions").cookie(cookie)).andExpect(status().isOk());
        LoginRequest loginRequest = new LoginRequest(registrationRequest.getLogin(), registrationRequest.getPassword());
        MvcResult loginResult = mockMvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        Cookie newCookie = loginResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(newCookie);
        assertNotEquals(cookie.getValue(), newCookie.getValue());
    }

    @Test
    public void logoutAndLoginAdmin() throws Exception {
        AdminRegistrationRequest registrationRequest = new AdminRegistrationRequest("Админ", "Фамилия", null, "admin", "Adminlogin", "password");
        MvcResult registrationResult = mockMvc.perform(post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        Cookie cookie = registrationResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(cookie);
        mockMvc.perform(delete("/api/sessions").cookie(cookie)).andExpect(status().isOk());
        LoginRequest loginRequest = new LoginRequest(registrationRequest.getLogin(), registrationRequest.getPassword());
        MvcResult loginResult = mockMvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        Cookie newCookie = loginResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(newCookie);
        assertNotEquals(cookie.getValue(), newCookie.getValue());
    }

    @Test
    public void reLoginClient() throws Exception {
        ClientRegistrationRequest registrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "Client_Address", "79131533464", "ClientLogin", "password123");
        MvcResult registrationResult = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        Cookie cookie = registrationResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(cookie);
        LoginRequest loginRequest = new LoginRequest(registrationRequest.getLogin(), registrationRequest.getPassword());
        for (int i = 0; i < 3; i++) {
            MvcResult loginResult = mockMvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(cookie().exists(COOKIE_NAME))
                    .andReturn();
            Cookie newCookie = loginResult.getResponse().getCookie(COOKIE_NAME);
            assertNotNull(newCookie);
            assertNotEquals(cookie.getValue(), newCookie.getValue());
        }

    }

}