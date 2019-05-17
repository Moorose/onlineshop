package net.thumbtack.onlineshop.rest;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.EmptyResponse;
import net.thumbtack.onlineshop.dto.category.AddCategoryRequest;
import net.thumbtack.onlineshop.dto.category.CategoryResponse;
import net.thumbtack.onlineshop.dto.edit.EditCategoryRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
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

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryTest {

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

    @Test
    public void add_root_and_child_category() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity = new HttpEntity(new AddCategoryRequest("Root_Category", 0), requestHeaders);
        ResponseEntity<CategoryResponse> rootCategoryResponse = restTemplate.exchange("/api/categories", HttpMethod.POST, requestEntity, CategoryResponse.class);
        assertEquals(HttpStatus.OK, rootCategoryResponse.getStatusCode());
        assertNull(rootCategoryResponse.getBody().getParentName());
        assertEquals(0, rootCategoryResponse.getBody().getParentId());

        HttpHeaders requestHeaders2 = new HttpHeaders();
        requestHeaders2.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity requestEntity2 = new HttpEntity(new AddCategoryRequest("Child_Category", rootCategoryResponse.getBody().getId()), requestHeaders2);
        ResponseEntity<CategoryResponse> rootCategoryResponse2 = restTemplate.exchange("/api/categories", HttpMethod.POST, requestEntity2, CategoryResponse.class);
        assertEquals(HttpStatus.OK, rootCategoryResponse2.getStatusCode());
        assertEquals(rootCategoryResponse.getBody().getId(), rootCategoryResponse2.getBody().getParentId());
        assertEquals(rootCategoryResponse.getBody().getName(), rootCategoryResponse2.getBody().getParentName());
    }


    @Test
    public void add_category_and_put_category_and_get_category() {
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity addRequestEntity = new HttpEntity(new AddCategoryRequest("Root_Category", 0), addRequestHeaders);
        ResponseEntity<CategoryResponse> addRootCategoryResponse = restTemplate.exchange("/api/categories", HttpMethod.POST, addRequestEntity, CategoryResponse.class);
        assertEquals(HttpStatus.OK, addRootCategoryResponse.getStatusCode());
        assertNull(addRootCategoryResponse.getBody().getParentName());
        assertEquals(0, addRootCategoryResponse.getBody().getParentId());

        HttpHeaders putRequestHeaders = new HttpHeaders();
        putRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity putRequestEntity = new HttpEntity(new EditCategoryRequest("RootRootCategory", 0), putRequestHeaders);
        ResponseEntity<CategoryResponse> putCategoryResponse = restTemplate.exchange("/api/categories/{category_number}", HttpMethod.PUT, putRequestEntity, CategoryResponse.class, addRootCategoryResponse.getBody().getId());
        assertEquals(HttpStatus.OK, putCategoryResponse.getStatusCode());
        assertEquals("RootRootCategory", putCategoryResponse.getBody().getName());

        HttpHeaders getRequestHeaders = new HttpHeaders();
        getRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity getRequestEntity = new HttpEntity(null, getRequestHeaders);
        ResponseEntity<CategoryResponse> getCategoryResponse = restTemplate.exchange("/api/categories/{category_number}", HttpMethod.GET, getRequestEntity, CategoryResponse.class, addRootCategoryResponse.getBody().getId());
        assertEquals(HttpStatus.OK, getCategoryResponse.getStatusCode());
        assertEquals(putCategoryResponse.getBody().getName(), getCategoryResponse.getBody().getName());
        assertEquals(putCategoryResponse.getBody().getId(), getCategoryResponse.getBody().getId());
    }

    @Test
    public void put_root_category_to_child_with_error() {
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity addRequestEntity = new HttpEntity(new AddCategoryRequest("Root_Category", 0), addRequestHeaders);
        ResponseEntity<CategoryResponse> addRootCategoryResponse = restTemplate.exchange("/api/categories", HttpMethod.POST, addRequestEntity, CategoryResponse.class);
        assertEquals(HttpStatus.OK, addRootCategoryResponse.getStatusCode());
        assertNull(addRootCategoryResponse.getBody().getParentName());
        assertEquals(0, addRootCategoryResponse.getBody().getParentId());

        HttpHeaders addRabbitHeaders = new HttpHeaders();
        addRabbitHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity addRabbitEntity = new HttpEntity(new AddCategoryRequest("Rabbit_Category", 0), addRabbitHeaders);
        ResponseEntity<CategoryResponse> addRabbitResponse = restTemplate.exchange("/api/categories", HttpMethod.POST, addRabbitEntity, CategoryResponse.class);
        assertEquals(HttpStatus.OK, addRabbitResponse.getStatusCode());
        assertNull(addRabbitResponse.getBody().getParentName());
        assertEquals(0, addRabbitResponse.getBody().getParentId());

        HttpHeaders putRequestHeaders = new HttpHeaders();
        putRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity putRequestEntity = new HttpEntity(new EditCategoryRequest(null, addRootCategoryResponse.getBody().getId()), putRequestHeaders);
        ResponseEntity<CategoryResponse> putCategoryResponse = restTemplate.exchange("/api/categories/{category_number}", HttpMethod.PUT, putRequestEntity, CategoryResponse.class, addRabbitResponse.getBody().getId());
        assertEquals(HttpStatus.BAD_REQUEST, putCategoryResponse.getStatusCode());
    }

    @Test
    public void add_category_and_delete_category_and_get_category_with_error() {
        HttpHeaders addRequestHeaders = new HttpHeaders();
        addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity addRequestEntity = new HttpEntity(new AddCategoryRequest("Root_Category", 0), addRequestHeaders);
        ResponseEntity<CategoryResponse> addRootCategoryResponse = restTemplate.exchange("/api/categories", HttpMethod.POST, addRequestEntity, CategoryResponse.class);
        assertEquals(HttpStatus.OK, addRootCategoryResponse.getStatusCode());
        assertNull(addRootCategoryResponse.getBody().getParentName());
        assertEquals(0, addRootCategoryResponse.getBody().getParentId());

        HttpHeaders deleteRequestHeaders = new HttpHeaders();
        deleteRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity deleteRequestEntity = new HttpEntity(null, deleteRequestHeaders);
        ResponseEntity<EmptyResponse> deleteCategoryResponse = restTemplate.exchange("/api/categories/{category_number}", HttpMethod.DELETE, deleteRequestEntity, EmptyResponse.class, addRootCategoryResponse.getBody().getId());
        assertEquals(HttpStatus.OK, deleteCategoryResponse.getStatusCode());

        HttpHeaders getRequestHeaders = new HttpHeaders();
        getRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity getRequestEntity = new HttpEntity(null, getRequestHeaders);
        ResponseEntity<CategoryResponse> getCategoryResponse = restTemplate.exchange("/api/categories/{category_number}", HttpMethod.GET, getRequestEntity, CategoryResponse.class, addRootCategoryResponse.getBody().getId());
        assertEquals(HttpStatus.BAD_REQUEST, getCategoryResponse.getStatusCode());
    }

    @Test
    public void getAllCategories() {
        for (int i = 0; i < 5; i++) {
            HttpHeaders addRequestHeaders = new HttpHeaders();
            addRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
            HttpEntity addRequestEntity = new HttpEntity(new AddCategoryRequest("Root_Category_" + i, 0), addRequestHeaders);
            ResponseEntity<CategoryResponse> addRootCategoryResponse = restTemplate.exchange("/api/categories", HttpMethod.POST, addRequestEntity, CategoryResponse.class);
            assertEquals(HttpStatus.OK, addRootCategoryResponse.getStatusCode());
        }
        HttpHeaders getRequestHeaders = new HttpHeaders();
        getRequestHeaders.add("Cookie", COOKIE_NAME + "=" + cookie);
        HttpEntity getRequestEntity = new HttpEntity(null, getRequestHeaders);
        ResponseEntity<List<CategoryResponse>> getCategoryResponse = restTemplate.exchange("/api/categories", HttpMethod.GET, getRequestEntity, new ParameterizedTypeReference<List<CategoryResponse>>() {
        });
        assertEquals(HttpStatus.OK, getCategoryResponse.getStatusCode());
        assertEquals(5, getCategoryResponse.getBody().size());
    }
}