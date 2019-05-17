package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.entity.Product;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

import java.util.List;

public interface ProductDao {
    Product insertProduct(Product product);

    Product updateProduct(Product product) throws OnlineShopException;

    Product findProductById(int number) throws OnlineShopException;

    void deleteProductById(int number);

    List<Product> findAllProduct();

    List<Product> findProductsSortedByProductName(int[] category);

    List<Product> findProductsSortedByCategory(int[] category);
}
