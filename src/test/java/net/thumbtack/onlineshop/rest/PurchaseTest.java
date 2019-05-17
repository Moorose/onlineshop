package net.thumbtack.onlineshop.rest;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.EmptyResponse;
import net.thumbtack.onlineshop.dto.OnlineShopExceptionResponse;
import net.thumbtack.onlineshop.dto.category.AddCategoryRequest;
import net.thumbtack.onlineshop.dto.category.CategoryResponse;
import net.thumbtack.onlineshop.dto.deposit.AddMoneyRequest;
import net.thumbtack.onlineshop.dto.product.*;
import net.thumbtack.onlineshop.dto.report.ReportResponse;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PurchaseTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

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
    public void add_money() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity requestEntity = new HttpEntity(new AddMoneyRequest(100), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> rootCategoryResponse = restTemplate.exchange("/api/deposits", HttpMethod.PUT, requestEntity, ClientRegistrationResponse.class);
        assertEquals(100, rootCategoryResponse.getBody().getDeposit());
    }

    @Test
    public void get_deposit() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity requestEntity = new HttpEntity(new AddMoneyRequest(100), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> rootCategoryResponse = restTemplate.exchange("/api/deposits", HttpMethod.PUT, requestEntity, ClientRegistrationResponse.class);
        assertEquals(100, rootCategoryResponse.getBody().getDeposit());


        HttpEntity getRequestEntity = new HttpEntity(new AddMoneyRequest(100), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> getResponse = restTemplate.exchange("/api/deposits", HttpMethod.GET, getRequestEntity, ClientRegistrationResponse.class);
        assertEquals(rootCategoryResponse.getBody().getDeposit(), getResponse.getBody().getDeposit());
    }

    private int[] insertCategory(int count, String nameCategory) {
        int[] catId = new int[count];
        for (int i = 0; i < catId.length; i++) {
            HttpHeaders addRequestHeaders = new HttpHeaders();
            addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + adminCookie);
            HttpEntity addRequestEntity = new HttpEntity(new AddCategoryRequest(nameCategory + i, 0), addRequestHeaders);
            ResponseEntity<CategoryResponse> addRootCategoryResponse = restTemplate.exchange("/api/categories", HttpMethod.POST, addRequestEntity, CategoryResponse.class);
            assertEquals(HttpStatus.OK, addRootCategoryResponse.getStatusCode());
            catId[i] = addRootCategoryResponse.getBody().getId();
        }
        return catId;
    }

    private List<SimpleProductResponse> insertProduct() {
        List<SimpleProductResponse> productResponses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int[] category = insertCategory(10, "Product_" + i + "_Category_");
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie", COOKIE_NAME + "=" + adminCookie);
            HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product_" + i, 100, 10, category), requestHeaders);
            ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
            assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
            assertTrue(rootSimpleProductResponse.getBody().getId() != 0);
            productResponses.add(rootSimpleProductResponse.getBody());
        }
        return productResponses;
    }

    @Test
    public void add_product_to_basket() {
        List<SimpleProductResponse> productResponseList = insertProduct();
        SimpleProductResponse product = productResponseList.get(0);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 100000), requestHeaders);
        ResponseEntity<List<BuyProductResponse>> addProductToBasketResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
        });
        assertEquals(HttpStatus.OK, addProductToBasketResponse.getStatusCode());
        assertEquals(product.getId(), addProductToBasketResponse.getBody().get(0).getId());
        assertEquals(100000, addProductToBasketResponse.getBody().get(0).getCount());
        assertNotEquals(product.getCount(), addProductToBasketResponse.getBody().get(0).getCount());
    }

    @Test
    public void add_non_existent_product_to_basket() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(1642, "NullProd", 10, 100000), requestHeaders);
        ResponseEntity<OnlineShopExceptionResponse> errorResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, OnlineShopExceptionResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        assertEquals(OnlineShopErrorCode.DATABASE_FIND_PRODUCT_BY_ID_ERROR, errorResponse.getBody().getErrorCode());
    }

    @Test
    public void delete_product_from_basket() {
        List<SimpleProductResponse> productResponseList = insertProduct();
        SimpleProductResponse product = productResponseList.get(0);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 100000), requestHeaders);
        ResponseEntity<List<BuyProductResponse>> addProductToBasketResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
        });
        assertEquals(HttpStatus.OK, addProductToBasketResponse.getStatusCode());
        assertEquals(product.getId(), addProductToBasketResponse.getBody().get(0).getId());
        assertEquals(100000, addProductToBasketResponse.getBody().get(0).getCount());
        assertNotEquals(product.getCount(), addProductToBasketResponse.getBody().get(0).getCount());
        int id = addProductToBasketResponse.getBody().get(0).getId();
        ResponseEntity<EmptyResponse> deleteResponse = restTemplate.exchange("/api/baskets/{id}", HttpMethod.DELETE, requestEntity, EmptyResponse.class, id);
        assertEquals(HttpStatus.OK, addProductToBasketResponse.getStatusCode());
    }

    @Test
    public void change_product_count() {
        List<SimpleProductResponse> productResponseList = insertProduct();
        SimpleProductResponse product = productResponseList.get(0);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 100000), requestHeaders);
        ResponseEntity<List<BuyProductResponse>> addProductToBasketResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
        });
        assertEquals(HttpStatus.OK, addProductToBasketResponse.getStatusCode());
        assertEquals(product.getId(), addProductToBasketResponse.getBody().get(0).getId());
        assertEquals(100000, addProductToBasketResponse.getBody().get(0).getCount());
        assertNotEquals(product.getCount(), addProductToBasketResponse.getBody().get(0).getCount());

        HttpEntity changeRequestEntity = new HttpEntity(new ChangeProductRequest(product.getId(), product.getName(), product.getPrice(), 1), requestHeaders);
        ResponseEntity<List<BuyProductResponse>> changeResponse = restTemplate.exchange("/api/baskets", HttpMethod.PUT, changeRequestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
        });
        assertEquals(HttpStatus.OK, changeResponse.getStatusCode());
        assertEquals(product.getId(), changeResponse.getBody().get(0).getId());
        assertEquals(addProductToBasketResponse.getBody().get(0).getId(), changeResponse.getBody().get(0).getId());
        assertEquals(1, changeResponse.getBody().get(0).getCount());
    }

    @Test
    public void get_products_from_basket() {
        List<SimpleProductResponse> productResponseList = insertProduct();
        for (int i = 0; i < 3; i++) {
            SimpleProductResponse product = productResponseList.get(i);
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
            HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 100000), requestHeaders);
            ResponseEntity<List<BuyProductResponse>> addProductToBasketResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
            });
            assertEquals(HttpStatus.OK, addProductToBasketResponse.getStatusCode());
        }
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<List<BuyProductResponse>> getResponse = restTemplate.exchange("/api/baskets", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
        });
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(3, getResponse.getBody().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(productResponseList.get(i).getId(), getResponse.getBody().get(i).getId());
            assertNotEquals(productResponseList.get(i).getCount(), getResponse.getBody().get(i).getCount());
            assertEquals(productResponseList.get(i).getPrice(), getResponse.getBody().get(i).getPrice());
        }
    }

    @Test
    public void buy_product() {
        List<SimpleProductResponse> productResponseList = insertProduct();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);

        HttpEntity addMoneyRequest = new HttpEntity(new AddMoneyRequest(500), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> addMoneyResponse = restTemplate.exchange("/api/deposits", HttpMethod.PUT, addMoneyRequest, ClientRegistrationResponse.class);
        assertEquals(500, addMoneyResponse.getBody().getDeposit());

        SimpleProductResponse product = productResponseList.get(0);
        HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 3), requestHeaders);
        ResponseEntity<BuyProductResponse> responseEntity = restTemplate.exchange("/api/purchases", HttpMethod.POST, requestEntity, BuyProductResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        assertEquals(product.getId(), responseEntity.getBody().getId());
        assertEquals(3, responseEntity.getBody().getCount());

        HttpEntity getMoneyEntity = new HttpEntity(new AddMoneyRequest(100), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> getMoneyResponse = restTemplate.exchange("/api/deposits", HttpMethod.GET, getMoneyEntity, ClientRegistrationResponse.class);
        assertNotEquals(addMoneyResponse.getBody().getDeposit(), getMoneyResponse.getBody().getDeposit());
        assertEquals(200, getMoneyResponse.getBody().getDeposit());
    }

    @Test
    public void buy_product_without_enough_money() {
        List<SimpleProductResponse> productResponseList = insertProduct();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        SimpleProductResponse product = productResponseList.get(0);
        HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 3), requestHeaders);
        ResponseEntity<OnlineShopExceptionResponse> responseEntity = restTemplate.exchange("/api/purchases", HttpMethod.POST, requestEntity, OnlineShopExceptionResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(OnlineShopErrorCode.NOT_ENOUGH_MONEY_ON_DEPOSIT, responseEntity.getBody().getErrorCode());
    }

    @Test
    public void buy_deleted_product_with_error() {
        List<SimpleProductResponse> productResponseList = insertProduct();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);

        SimpleProductResponse product = productResponseList.get(0);

        HttpHeaders adminRequestHeaders = new HttpHeaders();
        adminRequestHeaders.add("Cookie", COOKIE_NAME + "=" + adminCookie);
        HttpEntity deleteRequestEntity = new HttpEntity(null, adminRequestHeaders);
        ResponseEntity<EmptyResponse> deleteProductResponse = restTemplate.exchange("/api/products/{product_number}", HttpMethod.DELETE, deleteRequestEntity, EmptyResponse.class, product.getId());
        assertEquals(HttpStatus.OK, deleteProductResponse.getStatusCode());

        HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 3), requestHeaders);
        ResponseEntity<OnlineShopExceptionResponse> responseEntity = restTemplate.exchange("/api/purchases", HttpMethod.POST, requestEntity, OnlineShopExceptionResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(OnlineShopErrorCode.THIS_PRODUCT_IS_NOT_AVAILABLE, responseEntity.getBody().getErrorCode());
    }

    @Test
    public void buy_product_without_enough_count() {
        List<SimpleProductResponse> productResponseList = insertProduct();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        SimpleProductResponse product = productResponseList.get(0);
        HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 13), requestHeaders);
        ResponseEntity<OnlineShopExceptionResponse> responseEntity = restTemplate.exchange("/api/purchases", HttpMethod.POST, requestEntity, OnlineShopExceptionResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(OnlineShopErrorCode.NOT_ENOUGH_PRODUCT_IN_STORAGE, responseEntity.getBody().getErrorCode());
    }

    @Test
    public void buy_product_with_wrong_param() {
        List<SimpleProductResponse> productResponseList = insertProduct();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);

        HttpEntity addMoneyRequest = new HttpEntity(new AddMoneyRequest(500), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> addMoneyResponse = restTemplate.exchange("/api/deposits", HttpMethod.PUT, addMoneyRequest, ClientRegistrationResponse.class);
        assertEquals(500, addMoneyResponse.getBody().getDeposit());

        SimpleProductResponse product = productResponseList.get(0);
        HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice() + 10, 3), requestHeaders);
        ResponseEntity<OnlineShopExceptionResponse> responseEntity = restTemplate.exchange("/api/purchases", HttpMethod.POST, requestEntity, OnlineShopExceptionResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(OnlineShopErrorCode.PARAMS_FOR_PRODUCT_ARE_WRONG, responseEntity.getBody().getErrorCode());
    }

    @Test
    public void buy_products_from_basket() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity addMoneyRequest = new HttpEntity(new AddMoneyRequest(5000), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> addMoneyResponse = restTemplate.exchange("/api/deposits", HttpMethod.PUT, addMoneyRequest, ClientRegistrationResponse.class);
        assertEquals(5000, addMoneyResponse.getBody().getDeposit());
        List<BuyProductRequest> productRequestList = new ArrayList<>();
        List<SimpleProductResponse> productResponseList = insertProduct();
        for (int i = 0; i < 3; i++) {
            SimpleProductResponse product = productResponseList.get(i);
            HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice()), requestHeaders);
            ResponseEntity<List<BuyProductResponse>> addResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
            });
            assertEquals(HttpStatus.OK, addResponse.getStatusCode());
            productRequestList.add(new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
        }
        HttpEntity requestEntity = new HttpEntity(productRequestList, requestHeaders);
        ResponseEntity<BuyProductsResponse> buyResponse = restTemplate.exchange("/api/purchases/baskets", HttpMethod.POST, requestEntity, BuyProductsResponse.class);
        List<BuyProductResponse> remaining = buyResponse.getBody().getRemaining();
        List<BuyProductResponse> bought = buyResponse.getBody().getBought();
        assertEquals(productRequestList.size(), bought.size());
        assertEquals(0, remaining.size());
    }

    @Test
    public void buy_product_from_basket_with_wrong_count() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity addMoneyRequest = new HttpEntity(new AddMoneyRequest(1500), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> addMoneyResponse = restTemplate.exchange("/api/deposits", HttpMethod.PUT, addMoneyRequest, ClientRegistrationResponse.class);
        assertEquals(1500, addMoneyResponse.getBody().getDeposit());
        List<BuyProductRequest> productRequestList = new ArrayList<>();
        List<SimpleProductResponse> productResponseList = insertProduct();
        for (int i = 0; i < 3; i++) {
            SimpleProductResponse product = productResponseList.get(i);
            HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 5), requestHeaders);
            ResponseEntity<List<BuyProductResponse>> addResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
            });
            assertEquals(HttpStatus.OK, addResponse.getStatusCode());
            productRequestList.add(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 6));
        }
        HttpEntity requestEntity = new HttpEntity(productRequestList, requestHeaders);

        ResponseEntity<BuyProductsResponse> buyResponse = restTemplate.exchange("/api/purchases/baskets", HttpMethod.POST, requestEntity, BuyProductsResponse.class);
        List<BuyProductResponse> remaining = buyResponse.getBody().getRemaining();
        List<BuyProductResponse> bought = buyResponse.getBody().getBought();
        assertEquals(productRequestList.size(), bought.size());
        assertEquals(5, bought.get(2).getCount());
        assertTrue(remaining.isEmpty());
    }

    @Test
    public void buy_product_from_basket_with_over_count() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity addMoneyRequest = new HttpEntity(new AddMoneyRequest(1500), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> addMoneyResponse = restTemplate.exchange("/api/deposits", HttpMethod.PUT, addMoneyRequest, ClientRegistrationResponse.class);
        assertEquals(1500, addMoneyResponse.getBody().getDeposit());
        List<BuyProductRequest> productRequestList = new ArrayList<>();
        List<SimpleProductResponse> productResponseList = insertProduct();
        for (int i = 0; i < 3; i++) {
            SimpleProductResponse product = productResponseList.get(i);
            HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 1000), requestHeaders);
            ResponseEntity<List<BuyProductResponse>> addResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
            });
            assertEquals(HttpStatus.OK, addResponse.getStatusCode());
            productRequestList.add(new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
        }
        productRequestList.get(0).setCount(3);

        HttpEntity requestEntity = new HttpEntity(productRequestList, requestHeaders);

        ResponseEntity<BuyProductsResponse> buyResponse = restTemplate.exchange("/api/purchases/baskets", HttpMethod.POST, requestEntity, BuyProductsResponse.class);
        List<BuyProductResponse> remaining = buyResponse.getBody().getRemaining();
        List<BuyProductResponse> bought = buyResponse.getBody().getBought();
        productRequestList.forEach(System.out::println);
        System.out.println("              ");
        bought.forEach(System.out::println);
        System.out.println("              ");
        remaining.forEach(System.out::println);
        assertEquals(1, bought.size());
        assertEquals(2, remaining.size());
        assertEquals(3, bought.get(0).getCount());
        assertEquals(1000, remaining.get(0).getCount());
    }

    @Test
    public void buy_product_from_basket_without_enough_money() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity addMoneyRequest = new HttpEntity(new AddMoneyRequest(50), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> addMoneyResponse = restTemplate.exchange("/api/deposits", HttpMethod.PUT, addMoneyRequest, ClientRegistrationResponse.class);
        assertEquals(50, addMoneyResponse.getBody().getDeposit());
        List<BuyProductRequest> productRequestList = new ArrayList<>();
        List<SimpleProductResponse> productResponseList = insertProduct();
        for (int i = 0; i < 3; i++) {
            SimpleProductResponse product = productResponseList.get(i);
            HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice()), requestHeaders);
            ResponseEntity<List<BuyProductResponse>> addResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
            });
            assertEquals(HttpStatus.OK, addResponse.getStatusCode());
            productRequestList.add(new BuyProductRequest(product.getId(), product.getName(), product.getPrice()));
        }
        HttpEntity requestEntity = new HttpEntity(productRequestList, requestHeaders);
        ResponseEntity<OnlineShopExceptionResponse> buyResponse = restTemplate.exchange("/api/purchases/baskets", HttpMethod.POST, requestEntity, OnlineShopExceptionResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, buyResponse.getStatusCode());
        assertEquals(OnlineShopErrorCode.NOT_ENOUGH_MONEY_ON_DEPOSIT, buyResponse.getBody().getErrorCode());
    }

    @Test
    public void buy_product_from_basket_without_error() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity addMoneyRequest = new HttpEntity(new AddMoneyRequest(5000), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> addMoneyResponse = restTemplate.exchange("/api/deposits", HttpMethod.PUT, addMoneyRequest, ClientRegistrationResponse.class);
        assertEquals(5000, addMoneyResponse.getBody().getDeposit());

        List<SimpleProductResponse> productResponseList = insertProduct();
        for (int i = 0; i < 5; i++) {
            SimpleProductResponse product = productResponseList.get(i);
            HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 3), requestHeaders);
            ResponseEntity<List<BuyProductResponse>> addResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
            });
            assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        }

        List<BuyProductRequest> productRequestList = new ArrayList<>();
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<List<BuyProductResponse>> getBasketResponse = restTemplate.exchange("/api/baskets", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
        });
        for (BuyProductResponse resp : getBasketResponse.getBody()) {
            System.out.println(resp);
            productRequestList.add(new BuyProductRequest(resp.getId(), resp.getName(), resp.getPrice(), null));
        }

        HttpEntity buyEntity = new HttpEntity(productRequestList, requestHeaders);
        ResponseEntity<BuyProductsResponse> buyResponse = restTemplate.exchange("/api/purchases/baskets", HttpMethod.POST, buyEntity, BuyProductsResponse.class);
        List<BuyProductResponse> remaining = buyResponse.getBody().getRemaining();
        List<BuyProductResponse> bought = buyResponse.getBody().getBought();
        assertEquals(productRequestList.size(), bought.size());
        assertEquals(3, bought.get(2).getCount());
        assertTrue(remaining.isEmpty());
    }

    @Test
    public void buy_deleted_product_from_basket_without_error() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity addMoneyRequest = new HttpEntity(new AddMoneyRequest(5000), requestHeaders);
        ResponseEntity<ClientRegistrationResponse> addMoneyResponse = restTemplate.exchange("/api/deposits", HttpMethod.PUT, addMoneyRequest, ClientRegistrationResponse.class);
        assertEquals(5000, addMoneyResponse.getBody().getDeposit());

        List<SimpleProductResponse> productResponseList = insertProduct();
        for (int i = 0; i < 5; i++) {
            SimpleProductResponse product = productResponseList.get(i);
            HttpEntity requestEntity = new HttpEntity(new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 3), requestHeaders);
            ResponseEntity<List<BuyProductResponse>> addResponse = restTemplate.exchange("/api/baskets", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
            });
            assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        }

        List<BuyProductRequest> productRequestList = new ArrayList<>();
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<List<BuyProductResponse>> getBasketResponse = restTemplate.exchange("/api/baskets", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<BuyProductResponse>>() {
        });
        for (BuyProductResponse resp : getBasketResponse.getBody()) {
            System.out.println(resp);
            productRequestList.add(new BuyProductRequest(resp.getId(), resp.getName(), resp.getPrice(), null));
        }

        HttpHeaders adminRequestHeaders = new HttpHeaders();
        adminRequestHeaders.add("Cookie", COOKIE_NAME + "=" + adminCookie);
        HttpEntity deleteRequestEntity = new HttpEntity(null, adminRequestHeaders);
        ResponseEntity<EmptyResponse> deleteProductResponse = restTemplate.exchange("/api/products/{product_number}", HttpMethod.DELETE, deleteRequestEntity, EmptyResponse.class, productRequestList.get(0).getId());
        assertEquals(HttpStatus.OK, deleteProductResponse.getStatusCode());


        HttpEntity buyEntity = new HttpEntity(productRequestList, requestHeaders);
        ResponseEntity<BuyProductsResponse> buyResponse = restTemplate.exchange("/api/purchases/baskets", HttpMethod.POST, buyEntity, BuyProductsResponse.class);
        List<BuyProductResponse> remaining = buyResponse.getBody().getRemaining();
        List<BuyProductResponse> bought = buyResponse.getBody().getBought();
        assertNotEquals(productRequestList.size(), bought.size());
        assertEquals(3, bought.get(2).getCount());
        assertFalse(remaining.isEmpty());
        assertEquals(productRequestList.get(0).getId(), remaining.get(0).getId());
    }

    @Test
    public void get_report_without_param() {
        buy_product_from_basket_without_error();
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + adminCookie);
        HttpEntity addRequestEntity = new HttpEntity(null, addRequestHeaders);
        String url = "http://localhost:" + port + "/api/purchases";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(url);
        ResponseEntity<ReportResponse> reportResponse = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, addRequestEntity, ReportResponse.class);
        assertEquals(HttpStatus.OK, reportResponse.getStatusCode());
        reportResponse.getBody().getReportRows().forEach(System.out::println);
        System.out.println("getTotalPrise = " + reportResponse.getBody().getTotalPrise());
        System.out.println("getTotalCount = " + reportResponse.getBody().getTotalCount());
        assertEquals(1500, reportResponse.getBody().getTotalPrise());
        assertEquals(5, reportResponse.getBody().getTotalCount());
    }


    @Test
    public void get_report_with_param() {
        buy_product_from_basket_without_error();
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + adminCookie);
        HttpEntity addRequestEntity = new HttpEntity(null, addRequestHeaders);
        ResponseEntity<List<CategoryResponse>> getCategoryResponse = restTemplate.exchange("/api/categories", HttpMethod.GET, addRequestEntity, new ParameterizedTypeReference<List<CategoryResponse>>() {
        });
        String url = "http://localhost:" + port + "/api/purchases";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("type", "category")
                .queryParam("id", getCategoryResponse.getBody().get(0).getId(), getCategoryResponse.getBody().get(1).getId())
                .queryParam("order", "product");
        ResponseEntity<ReportResponse> reportResponse = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, addRequestEntity, ReportResponse.class);
        assertEquals(HttpStatus.OK, reportResponse.getStatusCode());
        reportResponse.getBody().getReportRows().forEach(System.out::println);
        System.out.println("getTotalPrise = " + reportResponse.getBody().getTotalPrise());
        System.out.println("getTotalCount = " + reportResponse.getBody().getTotalCount());
        assertEquals(0, reportResponse.getBody().getTotalPrise());
        assertEquals(2, reportResponse.getBody().getTotalCount());
        assertNotNull(reportResponse.getBody().getReportRows().get(0).getCategory().get(getCategoryResponse.getBody().get(0).getId()));
        assertNotNull(reportResponse.getBody().getReportRows().get(1).getCategory().get(getCategoryResponse.getBody().get(1).getId()));
    }

}
