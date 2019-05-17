package net.thumbtack.onlineshop.rest;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.EmptyResponse;
import net.thumbtack.onlineshop.dto.OnlineShopExceptionResponse;
import net.thumbtack.onlineshop.dto.category.AddCategoryRequest;
import net.thumbtack.onlineshop.dto.category.CategoryResponse;
import net.thumbtack.onlineshop.dto.edit.EditProductRequest;
import net.thumbtack.onlineshop.dto.product.AddProductRequest;
import net.thumbtack.onlineshop.dto.product.ProductWithCategoryNameResponse;
import net.thumbtack.onlineshop.dto.product.SimpleProductResponse;
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
public class ProductTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    private String cookie;

    @Before
    public void before() {
        commonClearDatabaseNode.clearDatabase();
        AdminRegistrationRequest request = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "Adminlogin", "password");
        ResponseEntity<AdminRegistrationResponse> response = restTemplate.postForEntity("/api/admins", request, AdminRegistrationResponse.class);
        cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        cookie = cookie.substring(cookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(cookie);
        assertTrue(response.getBody().getId() != 0);
        assertEquals(request.getFirstName(), response.getBody().getFirstName());
        assertEquals(request.getPosition(), response.getBody().getPosition());
    }

    private int[] insertCategory(int count, String nameCategory) {
        int[] catId = new int[count];
        for (int i = 0; i < catId.length; i++) {
            HttpHeaders addRequestHeaders = new HttpHeaders();
            addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
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
            requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
            HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product_" + i, 100, 0, category), requestHeaders);
            ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
            assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
            assertTrue(rootSimpleProductResponse.getBody().getId() != 0);
            productResponses.add(rootSimpleProductResponse.getBody());
        }
        return productResponses;
    }

    @Test
    public void add_product_without_category_and_count() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100), requestHeaders);
        ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
        assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
        assertTrue(rootSimpleProductResponse.getBody().getId() != 0);
        assertEquals(100, rootSimpleProductResponse.getBody().getPrice());
        assertEquals(0, rootSimpleProductResponse.getBody().getCount());
    }

    @Test
    public void add_product_without_category() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100, 10, null), requestHeaders);
        ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
        assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
        assertTrue(rootSimpleProductResponse.getBody().getId() != 0);
        assertEquals(0, rootSimpleProductResponse.getBody().getCategories().length);
        assertEquals(100, rootSimpleProductResponse.getBody().getPrice());
        assertEquals(10, rootSimpleProductResponse.getBody().getCount());
    }

    @Test
    public void add_product_without_count() {
        int[] category = insertCategory(10, "Category");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100, 0, category), requestHeaders);
        ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
        assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
        assertTrue(rootSimpleProductResponse.getBody().getId() != 0);
        assertEquals(10, rootSimpleProductResponse.getBody().getCategories().length);
        assertEquals(100, rootSimpleProductResponse.getBody().getPrice());
        assertEquals(0, rootSimpleProductResponse.getBody().getCount());
    }

    @Test
    public void add_product_with_category_and_count() {
        int[] category = insertCategory(10, "Category");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100, 10, category), requestHeaders);
        ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
        assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
        assertTrue(rootSimpleProductResponse.getBody().getId() != 0);
        assertEquals(10, rootSimpleProductResponse.getBody().getCategories().length);
        assertEquals(100, rootSimpleProductResponse.getBody().getPrice());
        assertEquals(10, rootSimpleProductResponse.getBody().getCount());
    }

    @Test
    public void add_product_with_wrong_category() {
        int[] category = {5, 145, 1654, 684, 987};
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100, 10, category), requestHeaders);
        ResponseEntity<OnlineShopExceptionResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, OnlineShopExceptionResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, rootSimpleProductResponse.getStatusCode());
        assertEquals(OnlineShopErrorCode.DATABASE_FIND_CATEGORY_BY_ID_ERROR, rootSimpleProductResponse.getBody().getErrorCode());
    }


    @Test
    public void update_product_with_category() {
        int[] category = insertCategory(5, "Category");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100, 10, category), requestHeaders);
        ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
        assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
        assertTrue(rootSimpleProductResponse.getBody().getId() != 0);
        assertEquals(5, rootSimpleProductResponse.getBody().getCategories().length);

        category = insertCategory(10, "newCategory");
        HttpEntity updateRequestEntity = new HttpEntity(new EditProductRequest("Product 1", 100, 10, category), requestHeaders);
        ResponseEntity<SimpleProductResponse> updateProductResponse = restTemplate.exchange("/api/products/{product_number}", HttpMethod.PUT, updateRequestEntity, SimpleProductResponse.class, rootSimpleProductResponse.getBody().getId());
        assertEquals(HttpStatus.OK, updateProductResponse.getStatusCode());
        assertEquals(updateProductResponse.getBody().getId(), rootSimpleProductResponse.getBody().getId());
        assertEquals(10, updateProductResponse.getBody().getCategories().length);
    }

    @Test
    public void update_product_to_without_category() {
        int[] category = insertCategory(5, "Category");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100, 10, category), requestHeaders);
        ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
        assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
        assertTrue(rootSimpleProductResponse.getBody().getId() != 0);
        assertEquals(5, rootSimpleProductResponse.getBody().getCategories().length);

        HttpEntity updateRequestEntity = new HttpEntity(new EditProductRequest("Product 1", 100, 10, new int[0]), requestHeaders);
        ResponseEntity<SimpleProductResponse> updateProductResponse = restTemplate.exchange("/api/products/{product_number}", HttpMethod.PUT, updateRequestEntity, SimpleProductResponse.class, rootSimpleProductResponse.getBody().getId());
        assertEquals(HttpStatus.OK, updateProductResponse.getStatusCode());
        assertEquals(updateProductResponse.getBody().getId(), rootSimpleProductResponse.getBody().getId());
        assertEquals(0, updateProductResponse.getBody().getCategories().length);
    }

    @Test
    public void update_product_to_null_category() {
        int[] category = insertCategory(5, "Category");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100, 10, category), requestHeaders);
        ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
        assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
        assertTrue(rootSimpleProductResponse.getBody().getId() != 0);
        assertEquals(5, rootSimpleProductResponse.getBody().getCategories().length);

        HttpEntity updateRequestEntity = new HttpEntity(new EditProductRequest("Product 1", 100, 100, null), requestHeaders);
        ResponseEntity<SimpleProductResponse> updateProductResponse = restTemplate.exchange("/api/products/{product_number}", HttpMethod.PUT, updateRequestEntity, SimpleProductResponse.class, rootSimpleProductResponse.getBody().getId());
        assertEquals(HttpStatus.OK, updateProductResponse.getStatusCode());
        assertEquals(updateProductResponse.getBody().getId(), rootSimpleProductResponse.getBody().getId());
        assertEquals(5, updateProductResponse.getBody().getCategories().length);
        assertEquals(100, updateProductResponse.getBody().getCount());
    }

    @Test
    public void deleteProductWithoutCategory() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100, 10, null), requestHeaders);
        ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
        assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
        assertTrue(rootSimpleProductResponse.getBody().getId() != 0);

        HttpEntity deleteRequestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<EmptyResponse> deleteProductResponse = restTemplate.exchange("/api/products/{product_number}", HttpMethod.DELETE, deleteRequestEntity, EmptyResponse.class, rootSimpleProductResponse.getBody().getId());
        assertEquals(HttpStatus.OK, deleteProductResponse.getStatusCode());
    }

    @Test
    public void get_product_with_category() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100, 10, null), requestHeaders);
        ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
        assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
        assertTrue(rootSimpleProductResponse.getBody().getId() != 0);

        HttpEntity httpEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<ProductWithCategoryNameResponse> getProductResponse = restTemplate.exchange("/api/products/{product_number}", HttpMethod.GET, httpEntity, ProductWithCategoryNameResponse.class, rootSimpleProductResponse.getBody().getId());
        assertEquals(HttpStatus.OK, getProductResponse.getStatusCode());
        assertEquals(rootSimpleProductResponse.getBody().getId(), getProductResponse.getBody().getId());
        assertEquals(rootSimpleProductResponse.getBody().getName(), getProductResponse.getBody().getName());
        assertEquals(rootSimpleProductResponse.getBody().getCategories().length, getProductResponse.getBody().getCategories().length);
    }

    @Test
    public void get_product_with_client_cookie_with_category() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddProductRequest("Product 1", 100, 10, null), requestHeaders);
        ResponseEntity<SimpleProductResponse> rootSimpleProductResponse = restTemplate.exchange("/api/products", HttpMethod.POST, requestEntity, SimpleProductResponse.class);
        assertEquals(HttpStatus.OK, rootSimpleProductResponse.getStatusCode());
        assertTrue(rootSimpleProductResponse.getBody().getId() != 0);

        ClientRegistrationRequest request = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        ResponseEntity<ClientRegistrationResponse> response = restTemplate.postForEntity("/api/clients", request, ClientRegistrationResponse.class);
        String clientCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        clientCookie = clientCookie.substring(clientCookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        HttpHeaders clientRequestHeaders = new HttpHeaders();
        clientRequestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity httpEntity = new HttpEntity(null, clientRequestHeaders);
        ResponseEntity<ProductWithCategoryNameResponse> getProductResponse = restTemplate.exchange("/api/products/{product_number}", HttpMethod.GET, httpEntity, ProductWithCategoryNameResponse.class, rootSimpleProductResponse.getBody().getId());
        assertEquals(HttpStatus.OK, getProductResponse.getStatusCode());
        assertEquals(rootSimpleProductResponse.getBody().getId(), getProductResponse.getBody().getId());
        assertEquals(rootSimpleProductResponse.getBody().getName(), getProductResponse.getBody().getName());
        assertEquals(rootSimpleProductResponse.getBody().getCategories().length, getProductResponse.getBody().getCategories().length);
    }

    @Test
    public void get_product_without_param() {
        List<SimpleProductResponse> productResponses = insertProduct();
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity addRequestEntity = new HttpEntity(null, addRequestHeaders);
        String url = "http://localhost:" + port + "/api/products";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
        ResponseEntity<List<ProductWithCategoryNameResponse>> listResponse = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, addRequestEntity, new ParameterizedTypeReference<List<ProductWithCategoryNameResponse>>() {
        });
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<ProductWithCategoryNameResponse> body = listResponse.getBody();
        assertEquals(10, body.size());
        for (ProductWithCategoryNameResponse response : body) {
            assertEquals(10, response.getCategories().length);
        }
    }

    @Test
    public void get_product_with_client_cookie_without_param() {
        ClientRegistrationRequest request = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", "client@gmail.com", "ClientAddress", "79131533464", "ClientLogin", "password123");
        ResponseEntity<ClientRegistrationResponse> response = restTemplate.postForEntity("/api/clients", request, ClientRegistrationResponse.class);
        String clientCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        clientCookie = clientCookie.substring(clientCookie.indexOf('=') + 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<SimpleProductResponse> productResponses = insertProduct();
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + clientCookie);
        HttpEntity addRequestEntity = new HttpEntity(null, addRequestHeaders);
        String url = "http://localhost:" + port + "/api/products";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
        ResponseEntity<List<ProductWithCategoryNameResponse>> listResponse = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, addRequestEntity, new ParameterizedTypeReference<List<ProductWithCategoryNameResponse>>() {
        });
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<ProductWithCategoryNameResponse> body = listResponse.getBody();
        assertEquals(10, body.size());
        for (ProductWithCategoryNameResponse categoryNameResponse : body) {
            assertEquals(10, categoryNameResponse.getCategories().length);
        }
    }

    @Test
    public void get_product_without_category() {
        List<SimpleProductResponse> productResponses = insertProduct();
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity addRequestEntity = new HttpEntity(null, addRequestHeaders);
        String url = "http://localhost:" + port + "/api/products";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("category");
        ResponseEntity<List<ProductWithCategoryNameResponse>> listResponse = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, addRequestEntity, new ParameterizedTypeReference<List<ProductWithCategoryNameResponse>>() {
        });
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<ProductWithCategoryNameResponse> body = listResponse.getBody();
        assertEquals(0, body.size());
    }

    @Test
    public void get_product_by_category() {
        List<SimpleProductResponse> productResponses = insertProduct();
        int[] cat0 = productResponses.get(0).getCategories();
        int[] cat1 = productResponses.get(1).getCategories();
        int[] cat2 = productResponses.get(2).getCategories();
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity addRequestEntity = new HttpEntity(null, addRequestHeaders);
        String url = "http://localhost:" + port + "/api/products";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("category", cat0[5], cat1[2], cat2[3]);
        ResponseEntity<List<ProductWithCategoryNameResponse>> listResponse = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, addRequestEntity, new ParameterizedTypeReference<List<ProductWithCategoryNameResponse>>() {
        });
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<ProductWithCategoryNameResponse> body = listResponse.getBody();
        assertEquals(productResponses.get(0).getId(), body.get(0).getId());
        assertEquals(productResponses.get(1).getId(), body.get(1).getId());
        assertEquals(productResponses.get(2).getId(), body.get(2).getId());
    }

    @Test
    public void get_product_sorted_by_category() {
        List<SimpleProductResponse> productResponses = insertProduct();
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity addRequestEntity = new HttpEntity(null, addRequestHeaders);
        String url = "http://localhost:" + port + "/api/products";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("order", "category");
        ResponseEntity<List<ProductWithCategoryNameResponse>> listResponse = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, addRequestEntity, new ParameterizedTypeReference<List<ProductWithCategoryNameResponse>>() {
        });
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<ProductWithCategoryNameResponse> body = listResponse.getBody();
        assertEquals(100, body.size());
    }

    @Test
    public void get_product_with_category_sorted_by_category() {
        List<SimpleProductResponse> productResponses = insertProduct();
        int[] cat0 = productResponses.get(0).getCategories();
        int[] cat1 = productResponses.get(1).getCategories();
        int[] cat2 = productResponses.get(2).getCategories();
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity addRequestEntity = new HttpEntity(null, addRequestHeaders);
        String url = "http://localhost:" + port + "/api/products";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("category", cat0[5], cat1[2], cat2[3])
                .queryParam("order", "category");
        ResponseEntity<List<ProductWithCategoryNameResponse>> listResponse = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, addRequestEntity, new ParameterizedTypeReference<List<ProductWithCategoryNameResponse>>() {
        });
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<ProductWithCategoryNameResponse> body = listResponse.getBody();
        assertEquals(productResponses.get(0).getId(), body.get(0).getId());
        assertEquals(productResponses.get(1).getId(), body.get(1).getId());
        assertEquals(productResponses.get(2).getId(), body.get(2).getId());
    }

    @Test
    public void get_product_with_wrong_param() {
        List<SimpleProductResponse> productResponses = insertProduct();
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity addRequestEntity = new HttpEntity(null, addRequestHeaders);
        String url = "http://localhost:" + port + "/api/products";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("order", "catgory");
        ResponseEntity<OnlineShopExceptionResponse> badResponse = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, addRequestEntity, OnlineShopExceptionResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, badResponse.getStatusCode());
        assertEquals(OnlineShopErrorCode.WRONG_PARAM, badResponse.getBody().getErrorCode());
    }

}
