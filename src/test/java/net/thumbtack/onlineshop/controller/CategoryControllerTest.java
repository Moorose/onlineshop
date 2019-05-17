package net.thumbtack.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.OnlineShopExceptionResponse;
import net.thumbtack.onlineshop.dto.category.AddCategoryRequest;
import net.thumbtack.onlineshop.dto.category.CategoryResponse;
import net.thumbtack.onlineshop.dto.edit.EditCategoryRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
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
public class CategoryControllerTest {

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
    public void addRootCategory() throws Exception {
//        EditCategoryRequest request = new EditCategoryRequest("Root_Category", 0);
        MvcResult addCategoryResult = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
                .content("{\"name\":\"Root_Category\"}"))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse categoryResponse = objectMapper.readValue(addCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(categoryResponse.getParentName());
        assertEquals(0, categoryResponse.getParentId());
        assertEquals("Root_Category", categoryResponse.getName());
    }


    @Test
    public void addRootUniqueCategory() throws Exception {
        EditCategoryRequest request = new EditCategoryRequest("Root_Category", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse categoryResponse = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        System.out.println(addCategoryResult1.getResponse());
        assertNull(categoryResponse.getParentName());
        assertEquals(0, categoryResponse.getParentId());
        assertEquals(request.getName(), categoryResponse.getName());
        MvcResult addCategoryResult2 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError())
                .andReturn();
        OnlineShopExceptionResponse onlineShopExceptionResponse = objectMapper.readValue(addCategoryResult2.getResponse().getContentAsString(), OnlineShopExceptionResponse.class);
        assertEquals(OnlineShopErrorCode.DATABASE_DUPLICATE_KEY, onlineShopExceptionResponse.getErrorCode());
        assertEquals(categoryResponse.getName(), onlineShopExceptionResponse.getField());
    }

    @Test
    public void addRootAndChildCategory() throws Exception {
        AddCategoryRequest rootCategory = new AddCategoryRequest("Root_Category", 0);
        MvcResult addCategoryResult = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse = objectMapper.readValue(addCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse.getParentName());
        assertEquals(0, rootCategoryResponse.getParentId());
        AddCategoryRequest childCategory = new AddCategoryRequest("Child_Category", rootCategoryResponse.getId());
        MvcResult addChildCategoryResult = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(childCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse childCategoryResponse = objectMapper.readValue(addChildCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertEquals(rootCategoryResponse.getName(), childCategoryResponse.getParentName());
        assertEquals(rootCategoryResponse.getId(), childCategoryResponse.getParentId());
        assertEquals(childCategory.getName(), childCategoryResponse.getName());
    }


    @Test
    public void getCategory() throws Exception {
        AddCategoryRequest rootCategory = new AddCategoryRequest("Root_Category", 0);
        MvcResult addCategoryResult = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse = objectMapper.readValue(addCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse.getParentName());
        assertEquals(0, rootCategoryResponse.getParentId());
        MvcResult getCategoryResult = mockMvc.perform(get("/api/categories/{category_number}", String.valueOf(rootCategoryResponse.getId()))
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse getCategoryResponse = objectMapper.readValue(getCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
//        System.out.println(getCategoryResponse);
        assertEquals(rootCategoryResponse.getName(), getCategoryResponse.getName());
        assertEquals(rootCategoryResponse.getId(), getCategoryResponse.getId());
    }

    @Test
    public void putRootCategory() throws Exception {
        AddCategoryRequest rootCategory = new AddCategoryRequest("Root_Category", 0);
        MvcResult addCategoryResult = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse = objectMapper.readValue(addCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse.getParentName());
        assertEquals(0, rootCategoryResponse.getParentId());
        EditCategoryRequest putCategory = new EditCategoryRequest("Category_Root", 0);
        MvcResult getCategoryResult = mockMvc.perform(put("/api/categories/{category_number}", String.valueOf(rootCategoryResponse.getId()))
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse putCategoryResponse = objectMapper.readValue(getCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertEquals(rootCategoryResponse.getId(), putCategoryResponse.getId());
        assertNotEquals(rootCategoryResponse.getName(), putCategoryResponse.getName());
    }

    @Test
    public void putRootToChildCategory_withError() throws Exception {
        AddCategoryRequest rootCategory1 = new AddCategoryRequest("Root_Category-1", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory1)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse1 = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse1.getParentName());
        assertEquals(0, rootCategoryResponse1.getParentId());
        AddCategoryRequest rootCategory2 = new AddCategoryRequest("Root_Category-2", 0);
        MvcResult addCategoryResult2 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory2)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse2 = objectMapper.readValue(addCategoryResult2.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse2.getParentName());
        assertEquals(0, rootCategoryResponse2.getParentId());
        EditCategoryRequest putCategory = new EditCategoryRequest(rootCategoryResponse1.getName(), rootCategoryResponse2.getId());
        mockMvc.perform(put("/api/categories/{category_number}", String.valueOf(rootCategoryResponse1.getId()))
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putCategory)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putChildCategory() throws Exception {
        AddCategoryRequest rootCategory1 = new AddCategoryRequest("Root_Category-1", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory1)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse1 = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse1.getParentName());
        assertEquals(0, rootCategoryResponse1.getParentId());
        AddCategoryRequest rootCategory2 = new AddCategoryRequest("Root_Category-2", 0);
        MvcResult addCategoryResult2 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory2)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse2 = objectMapper.readValue(addCategoryResult2.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse2.getParentName());
        assertEquals(0, rootCategoryResponse2.getParentId());
        AddCategoryRequest childCategory = new AddCategoryRequest("Child_Category", rootCategoryResponse1.getId());
        MvcResult addChildCategoryResult = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(childCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse childCategoryResponse = objectMapper.readValue(addChildCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertEquals(rootCategoryResponse1.getName(), childCategoryResponse.getParentName());
        assertEquals(rootCategoryResponse1.getId(), childCategoryResponse.getParentId());
        EditCategoryRequest putCategory = new EditCategoryRequest(null, rootCategoryResponse2.getId());
        MvcResult getCategoryResult = mockMvc.perform(put("/api/categories/{category_number}", String.valueOf(childCategoryResponse.getId()))
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse putCategoryResponse = objectMapper.readValue(getCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertEquals(rootCategoryResponse2.getId(), putCategoryResponse.getParentId());
        assertEquals(rootCategoryResponse2.getName(), putCategoryResponse.getParentName());
    }

    @Test
    public void putChildToRootCategory_withError() throws Exception {
        AddCategoryRequest rootCategory = new AddCategoryRequest("Root_Category", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse.getParentName());
        assertEquals(0, rootCategoryResponse.getParentId());
        AddCategoryRequest childCategory = new AddCategoryRequest("Child_Category", rootCategoryResponse.getId());
        MvcResult addChildCategoryResult = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(childCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse childCategoryResponse = objectMapper.readValue(addChildCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertEquals(rootCategoryResponse.getName(), childCategoryResponse.getParentName());
        assertEquals(rootCategoryResponse.getId(), childCategoryResponse.getParentId());
        EditCategoryRequest putCategory = new EditCategoryRequest("Child_Category", 0);
        mockMvc.perform(put("/api/categories/{category_number}", String.valueOf(childCategoryResponse.getId()))
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putCategory)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putCategoryWithEmptyRequest_withError() throws Exception {
        AddCategoryRequest rootCategory = new AddCategoryRequest("Root_Category", 0);
        MvcResult addCategoryResult1 = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse = objectMapper.readValue(addCategoryResult1.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse.getParentName());
        assertEquals(0, rootCategoryResponse.getParentId());
        AddCategoryRequest childCategory = new AddCategoryRequest("Child_Category", rootCategoryResponse.getId());
        MvcResult addChildCategoryResult = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(childCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse childCategoryResponse = objectMapper.readValue(addChildCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertEquals(rootCategoryResponse.getName(), childCategoryResponse.getParentName());
        assertEquals(rootCategoryResponse.getId(), childCategoryResponse.getParentId());
        EditCategoryRequest putCategory = new EditCategoryRequest(null, 0);
        MvcResult getCategoryResult = mockMvc.perform(put("/api/categories/{category_number}", String.valueOf(childCategoryResponse.getId()))
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putCategory)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void deleteCategory() throws Exception {
        AddCategoryRequest rootCategory = new AddCategoryRequest("Root_Category", 0);
        MvcResult addCategoryResult = mockMvc.perform(post("/api/categories")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rootCategory)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponse rootCategoryResponse = objectMapper.readValue(addCategoryResult.getResponse().getContentAsString(), CategoryResponse.class);
        assertNull(rootCategoryResponse.getParentName());
        assertEquals(0, rootCategoryResponse.getParentId());
        MvcResult result = mockMvc.perform(delete("/api/categories/{category_number}", String.valueOf(rootCategoryResponse.getId()))
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("{}", result.getResponse().getContentAsString());
    }

    @Test
    public void getAllCategories() throws Exception {
        List<AddCategoryRequest> requestList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            requestList.add(new AddCategoryRequest("Root_Category_" + i, 0));
        }
        for (AddCategoryRequest request : requestList) {
            MvcResult addCategoryResult = mockMvc.perform(post("/api/categories")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();
        }
        MvcResult getResult = mockMvc.perform(get("/api/categories")
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        List<CategoryResponse> categoryResponseList = objectMapper.readValue(getResult.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryResponse.class));
        assertFalse(categoryResponseList.isEmpty());
    }
}