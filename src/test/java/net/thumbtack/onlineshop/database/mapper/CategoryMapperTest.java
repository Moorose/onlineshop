package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.entity.Category;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryMapperTest {


    @Autowired
    private CommonClearDatabaseNode clearDatabaseNode;

    @Autowired
    private CategoryMapper categoryMapper;

    @Before
    public void clear() {
        clearDatabaseNode.clearDatabase();
    }

    @Test
    public void insertCategory() {
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            categories.add(new Category(0, "category" + i, null));
        }
        for (Category category : categories) {
            categoryMapper.insertCategory(category);
            assertTrue(category.getId() != 0);
        }
        List<Category> subCategories = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            subCategories.add(new Category(0, "sub_category" + i, categories.get(0).getId()));
        }
        for (Category category : subCategories) {
            categoryMapper.insertCategory(category);
            assertTrue(category.getId() != 0);
        }
    }

    @Test
    public void findCategoryById() {
        Set<Category> categories = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            categories.add(new Category(0, "category" + i, 0));
        }
        for (Category category : categories) {
            categoryMapper.insertCategory(category);
            Set<Category> subCategories = new HashSet<>();
            for (int i = 100; i < 115; i++) {
                subCategories.add(new Category(0, i + "subcategory" + category.getId(), category.getId()));
            }
            for (Category subCategory : subCategories) {
                categoryMapper.insertCategory(subCategory);
            }
        }
        List<Category> allCategory = categoryMapper.findAllCategory();
        for (Category cat : allCategory) {
            assertEquals(15, cat.getSubCategories().size());
//            System.out.println(cat.toString());
        }
    }

    @Test
    public void testTreeCategory() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(0, "Root_category", 0));
        categoryMapper.insertCategory(categories.get(0));
        for (int i = 1; i < 10; i++) {
            categories.add(new Category(0, "category" + i, categories.get(i - 1).getId()));
            categoryMapper.insertCategory(categories.get(i));
        }
        for (Category category : categories) {
            assertTrue(category.getId() != 0);
        }
        Category bufCategory = categoryMapper.findCategoryById(categories.get(0).getId());
        assertFalse(bufCategory.getSubCategories().isEmpty());
        for (int i = 1; i < 10; i++) {
            bufCategory = bufCategory.getSubCategories().get(0);
            assertEquals(categories.get(i).getId(), bufCategory.getId());
        }
    }

    @Test
    public void findAllCategory() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(0, "Root_category", 0));
        categoryMapper.insertCategory(categories.get(0));
        for (int i = 1; i < 10; i++) {
            categories.add(new Category(0, "category" + i, categories.get(i - 1).getId()));
            categoryMapper.insertCategory(categories.get(i));
        }
        for (Category category : categories) {
            assertTrue(category.getId() != 0);
        }
    }

    @Test
    public void updateCategory() {
        Category category1 = new Category(0, "categoryTest1", 0);
        Category category2 = new Category(0, "categoryTest2", null);
        categoryMapper.insertCategory(category1);
        categoryMapper.insertCategory(category2);
        assertTrue(category1.getId() != 0);
        assertTrue(category2.getId() != 0);
        Category subCategory = new Category(0, "subCategoryTest", category1.getId());
        categoryMapper.insertCategory(subCategory);
        assertTrue(subCategory.getId() != 0);
        subCategory.setName("SomeTestName");
        subCategory.setParentId(category2.getId());
        categoryMapper.updateCategory(subCategory);
        Category bufCategory = categoryMapper.findCategoryById(category2.getId());
        assertEquals(subCategory.getName(), bufCategory.getSubCategories().get(0).getName());
    }

    @Test
    public void deleteCategoryById() {
        Category category = new Category(0, "categoryTest1", 0);
        categoryMapper.insertCategory(category);
        categoryMapper.deleteCategoryById(category.getId());
        Category categoryById = categoryMapper.findCategoryById(category.getId());
        assertNull(categoryById);
    }
}