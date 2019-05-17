package net.thumbtack.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.OnlineShopExceptionResponse;
import net.thumbtack.onlineshop.dto.deposit.AddMoneyRequest;
import net.thumbtack.onlineshop.dto.product.*;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
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
public class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    private Cookie clientCookie;
    private Cookie adminCookie;

    @Before
    public void before() throws Exception {
        commonClearDatabaseNode.clearDatabase();
        ClientRegistrationRequest request = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        MvcResult result = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        ClientRegistrationResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ClientRegistrationResponse.class);
        clientCookie = result.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(clientCookie);
        AdminRegistrationRequest adminRegistrationRequest = new AdminRegistrationRequest("Администратор", "Фамилия", "Отчество", "admin", "Adminlogin", "password");
        MvcResult registrationResult = mockMvc.perform(post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        adminCookie = registrationResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(adminCookie);
    }

    @Test
    public void addMoney() throws Exception {
        AddMoneyRequest request = new AddMoneyRequest(100);
        MvcResult result = mockMvc.perform(put("/api/deposits")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        ClientRegistrationResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ClientRegistrationResponse.class);
        assertEquals(request.getDeposit(), response.getDeposit());
    }

    @Test
    public void getDeposit() throws Exception {
        AddMoneyRequest request = new AddMoneyRequest(100);
        MvcResult result = mockMvc.perform(put("/api/deposits")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        ClientRegistrationResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ClientRegistrationResponse.class);
        assertEquals(request.getDeposit(), response.getDeposit());
        MvcResult result2 = mockMvc.perform(get("/api/deposits")
                .cookie(clientCookie))
                .andExpect(status().isOk())
                .andReturn();
        ClientRegistrationResponse response2 = objectMapper.readValue(result2.getResponse().getContentAsString(), ClientRegistrationResponse.class);
        assertEquals(response.getDeposit(), response2.getDeposit());
    }

    private List<SimpleProductResponse> addProduct() throws Exception {
        List<SimpleProductResponse> productResponseList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            AddProductRequest request = new AddProductRequest(i + "_Product", 100, 10, null);
            MvcResult addProductResult = mockMvc.perform(post("/api/products")
                    .cookie(adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();
            productResponseList.add(objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class));
        }
        return productResponseList;
    }

    @Test
    public void addProductToBasket() throws Exception {
        List<SimpleProductResponse> productResponses = addProduct();
        SimpleProductResponse product = productResponses.get(0);
        BuyProductRequest request = new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 100000);
        MvcResult result = mockMvc.perform(post("/api/baskets")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
//                .content("{\"id\":"+product.getId()+",\"name\":\"0_Product\",\"price\":100}"))
                .andExpect(status().isOk())
                .andReturn();
        List<BuyProductResponse> responses = objectMapper.readValue(result.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, BuyProductResponse.class));
        assertEquals(product.getId(), responses.get(0).getId());
        assertEquals(100000, responses.get(0).getCount());
        assertNotEquals(product.getCount(), responses.get(0).getCount());
    }

    @Test
    public void addNonexistentProductToBasket() throws Exception {
        BuyProductRequest request = new BuyProductRequest(15641, "SomeProd", 1, 100000);
        MvcResult result = mockMvc.perform(post("/api/baskets")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
        OnlineShopExceptionResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), OnlineShopExceptionResponse.class);
        assertEquals(OnlineShopErrorCode.DATABASE_FIND_PRODUCT_BY_ID_ERROR, response.getErrorCode());
    }

    @Test
    public void deleteProductFromBasket() throws Exception {
        List<SimpleProductResponse> productResponses = addProduct();
        SimpleProductResponse product = productResponses.get(0);
        BuyProductRequest request = new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 100000);
        MvcResult result = mockMvc.perform(post("/api/baskets")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        List<BuyProductResponse> responses = objectMapper.readValue(result.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, BuyProductResponse.class));
        assertEquals(product.getId(), responses.get(0).getId());
        assertEquals(100000, responses.get(0).getCount());
        assertNotEquals(product.getCount(), responses.get(0).getCount());
        MvcResult deleteResult = mockMvc.perform(delete("/api/baskets/{id}", responses.get(0).getId())
                .cookie(clientCookie))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("{}", deleteResult.getResponse().getContentAsString());
    }

    @Test
    public void changeProductCount() throws Exception {
        List<SimpleProductResponse> productResponses = addProduct();
        SimpleProductResponse product = productResponses.get(0);
        BuyProductRequest request = new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 100000);
        MvcResult result = mockMvc.perform(post("/api/baskets")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        List<BuyProductResponse> responses = objectMapper.readValue(result.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, BuyProductResponse.class));
        assertEquals(product.getId(), responses.get(0).getId());
        assertEquals(100000, responses.get(0).getCount());
        assertNotEquals(product.getCount(), responses.get(0).getCount());
        ChangeProductRequest productRequest = new ChangeProductRequest(product.getId(), product.getName(), product.getPrice(), 10);
        MvcResult changeResult = mockMvc.perform(put("/api/baskets")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andReturn();
        List<BuyProductResponse> newResponses = objectMapper.readValue(changeResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, BuyProductResponse.class));
        assertEquals(10, newResponses.get(0).getCount());
        assertEquals(product.getId(), newResponses.get(0).getId());
    }

    @Test
    public void getProductsFromBasket() throws Exception {
        List<SimpleProductResponse> productResponses = addProduct();
        for (int i = 0; i < 3; i++) {
            SimpleProductResponse product = productResponses.get(i);
            BuyProductRequest request = new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 100000);
            MvcResult result = mockMvc.perform(post("/api/baskets")
                    .cookie(clientCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();
            List<BuyProductResponse> responses = objectMapper.readValue(result.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, BuyProductResponse.class));
            assertEquals(product.getId(), responses.get(i).getId());
            assertEquals((int) request.getCountWithSafe(), responses.get(i).getCount());
        }
        MvcResult result = mockMvc.perform(get("/api/baskets")
                .cookie(clientCookie))
                .andExpect(status().isOk())
                .andReturn();
        List<BuyProductResponse> responses = objectMapper.readValue(result.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, BuyProductResponse.class));
        assertEquals(3, responses.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(productResponses.get(i).getId(), responses.get(i).getId());
        }
    }

    @Test
    public void buyProduct() throws Exception {
        addMoneyToDeposit(500);
        List<SimpleProductResponse> productResponses = addProduct();
        SimpleProductResponse product = productResponses.get(0);
        BuyProductRequest request = new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 3);
        MvcResult result = mockMvc.perform(post("/api/purchases")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        BuyProductResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), BuyProductResponse.class);
        assertEquals(request.getId(), response.getId());
        MvcResult result2 = mockMvc.perform(get("/api/deposits")
                .cookie(clientCookie))
                .andExpect(status().isOk())
                .andReturn();
        ClientRegistrationResponse response2 = objectMapper.readValue(result2.getResponse().getContentAsString(), ClientRegistrationResponse.class);
        assertEquals(200, response2.getDeposit());
    }

    @Test
    public void buyProduct_withError() throws Exception {
        addMoneyToDeposit(100);
        List<SimpleProductResponse> productResponses = addProduct();
        SimpleProductResponse product = productResponses.get(0);
        BuyProductRequest request = new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 3);
        MvcResult result = mockMvc.perform(post("/api/purchases")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
        OnlineShopExceptionResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), OnlineShopExceptionResponse.class);
        assertEquals(OnlineShopErrorCode.NOT_ENOUGH_MONEY_ON_DEPOSIT, response.getErrorCode());
    }

    @Test
    public void buyProductsFromBasket() throws Exception {
        addMoneyToDeposit(1000);
        List<SimpleProductResponse> productResponses = addProduct();
        List<BuyProductRequest> productRequest = new ArrayList<>();
        for (SimpleProductResponse product : productResponses) {
            productRequest.add(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 3));
        }
        for (BuyProductRequest request : productRequest) {
            mockMvc.perform(post("/api/baskets")
                    .cookie(clientCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
        MvcResult buyResult = mockMvc.perform(post("/api/purchases/baskets")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andReturn();
        BuyProductsResponse response = objectMapper.readValue(buyResult.getResponse().getContentAsString(), BuyProductsResponse.class);
        System.out.println(response);
        assertEquals(3, response.getBought().size());
        assertTrue(response.getRemaining().isEmpty());
    }

    private void addMoneyToDeposit(int money) throws Exception {
        AddMoneyRequest request = new AddMoneyRequest(money);
        MvcResult result = mockMvc.perform(put("/api/deposits")
                .cookie(clientCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        ClientRegistrationResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ClientRegistrationResponse.class);
        assertEquals(request.getDeposit(), response.getDeposit());
    }
}