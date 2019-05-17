package net.thumbtack.onlineshop.service.implementation;

import net.thumbtack.onlineshop.database.dao.AdministratorDao;
import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.ClientDao;
import net.thumbtack.onlineshop.database.dao.UserDao;
import net.thumbtack.onlineshop.dto.category.AddCategoryRequest;
import net.thumbtack.onlineshop.dto.category.CategoryResponse;
import net.thumbtack.onlineshop.dto.edit.EditCategoryRequest;
import net.thumbtack.onlineshop.entity.Category;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoriesServiceImpl extends BaseService implements CategoriesService {

    @Autowired
    CategoriesServiceImpl(AdministratorDao administratorDao, ClientDao clientDao, UserDao userDao, CategoryDao categoryDao) {
        super(administratorDao, clientDao, userDao, categoryDao, null);
    }

    @Override
    public CategoryResponse addCategory(String javaSessionId, AddCategoryRequest request) throws OnlineShopException {
        getAdminByCookie(javaSessionId);
        Category category;
        Category parentCategory = null;
        if (request.getParentId() == 0) {
            category = categoryDao.addCategory(new Category(0, request.getName(), 0));
        } else {
            parentCategory = categoryDao.findCategoryById(request.getParentId());
            category = categoryDao.addCategory(new Category(0, request.getName(), request.getParentId()));
        }
        return categoryResponseBuilder(category, parentCategory);
    }

    @Override
    public CategoryResponse getCategoryById(String javaSessionId, int id) throws OnlineShopException {
        getAdminByCookie(javaSessionId);
        if (id == 0) {
            throw new OnlineShopException("id", OnlineShopErrorCode.WRONG_CATEGORY_ID);
        }
        Category category = categoryDao.findCategoryById(id);
        Category parentCategory = null;
        if (category.getParentId() != null) {
            parentCategory = categoryDao.findCategoryById(category.getParentId());
        }
        return categoryResponseBuilder(category, parentCategory);
    }

    @Override
    public CategoryResponse editCategory(String javaSessionId, int id, EditCategoryRequest request) throws OnlineShopException {
        if (request.getParentId() == 0 && request.getName() == null) {
            throw new OnlineShopException("request", OnlineShopErrorCode.EMPTY_REQUEST);
        }
        getAdminByCookie(javaSessionId);
        Category category = categoryDao.findCategoryById(id);
        if (request.getName() != null && !request.getName().isEmpty()) {
            category.setName(request.getName());
        }
        if (category.getParentId() == null) {
            if (request.getParentId() != 0) {
                throw new OnlineShopException("parentId", OnlineShopErrorCode.ERROR_CHANGE_CATEGORY_TYPE);
            }
        } else {
            if (request.getParentId() != 0) {
                category.setParentId(request.getParentId());
            } else {
                throw new OnlineShopException("parentId", OnlineShopErrorCode.ERROR_CHANGE_CATEGORY_TYPE);
            }
        }
        category = categoryDao.updateCategory(category);
        Category parentCategory = null;
        if (category.getParentId() != null) {
            parentCategory = categoryDao.findCategoryById(category.getParentId());
        }
        return categoryResponseBuilder(category, parentCategory);
    }

    @Override
    public void deleteCategoryById(String javaSessionId, int id) throws OnlineShopException {
        getAdminByCookie(javaSessionId);
        if (id == 0) {
            throw new OnlineShopException("id", OnlineShopErrorCode.WRONG_CATEGORY_ID);
        }
        categoryDao.deleteCategoryById(id);
    }

    @Override
    public List<CategoryResponse> getCategoryList(String javaSessionId) throws OnlineShopException {
        getAdminByCookie(javaSessionId);
        List<Category> allCategory = categoryDao.findAllCategory();
        List<CategoryResponse> allCategoryResponse = new ArrayList<>();
        for (Category rootCategory : allCategory) {
            allCategoryResponse.add(categoryResponseBuilder(rootCategory, null));
            for (Category childCategory : rootCategory.getSubCategories()) {
                allCategoryResponse.add(categoryResponseBuilder(childCategory, rootCategory));// only 2 level depth for categoryResponse
            }
        }
        return allCategoryResponse;
    }

    private CategoryResponse categoryResponseBuilder(Category category, Category parentCategory) {
        if (parentCategory == null) {
            return CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build();
        }
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(parentCategory.getId())
                .parentName(parentCategory.getName())
                .build();
    }
}
