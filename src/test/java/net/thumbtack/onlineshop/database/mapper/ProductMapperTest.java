package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.entity.Category;
import net.thumbtack.onlineshop.entity.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductMapperTest {


    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CommonClearDatabaseNode clearDatabaseNode;

    @Before
    public void clear() {
        clearDatabaseNode.clearDatabase();
    }

    @Test
    public void insertProduct() {
        List<Product> productList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            productList.add(new Product(0, "prod_" + i, 1000 * i, 10));
        }
        for (Product product : productList) {
            productMapper.insertProduct(product);
            assertTrue(product.getId() != 0);
        }
        List<Product> allProduct = productMapper.findAllProduct();
        allProduct.forEach(System.out::println);
        assertNotNull(allProduct);
        assertEquals(9, allProduct.size());
    }

    @Test
    public void insertProductWithCategory() {
        Product product = new Product(0, "prodWithCategory", 1000, 10);
        productMapper.insertProduct(product);
        assertTrue(product.getId() != 0);
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            categories.add(new Category(0, "category_" + i, 0));
        }
        for (Category category : categories) {
            categoryMapper.insertCategory(category);
            assertTrue(category.getId() != 0);
        }
        productMapper.insertProductToCategory(product, categories);
        Product productById = productMapper.findProductById(product.getId());
        assertEquals("prodWithCategory", productById.getName());
        assertEquals(10, productById.getCategories().size());
    }

    @Test
    public void findProductById() {
        Product product = new Product(0, "prodWithCategory", 1000, 10);
        productMapper.insertProduct(product);
        assertTrue(product.getId() != 0);
        List<Category> categories = new ArrayList<>();
        List<Category> subCategories = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            categories.add(new Category(0, "category_" + i, 0));
            subCategories.add(new Category(0, "subCategory_" + i, 0));
        }
        for (int i = 0; i < 4; i++) {
            categoryMapper.insertCategory(categories.get(i));
            assertTrue(categories.get(i).getId() != 0);
            subCategories.get(i).setParentId(categories.get(i).getId());
            categoryMapper.insertCategory(subCategories.get(i));
            assertTrue(subCategories.get(i).getId() != 0);
        }
        productMapper.insertProductToCategory(product, categories);
        Product productById = productMapper.findProductById(product.getId());
        assertEquals("prodWithCategory", productById.getName());
        assertEquals(4, productById.getCategories().size());
        assertEquals(categories.get(0).getId(), productById.getCategories().get(0).getId());
        assertEquals(categories.get(0).getName(), productById.getCategories().get(0).getName());
        assertEquals(subCategories.get(0).getId(), productById.getCategories().get(0).getSubCategories().get(0).getId());
    }

    @Test
    public void updateProduct() {
        Product product = new Product(0, "prodUpdate", 10, 0);
        productMapper.insertProduct(product);
        assertTrue(product.getId() != 0);
        product = productMapper.findProductById(product.getId());
        product.setCount(1564);
        product.setPrice(6545);
        productMapper.updateProduct(product);
        Product productById = productMapper.findProductById(product.getId());
        assertEquals(product.getName(), productById.getName());
        assertEquals(1564, productById.getCount());
        assertEquals(6545, productById.getPrice());
        assertEquals(2, productById.getVersion());
    }

    @Test
    public void deleteProductById() {
        Product product = new Product(0, "prodDelete", 10, 0);
        productMapper.insertProduct(product);
        assertTrue(product.getId() != 0);
        productMapper.deleteProductById(product.getId());
        Product productById = productMapper.findProductById(product.getId());
        assertTrue(productById.isDeleted());
    }

    @Test
    public void findProductByCategorySortedByProductName() {
        List<String> name = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            name.add(i + "_Prod");
        }
        List<Category> categories;
        int[] catId = new int[10];
        for (int i = 0; i < name.size(); i++) {
            String s = name.get(i);
            categories = insertSetProduct(s);
            catId[i] = categories.get(0).getId();
        }
        List<Product> productByCategoryWithEmptyMas = productMapper.findProductByCategorySortedByProductName(new int[0]);
        List<Product> productByCategoryWithCat = productMapper.findProductByCategorySortedByProductName(catId);
        assertEquals(10, productByCategoryWithEmptyMas.size());
        assertTrue(productByCategoryWithEmptyMas.get(0).getCategories().isEmpty());
        assertEquals(8, productByCategoryWithCat.get(0).getCategories().size());
        assertFalse(productByCategoryWithCat.get(0).getCategories().isEmpty());
    }

    @Test
    public void findProductByCategorySortedByCategory() {
        List<String> name = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            name.add(i + "_Prod");
        }
        List<Category> categories;
        int[] catId = new int[10];
        for (int i = 0; i < name.size(); i++) {
            String s = name.get(i);
            categories = insertSetProduct(s);
            catId[i] = categories.get(0).getId();
        }
        List<Product> productByCategoryWithoutMas = productMapper.findProductByCategorySortedByCategory(null);
        List<Product> productByCategoryWithEmptyMas = productMapper.findProductByCategorySortedByCategory(new int[0]);
        List<Product> productByCategoryWithMas = productMapper.findProductByCategorySortedByCategory(catId);
        assertNull(productByCategoryWithoutMas.get(0).getCategories());
        assertEquals(1, productByCategoryWithoutMas.get(11).getCategories().size());
        assertEquals(1, productByCategoryWithEmptyMas.get(0).getCategories().size());
        assertEquals(1, productByCategoryWithEmptyMas.get(11).getCategories().size());
        assertEquals(1, productByCategoryWithMas.get(0).getCategories().size());
        assertEquals(1, productByCategoryWithMas.get(9).getCategories().size());
    }

    private List<Category> insertSetProduct(String prodName) {
        Product product = new Product(0, prodName, 1000, 10);
        productMapper.insertProduct(product);
        productMapper.insertProduct(new Product(0, prodName + "_WithoutCategory", 1000, 10));
        assertTrue(product.getId() != 0);
        List<Category> categories = new ArrayList<>();
        List<Category> subCategories = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            categories.add(new Category(0, prodName + "_cat_" + i, 0));
            subCategories.add(new Category(0, prodName + "_subCat_" + i, 0));
        }
        for (int i = 0; i < 4; i++) {
            categoryMapper.insertCategory(categories.get(i));
            assertTrue(categories.get(i).getId() != 0);
            subCategories.get(i).setParentId(categories.get(i).getId());
            categoryMapper.insertCategory(subCategories.get(i));
            assertTrue(subCategories.get(i).getId() != 0);
        }
        productMapper.insertProductToCategory(product, categories);
        productMapper.insertProductToCategory(product, subCategories);
        return categories;
    }
}