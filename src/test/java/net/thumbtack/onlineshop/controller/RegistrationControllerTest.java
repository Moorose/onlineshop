package net.thumbtack.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
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

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

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
    public void adminRegistration() throws Exception {
        AdminRegistrationRequest request = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "Adminlogin", "password");
        MvcResult result = mockMvc.perform(post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        AdminRegistrationResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AdminRegistrationResponse.class);
        assertEquals(request.getFirstName(), response.getFirstName());
        assertEquals(request.getLastName(), response.getLastName());
        assertEquals(request.getPosition(), response.getPosition());
    }

    @Test
    public void clientRegistration() throws Exception {
        ClientRegistrationRequest request = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        MvcResult result = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        ClientRegistrationResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ClientRegistrationResponse.class);
        assertEquals(request.getFirstName(), response.getFirstName());
        assertEquals(request.getLastName(), response.getLastName());
        assertEquals(request.getEmail(), response.getEmail());
    }
}