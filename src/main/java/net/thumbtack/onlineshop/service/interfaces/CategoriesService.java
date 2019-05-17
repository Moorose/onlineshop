package net.thumbtack.onlineshop.service.interfaces;

import net.thumbtack.onlineshop.dto.category.AddCategoryRequest;
import net.thumbtack.onlineshop.dto.category.CategoryResponse;
import net.thumbtack.onlineshop.dto.edit.EditCategoryRequest;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

import java.util.List;

public interface CategoriesService {
    CategoryResponse addCategory(String javaSessionId, AddCategoryRequest request) throws OnlineShopException;

    CategoryResponse getCategoryById(String javaSessionId, int number) throws OnlineShopException;

    CategoryResponse editCategory(String javaSessionId, int number, EditCategoryRequest request) throws OnlineShopException;

    void deleteCategoryById(String javaSessionId, int number) throws OnlineShopException;

    List<CategoryResponse> getCategoryList(String javaSessionId) throws OnlineShopException;
}
