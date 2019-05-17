package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.entity.Category;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

import java.util.List;

public interface CategoryDao {

    Category addCategory(Category category);

    Category findCategoryById(int id) throws OnlineShopException;

    Category updateCategory(Category category);

    void deleteCategoryById(int id);

    List<Category> findAllCategory();

}
