package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.EmptyResponse;
import net.thumbtack.onlineshop.dto.category.AddCategoryRequest;
import net.thumbtack.onlineshop.dto.category.CategoryResponse;
import net.thumbtack.onlineshop.dto.edit.EditCategoryRequest;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;

@RestController
@RequestMapping("/api")
class CategoryController {

    @Autowired
    private CategoriesService categoriesService;

    @PostMapping("/categories")
    public ResponseEntity addCategory(@CookieValue(COOKIE_NAME) String javaSessionId, @Valid @RequestBody AddCategoryRequest request) throws OnlineShopException {
        CategoryResponse categoryResponseResponse = categoriesService.addCategory(javaSessionId, request);
        return ResponseEntity.ok().body(categoryResponseResponse);
    }

    @GetMapping("/categories/{category_number}")
    public ResponseEntity getCategory(@CookieValue(COOKIE_NAME) String javaSessionId, @PathVariable("category_number") int number) throws OnlineShopException {
        CategoryResponse categoryResponseResponse = categoriesService.getCategoryById(javaSessionId, number);
        return ResponseEntity.ok().body(categoryResponseResponse);
    }

    @PutMapping("/categories/{category_number}")
    public ResponseEntity putCategory(@CookieValue(COOKIE_NAME) String javaSessionId, @PathVariable("category_number") int number, @Valid @RequestBody EditCategoryRequest request) throws OnlineShopException {
        CategoryResponse categoryResponseResponse = categoriesService.editCategory(javaSessionId, number, request);
        return ResponseEntity.ok().body(categoryResponseResponse);
    }

    @DeleteMapping("/categories/{category_number}")
    public ResponseEntity deleteCategory(@CookieValue(COOKIE_NAME) String javaSessionId, @PathVariable("category_number") int number) throws OnlineShopException {
        categoriesService.deleteCategoryById(javaSessionId, number);
        return ResponseEntity.ok().body(new EmptyResponse());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(@CookieValue(COOKIE_NAME) String javaSessionId) throws OnlineShopException {
        List<CategoryResponse> responseList = categoriesService.getCategoryList(javaSessionId);
        return ResponseEntity.ok().body(responseList);
    }
}

