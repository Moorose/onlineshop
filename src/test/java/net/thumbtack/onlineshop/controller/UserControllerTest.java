package net.thumbtack.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.edit.EditAdminProfileRequest;
import net.thumbtack.onlineshop.dto.edit.EditClientProfileRequest;
import net.thumbtack.onlineshop.dto.user.*;
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
import java.util.ArrayList;
import java.util.List;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

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
    public void getAccountsInfo() throws Exception {
        ClientRegistrationRequest registrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        MvcResult registrationResult = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        Cookie cookie = registrationResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(cookie);
        ClientRegistrationResponse registrationResponse = objectMapper.readValue(registrationResult.getResponse().getContentAsString(), ClientRegistrationResponse.class);
        MvcResult infoResult = mockMvc.perform(get("/api/accounts")
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        ClientRegistrationResponse infoResponse = objectMapper.readValue(infoResult.getResponse().getContentAsString(), ClientRegistrationResponse.class);
        assertEquals(registrationResponse, infoResponse);
    }

    @Test
    public void getAllClients() throws Exception {
        List<ClientRegistrationRequest> requests = new ArrayList<>();
        requests.add(new ClientRegistrationRequest("Клиент", "Фамилия", null, "client1@gmail.com", "ClientAddress", "+79131533461", "ClientLoginI", "password123"));
        requests.add(new ClientRegistrationRequest("Клиент", "Фамилия", null, "client2@gmail.com", "ClientAddress", "7-913-153-34-62", "ClientLoginII", "password123"));
        requests.add(new ClientRegistrationRequest("Клиент", "Фамилия", null, "client3@gmail.com", "ClientAddress", "7-913-153-34-63", "ClientLoginIII", "password123"));
        requests.add(new ClientRegistrationRequest("Клиент", "Фамилия", null, "client4@gmail.com", "ClientAddress", "7-913-153-34-64", "ClientLoginIV", "password123"));
        for (ClientRegistrationRequest request : requests) {
            mockMvc.perform(post("/api/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(cookie().exists(COOKIE_NAME))
                    .andReturn();
        }
        AdminRegistrationRequest request = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "Adminlogin", "password");
        MvcResult registrationResult = mockMvc.perform(post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        Cookie cookie = registrationResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(cookie);
        MvcResult clientsResult = mockMvc.perform(get("/api/clients")
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        List<GetClientsInfoResponse> infoResponse = objectMapper.readValue(clientsResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, GetClientsInfoResponse.class));
        for (int i = 0; i < 4; i++) {
            assertEquals(requests.get(i).getEmail(), infoResponse.get(i).getEmail());
            assertEquals(requests.get(i).getAddress(), infoResponse.get(i).getAddress());
            assertEquals(requests.get(i).getPhone().replaceAll("[+ -]", ""), infoResponse.get(i).getPhone());
        }
    }

    @Test
    public void editAdminProfile() throws Exception {
        AdminRegistrationRequest registrationRequest = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "Adminlogin", "password");
        MvcResult registrationResult = mockMvc.perform(post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        Cookie cookie = registrationResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(cookie);
        EditAdminProfileRequest editRequest = new EditAdminProfileRequest("АААААдминистратор", "Фамилия", null, "admin", "password", "password123");
        MvcResult editResult = mockMvc.perform(put("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editRequest))
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        AdminRegistrationResponse response = objectMapper.readValue(editResult.getResponse().getContentAsString(), AdminRegistrationResponse.class);
        assertNotEquals(registrationRequest.getFirstName(), response.getFirstName());
        assertEquals(editRequest.getFirstName(), response.getFirstName());
        assertEquals(editRequest.getPatronymic(), response.getPatronymic());
    }

    @Test
    public void editClientProfile() throws Exception {
        ClientRegistrationRequest registrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        MvcResult registrationResult = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        Cookie cookie = registrationResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(cookie);
        EditClientProfileRequest editRequest = new EditClientProfileRequest("КККлиент", "Фамилия", null, "client@gmail.com", "ClientAddress", "+84564569895", "password123", "123password");
        MvcResult editResult = mockMvc.perform(put("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editRequest))
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        ClientRegistrationResponse response = objectMapper.readValue(editResult.getResponse().getContentAsString(), ClientRegistrationResponse.class);
        assertNotEquals(registrationRequest.getFirstName(), response.getFirstName());
        assertEquals(editRequest.getFirstName(), response.getFirstName());
        assertEquals(editRequest.getPatronymic(), response.getPatronymic());
        assertNotEquals(registrationRequest.getPhone().replaceAll("[+ -]", ""), response.getPhone());
    }
}