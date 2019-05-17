package net.thumbtack.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.category.CategoryResponse;
import net.thumbtack.onlineshop.dto.edit.EditCategoryRequest;
import net.thumbtack.onlineshop.dto.edit.EditProductRequest;
import net.thumbtack.onlineshop.dto.product.AddProductRequest;
import net.thumbtack.onlineshop.dto.product.ProductWithCategoryNameResponse;
import net.thumbtack.onlineshop.dto.product.SimpleProductResponse;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
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
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    private Cookie cookie;

    @Before
    public void before() throws Exception {
        commonClearDatabaseNode.clearDatabase();
        AdminRegistrationRequest request = new AdminRegistrationRequest("Администратор", "Фамилия", "Отчество", "admin", "Adminlogin", "password");
        MvcResult registrationResult = mockMvc.perform(post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_NAME))
                .andReturn();
        cookie = registrationResult.getResponse().getCookie(COOKIE_NAME);
        assertNotNull(cookie);
    }

    @Test
    public void addProductWithoutCategory() throws Exception {
        AddProductRequest request = new AddProductRequest("Product", 100, 10, null);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(0, simpleProductResponse.getCategories().length);

    }

    @Test
    public void addProductWithCategory() throws Exception {
        EditCategoryRequest rootCategory1 = new EditCategoryRequest("Root_Category-1", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory1)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse1 = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse1.getParentName());
        assertEquals(0, rootCategoryResponse1.getParentId());
        EditCategoryRequest rootCategory2 = new EditCategoryRequest("Root_Category-2", 0);
        MvcResult addCategoryResult2 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory2)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse2 = objectMapper.readValue(addCategoryResult2.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse2.getParentName());
        assertEquals(0, rootCategoryResponse2.getParentId());
        int[] categories = new int[2];
        categories[0] = rootCategoryResponse1.getId();
        categories[1] = rootCategoryResponse2.getId();
        AddProductRequest request = new AddProductRequest("Product", 100, 10, categories);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(2, simpleProductResponse.getCategories().length);
    }

    @Test
    public void addProductWithoutCount() throws Exception {
        AddProductRequest request = new AddProductRequest("Product", 100);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
//        System.out.println(simpleProductResponse);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(0, simpleProductResponse.getCount());
        assertEquals(0, simpleProductResponse.getCategories().length);
    }

    @Test
    public void addProductWith_0_Prise() throws Exception {
        AddProductRequest request = new AddProductRequest("Product", 0);
        mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putProduct_All() throws Exception {
        EditCategoryRequest rootCategory1 = new EditCategoryRequest("Root_Category-1", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory1)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse1 = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse1.getParentName());
        assertEquals(0, rootCategoryResponse1.getParentId());
        EditCategoryRequest rootCategory2 = new EditCategoryRequest("Root_Category-2", 0);
        MvcResult addCategoryResult2 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory2)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse2 = objectMapper.readValue(addCategoryResult2.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse2.getParentName());
        assertEquals(0, rootCategoryResponse2.getParentId());
        int[] categories = new int[2];
        categories[0] = rootCategoryResponse1.getId();
        categories[1] = rootCategoryResponse2.getId();
        AddProductRequest request = new AddProductRequest("Product", 100, 10, categories);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(10, simpleProductResponse.getCount());
        assertNotNull(simpleProductResponse.getCategories());
        assertEquals(2, simpleProductResponse.getCategories().length);
        int[] categories2 = new int[1];
        categories2[0] = rootCategoryResponse1.getId();
        EditProductRequest putRequest = new EditProductRequest("NewNameProduct", 99, 9, categories2);
        MvcResult putProductResult = mockMvc.perform(put("/api/products/{product_number}", String.valueOf(simpleProductResponse.getId()))
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putRequest)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse putProductResponse = objectMapper.readValue(putProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertEquals(simpleProductResponse.getId(), putProductResponse.getId());
        assertNotEquals(simpleProductResponse.getPrice(), putProductResponse.getPrice());
        assertNotEquals(simpleProductResponse.getCount(), putProductResponse.getCount());
        assertNotEquals(simpleProductResponse.getName(), putProductResponse.getName());
        assertNotEquals(simpleProductResponse.getCategories().length, putProductResponse.getCategories().length);
    }

    @Test
    public void putProduct_WithoutCategory() throws Exception {
        EditCategoryRequest rootCategory1 = new EditCategoryRequest("Root_Category-1", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory1)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse1 = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse1.getParentName());
        assertEquals(0, rootCategoryResponse1.getParentId());
        EditCategoryRequest rootCategory2 = new EditCategoryRequest("Root_Category-2", 0);
        MvcResult addCategoryResult2 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory2)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse2 = objectMapper.readValue(addCategoryResult2.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse2.getParentName());
        assertEquals(0, rootCategoryResponse2.getParentId());
        int[] categories = new int[2];
        categories[0] = rootCategoryResponse1.getId();
        categories[1] = rootCategoryResponse2.getId();
        AddProductRequest request = new AddProductRequest("Product", 100, 10, categories);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(10, simpleProductResponse.getCount());
        assertNotNull(simpleProductResponse.getCategories());
        assertEquals(2, simpleProductResponse.getCategories().length);
        EditProductRequest putRequest = new EditProductRequest(null, null, null, new int[0]);
        MvcResult putProductResult = mockMvc.perform(put("/api/products/{product_number}", String.valueOf(simpleProductResponse.getId()))
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putRequest)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse putProductResponse = objectMapper.readValue(putProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertEquals(simpleProductResponse.getId(), putProductResponse.getId());
        assertNotEquals(simpleProductResponse.getCategories().length, putProductResponse.getCategories().length);
        assertEquals(0, putProductResponse.getCategories().length);
    }

    @Test
    public void putProduct_WithName() throws Exception {
        EditCategoryRequest rootCategory1 = new EditCategoryRequest("Root_Category-1", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory1)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse1 = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse1.getParentName());
        assertEquals(0, rootCategoryResponse1.getParentId());
        EditCategoryRequest rootCategory2 = new EditCategoryRequest("Root_Category-2", 0);
        MvcResult addCategoryResult2 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory2)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse2 = objectMapper.readValue(addCategoryResult2.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse2.getParentName());
        assertEquals(0, rootCategoryResponse2.getParentId());
        int[] categories = new int[2];
        categories[0] = rootCategoryResponse1.getId();
        categories[1] = rootCategoryResponse2.getId();
        AddProductRequest request = new AddProductRequest("Product", 100, 10, categories);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(10, simpleProductResponse.getCount());
        assertNotNull(simpleProductResponse.getCategories());
        assertEquals(2, simpleProductResponse.getCategories().length);
        EditProductRequest putRequest = new EditProductRequest("NewNameProduct", null, null, null);
        MvcResult putProductResult = mockMvc.perform(put("/api/products/{product_number}", String.valueOf(simpleProductResponse.getId()))
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putRequest)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse putProductResponse = objectMapper.readValue(putProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertEquals(simpleProductResponse.getId(), putProductResponse.getId());
        assertEquals(simpleProductResponse.getPrice(), putProductResponse.getPrice());
        assertEquals(simpleProductResponse.getCount(), putProductResponse.getCount());
        assertEquals(simpleProductResponse.getCategories().length, putProductResponse.getCategories().length);
        assertNotEquals(simpleProductResponse.getName(), putProductResponse.getName());
    }

    @Test
    public void putProduct_WithPrice() throws Exception {
        AddProductRequest request = new AddProductRequest("Product", 100, 10, null);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(10, simpleProductResponse.getCount());
        assertEquals(0, simpleProductResponse.getCategories().length);
        EditProductRequest putRequest = new EditProductRequest(null, 99, null, null);
        MvcResult putProductResult = mockMvc.perform(put("/api/products/{product_number}", String.valueOf(simpleProductResponse.getId()))
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putRequest)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse putProductResponse = objectMapper.readValue(putProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertEquals(simpleProductResponse.getId(), putProductResponse.getId());
        assertNotEquals(simpleProductResponse.getPrice(), putProductResponse.getPrice());
        assertEquals(simpleProductResponse.getCount(), putProductResponse.getCount());
        assertEquals(simpleProductResponse.getName(), putProductResponse.getName());
    }

    @Test
    public void putProduct_WithCount() throws Exception {
        AddProductRequest request = new AddProductRequest("Product", 100, 10, null);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(10, simpleProductResponse.getCount());
        assertEquals(0, simpleProductResponse.getCategories().length);
        EditProductRequest putRequest = new EditProductRequest(null, null, 1234, null);
        MvcResult putProductResult = mockMvc.perform(put("/api/products/{product_number}", String.valueOf(simpleProductResponse.getId()))
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putRequest)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse putProductResponse = objectMapper.readValue(putProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertEquals(simpleProductResponse.getId(), putProductResponse.getId());
        assertEquals(simpleProductResponse.getPrice(), putProductResponse.getPrice());
        assertNotEquals(simpleProductResponse.getCount(), putProductResponse.getCount());
        assertEquals(simpleProductResponse.getName(), putProductResponse.getName());
    }

    @Test
    public void deleteProductWithoutCategory() throws Exception {
        AddProductRequest request = new AddProductRequest("Product", 100, 10, null);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(0, simpleProductResponse.getCategories().length);
        MvcResult deleteProductResult = mockMvc.perform(delete("/api/products/{product_number}", String.valueOf(simpleProductResponse.getId()))
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("{}", deleteProductResult.getResponse().getContentAsString());
    }

    @Test
    public void deleteProductWithCategory() throws Exception {
        EditCategoryRequest rootCategory1 = new EditCategoryRequest("Root_Category-1", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory1)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse1 = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse1.getParentName());
        assertEquals(0, rootCategoryResponse1.getParentId());
        EditCategoryRequest rootCategory2 = new EditCategoryRequest("Root_Category-2", 0);
        MvcResult addCategoryResult2 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory2)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse2 = objectMapper.readValue(addCategoryResult2.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse2.getParentName());
        assertEquals(0, rootCategoryResponse2.getParentId());
        int[] categories = new int[2];
        categories[0] = rootCategoryResponse1.getId();
        categories[1] = rootCategoryResponse2.getId();
        AddProductRequest request = new AddProductRequest("Product", 100, 10, categories);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(10, simpleProductResponse.getCount());
        assertNotNull(simpleProductResponse.getCategories());
        assertEquals(2, simpleProductResponse.getCategories().length);
        MvcResult deleteProductResult = mockMvc.perform(delete("/api/products/{product_number}", simpleProductResponse.getId())
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("{}", deleteProductResult.getResponse().getContentAsString());
    }

    @Test
    public void getProductWithoutCategory() throws Exception {
        AddProductRequest request = new AddProductRequest("Product", 100, 10, null);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertEquals(0, simpleProductResponse.getCategories().length);
        MvcResult getProductResult = mockMvc.perform(get("/api/products/{product_number}", simpleProductResponse.getId())
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        ProductWithCategoryNameResponse productWithCategoryNameResponse = objectMapper.readValue(getProductResult.getResponse().getContentAsString(), ProductWithCategoryNameResponse.class);
        assertEquals(simpleProductResponse.getId(), productWithCategoryNameResponse.getId());
        assertEquals(0, productWithCategoryNameResponse.getCategories().length);
    }

    @Test
    public void getProductWithCategory() throws Exception {
        EditCategoryRequest rootCategory1 = new EditCategoryRequest("Root_Category-1", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory1)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse1 = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse1.getParentName());
        assertEquals(0, rootCategoryResponse1.getParentId());
        EditCategoryRequest rootCategory2 = new EditCategoryRequest("Root_Category-2", 0);
        MvcResult addCategoryResult2 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory2)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse2 = objectMapper.readValue(addCategoryResult2.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse2.getParentName());
        assertEquals(0, rootCategoryResponse2.getParentId());
        int[] categories = new int[2];
        categories[0] = rootCategoryResponse1.getId();
        categories[1] = rootCategoryResponse2.getId();
        AddProductRequest request = new AddProductRequest("Product", 100, 10, categories);
        MvcResult addProductResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        SimpleProductResponse simpleProductResponse = objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class);
        assertTrue(simpleProductResponse.getId() != 0);
        assertNotNull(simpleProductResponse.getCategories());
        MvcResult getProductResult = mockMvc.perform(get("/api/products/{product_number}", simpleProductResponse.getId())
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        ProductWithCategoryNameResponse productWithCategoryNameResponse = objectMapper.readValue(getProductResult.getResponse().getContentAsString(), ProductWithCategoryNameResponse.class);
        assertEquals(simpleProductResponse.getId(), productWithCategoryNameResponse.getId());
        assertEquals(2, productWithCategoryNameResponse.getCategories().length);
    }

    private List<CategoryResponse> insertCategory() throws Exception {
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for (int i = 6; i > 0; i--) {
            MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new EditCategoryRequest(i + "_Category", 0))))
                    .andExpect(status().isOk())
                    .andReturn();
            CategoryResponse rootCategoryResponse = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
            categoryResponses.add(rootCategoryResponse);
            assertTrue(rootCategoryResponse.getId() != 0);
        }
        return categoryResponses;
    }

    private List<SimpleProductResponse> insertProduct(List<CategoryResponse> categoryResponses) throws Exception {
        List<SimpleProductResponse> productResponses = new ArrayList<>();
        AddProductRequest request = new AddProductRequest("ProductWithoutCategory", 100, 10, null);
        MvcResult mvcResult = mockMvc.perform(post("/api/products")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        productResponses.add(objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SimpleProductResponse.class));
        for (int i = 0; i < 5; i++) {
            int[] category = new int[2];
            category[0] = categoryResponses.get(i).getId();
            category[1] = categoryResponses.get(i + 1).getId();
            AddProductRequest requestProd = new AddProductRequest(i + "_ProductWithCategory", 100, 100, category);
            MvcResult addProductResult = mockMvc.perform(post("/api/products")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestProd)))
                    .andExpect(status().isOk())
                    .andReturn();
            productResponses.add(objectMapper.readValue(addProductResult.getResponse().getContentAsString(), SimpleProductResponse.class));
        }
        return productResponses;
    }


    @Test
    public void getProductWithParam_orderProduct() throws Exception {
        List<CategoryResponse> categoryResponses = insertCategory();
        List<SimpleProductResponse> productResponses = insertProduct(categoryResponses);
        MvcResult getResult = mockMvc.perform(get("/api/products")
                .cookie(cookie)
                .param("order", "product"))
                .andExpect(status().isOk())
                .andReturn();
        List<ProductWithCategoryNameResponse> responseList = objectMapper.readValue(getResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, ProductWithCategoryNameResponse.class));
//        responseList.forEach(System.out::println);
        assertEquals(6, responseList.size());
    }

    @Test
    public void getProductWithParam_orderProduct_withCategory() throws Exception {
        List<CategoryResponse> categoryResponses = insertCategory();
        List<SimpleProductResponse> productResponses = insertProduct(categoryResponses);
        MvcResult getResult = mockMvc.perform(get("/api/products")
                .cookie(cookie)
                .param("category", String.valueOf(categoryResponses.get(0).getId()), String.valueOf(categoryResponses.get(2).getId()))
                .param("order", "product"))
                .andExpect(status().isOk())
                .andReturn();
        List<ProductWithCategoryNameResponse> responseList = objectMapper.readValue(getResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, ProductWithCategoryNameResponse.class));
//        responseList.forEach(System.out::println);
        assertEquals(3, responseList.size());
    }

    @Test
    public void getProductWithParam_orderCategory() throws Exception {
        List<CategoryResponse> categoryResponses = insertCategory();
        List<SimpleProductResponse> productResponses = insertProduct(categoryResponses);
        MvcResult getResult = mockMvc.perform(get("/api/products")
                .cookie(cookie)
                .param("order", "category"))
                .andExpect(status().isOk())
                .andReturn();
        List<ProductWithCategoryNameResponse> responseList = objectMapper.readValue(getResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, ProductWithCategoryNameResponse.class));
//        responseList.forEach(System.out::println);
        assertEquals(11, responseList.size());
    }

    @Test
    public void getProductWithParam_orderCategory_withCategory() throws Exception {
        List<CategoryResponse> categoryResponses = insertCategory();
        List<SimpleProductResponse> productResponses = insertProduct(categoryResponses);
        MvcResult getResult = mockMvc.perform(get("/api/products")
                .cookie(cookie)
                .param("category", String.valueOf(categoryResponses.get(0).getId()), String.valueOf(categoryResponses.get(2).getId()))
                .param("order", "category"))
                .andExpect(status().isOk())
                .andReturn();
        List<ProductWithCategoryNameResponse> responseList = objectMapper.readValue(getResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, ProductWithCategoryNameResponse.class));
//        responseList.forEach(System.out::println);
        assertEquals(3, responseList.size());
    }

    @Test
    public void getProductWithParam_orderDefault() throws Exception {
        List<CategoryResponse> categoryResponses = insertCategory();
        List<SimpleProductResponse> productResponses = insertProduct(categoryResponses);
        MvcResult getResult = mockMvc.perform(get("/api/products")
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        List<ProductWithCategoryNameResponse> responseList = objectMapper.readValue(getResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, ProductWithCategoryNameResponse.class));
//        responseList.forEach(System.out::println);
        assertEquals(6, responseList.size());
    }

    @Test
    public void getProductWithParam_orderDefault_withCategory() throws Exception {
        List<CategoryResponse> categoryResponses = insertCategory();
        List<SimpleProductResponse> productResponses = insertProduct(categoryResponses);
        MvcResult getResult = mockMvc.perform(get("/api/products")
                .cookie(cookie)
                .param("category", String.valueOf(categoryResponses.get(0).getId()), String.valueOf(categoryResponses.get(2).getId())))
                .andExpect(status().isOk())
                .andReturn();
        List<ProductWithCategoryNameResponse> responseList = objectMapper.readValue(getResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, ProductWithCategoryNameResponse.class));
//        responseList.forEach(System.out::println);
        assertEquals(3, responseList.size());
    }

}