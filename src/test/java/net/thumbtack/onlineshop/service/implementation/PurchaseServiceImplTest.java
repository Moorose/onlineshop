package net.thumbtack.onlineshop.service.implementation;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.deposit.AddMoneyRequest;
import net.thumbtack.onlineshop.dto.product.*;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.ProductService;
import net.thumbtack.onlineshop.service.interfaces.PurchaseService;
import net.thumbtack.onlineshop.service.interfaces.RegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PurchaseServiceImplTest {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    private String adminCookie;
    private String clientCookie;
    private List<SimpleProductResponse> productList;

    @Before
    public void before() throws OnlineShopException {
        commonClearDatabaseNode.clearDatabase();

        AdminRegistrationRequest adminRegistrationRequest = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "AdminLogin", "password");
        AdminRegistrationResponse adminRegistrationResponse = registrationService.adminRegistration(adminRegistrationRequest);
        assertTrue(adminRegistrationResponse.getId() != 0);
        adminCookie = adminRegistrationResponse.getJavaSessionId();

        ClientRegistrationRequest clientRegistrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        ClientRegistrationResponse clientRegistrationResponse = registrationService.clientRegistration(clientRegistrationRequest);
        assertTrue(clientRegistrationResponse.getId() != 0);
        clientCookie = clientRegistrationResponse.getJavaSessionId();

        productList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AddProductRequest addProductRequest = new AddProductRequest("Product_" + i, 10 * i, 10, null);
            productList.add(productService.addProduct(adminCookie, addProductRequest));
            assertTrue(productList.get(i).getId() != 0);
        }
    }

    @Test
    public void add_money_and_get_deposit() throws OnlineShopException {
        ClientRegistrationResponse response = purchaseService.addMoney(clientCookie, new AddMoneyRequest(100));
        assertEquals(100, response.getDeposit());
        ClientRegistrationResponse deposit = purchaseService.getDeposit(clientCookie);
        assertEquals(response.getId(), deposit.getId());
        assertEquals(response.getDeposit(), deposit.getDeposit());
    }


    @Test
    public void add_to_basket_change_count_get_basket() throws OnlineShopException {
        SimpleProductResponse product = productList.get(1);
        List<BuyProductResponse> buyProductResponses = purchaseService.addProductToBasket(clientCookie, new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
        assertEquals(1, buyProductResponses.get(0).getCount());
        assertEquals(product.getId(), buyProductResponses.get(0).getId());
        List<BuyProductResponse> addProductResponses = purchaseService.changeProductCount(clientCookie, new ChangeProductRequest(product.getId(), product.getName(), product.getPrice(), 10));
        assertEquals(10, addProductResponses.get(0).getCount());
        assertEquals(product.getId(), addProductResponses.get(0).getId());
        List<BuyProductResponse> productsFromBasket = purchaseService.getProductsFromBasket(clientCookie);
        assertEquals(10, productsFromBasket.get(0).getCount());
        assertEquals(product.getId(), productsFromBasket.get(0).getId());
    }

    @Test
    public void add_to_basket_delete_get_basket() throws OnlineShopException {
        SimpleProductResponse product = productList.get(1);
        List<BuyProductResponse> buyProductResponses = purchaseService.addProductToBasket(clientCookie, new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
        assertEquals(1, buyProductResponses.get(0).getCount());
        assertEquals(product.getId(), buyProductResponses.get(0).getId());
        purchaseService.deleteProductFromBasket(clientCookie, product.getId());
        List<BuyProductResponse> productsFromBasket = purchaseService.getProductsFromBasket(clientCookie);
        assertTrue(productsFromBasket.isEmpty());
    }

    @Test
    public void buy_solo_product() throws OnlineShopException {
        ClientRegistrationResponse response = purchaseService.addMoney(clientCookie, new AddMoneyRequest(100));
        assertEquals(100, response.getDeposit());
        SimpleProductResponse product = productList.get(1);
        BuyProductResponse buyProduct = purchaseService.buyProduct(clientCookie, new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
        assertEquals(product.getId(), buyProduct.getId());
        assertEquals(1, buyProduct.getCount());
    }

    @Test
    public void buy_solo_product_without_money() {
        SimpleProductResponse product = productList.get(1);
        try {
            purchaseService.buyProduct(clientCookie, new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
            fail();
        } catch (OnlineShopException e) {
            assertEquals(OnlineShopErrorCode.NOT_ENOUGH_MONEY_ON_DEPOSIT, e.getOnlineShopErrorCode());
        }
    }

    @Test
    public void add_to_basket_many_product_and_buy() throws OnlineShopException {
        int money = 10000;
        ClientRegistrationResponse response = purchaseService.addMoney(clientCookie, new AddMoneyRequest(money));
        assertEquals(money, response.getDeposit());
        int i = 0;
        List<BuyProductRequest> buyProductRequestList = new ArrayList<>();
        for (SimpleProductResponse product : productList) {
            List<BuyProductResponse> buyProductResponses = purchaseService.addProductToBasket(clientCookie, new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
            assertEquals(1, buyProductResponses.get(i).getCount());
            assertEquals(product.getId(), buyProductResponses.get(i).getId());
            buyProductRequestList.add(new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
            i++;
        }
        BuyProductsResponse buyProductsResponse = purchaseService.buyProduct(clientCookie, buyProductRequestList);
        assertEquals(buyProductRequestList.size(), buyProductsResponse.getBought().size());
        assertTrue(buyProductsResponse.getRemaining().isEmpty());
        List<BuyProductResponse> productsFromBasket = purchaseService.getProductsFromBasket(clientCookie);
        assertTrue(productsFromBasket.isEmpty());
    }

    @Test
    public void add_to_basket_many_product_and_buy_without_money() throws OnlineShopException {
        int i = 0;
        List<BuyProductRequest> buyProductRequestList = new ArrayList<>();
        for (SimpleProductResponse product : productList) {
            List<BuyProductResponse> buyProductResponses = purchaseService.addProductToBasket(clientCookie, new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
            assertEquals(1, buyProductResponses.get(i).getCount());
            assertEquals(product.getId(), buyProductResponses.get(i).getId());
            buyProductRequestList.add(new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
            i++;
        }
        try {
            purchaseService.buyProduct(clientCookie, buyProductRequestList);
            fail();
        } catch (OnlineShopException e) {
            assertEquals(OnlineShopErrorCode.NOT_ENOUGH_MONEY_ON_DEPOSIT, e.getOnlineShopErrorCode());
        }
    }

    @Test
    public void add_to_basket_many_product_deleted_and_buy() throws OnlineShopException {
        int money = 10000;
        ClientRegistrationResponse response = purchaseService.addMoney(clientCookie, new AddMoneyRequest(money));
        assertEquals(money, response.getDeposit());
        int i = 0;
        List<BuyProductRequest> buyProductRequestList = new ArrayList<>();
        for (SimpleProductResponse product : productList) {
            List<BuyProductResponse> buyProductResponses = purchaseService.addProductToBasket(clientCookie, new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
            assertEquals(1, buyProductResponses.get(i).getCount());
            assertEquals(product.getId(), buyProductResponses.get(i).getId());
            buyProductRequestList.add(new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
            i++;
        }
        productService.deleteProductById(adminCookie, productList.get(0).getId());
        BuyProductsResponse buyProductsResponse = purchaseService.buyProduct(clientCookie, buyProductRequestList);
        assertEquals(buyProductRequestList.size(), buyProductsResponse.getBought().size() + buyProductsResponse.getRemaining().size());
        assertFalse(buyProductsResponse.getRemaining().isEmpty());
        assertEquals(productList.get(0).getId(), buyProductsResponse.getRemaining().get(0).getId());
        List<BuyProductResponse> productsFromBasket = purchaseService.getProductsFromBasket(clientCookie);
        assertFalse(productsFromBasket.isEmpty());
        assertEquals(productList.get(0).getId(), productsFromBasket.get(0).getId());
    }

    @Test
    public void buy_product_with_count() throws OnlineShopException {
        int money = 10000;
        ClientRegistrationResponse response = purchaseService.addMoney(clientCookie, new AddMoneyRequest(money));
        assertEquals(money, response.getDeposit());
        int i = 0;
        List<BuyProductRequest> buyProductRequestList = new ArrayList<>();
        for (SimpleProductResponse product : productList) {
            List<BuyProductResponse> buyProductResponses = purchaseService.addProductToBasket(clientCookie, new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 5));
            assertEquals(product.getId(), buyProductResponses.get(i).getId());
            buyProductRequestList.add(new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
            i++;
        }
        BuyProductsResponse buyProductsResponse = purchaseService.buyProduct(clientCookie, buyProductRequestList);
        assertEquals(10, buyProductsResponse.getBought().size());
        assertEquals(5, buyProductsResponse.getBought().get(5).getCount());
        assertTrue(buyProductsResponse.getRemaining().isEmpty());
    }


}