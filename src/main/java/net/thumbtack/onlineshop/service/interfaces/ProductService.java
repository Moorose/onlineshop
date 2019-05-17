package net.thumbtack.onlineshop.service.interfaces;

import net.thumbtack.onlineshop.dto.edit.EditProductRequest;
import net.thumbtack.onlineshop.dto.product.AddProductRequest;
import net.thumbtack.onlineshop.dto.product.ProductWithCategoryNameResponse;
import net.thumbtack.onlineshop.dto.product.SimpleProductResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

import java.util.List;

public interface ProductService {
    SimpleProductResponse addProduct(String javaSessionId, AddProductRequest request) throws OnlineShopException;

    SimpleProductResponse editProduct(String javaSessionId, EditProductRequest request, int number) throws OnlineShopException;

    void deleteProductById(String javaSessionId, int number) throws OnlineShopException;

    ProductWithCategoryNameResponse getProductById(String javaSessionId, int number) throws OnlineShopException;

    List<ProductWithCategoryNameResponse> getProductByParam(String javaSessionId, int[] category, String order) throws OnlineShopException;
}
