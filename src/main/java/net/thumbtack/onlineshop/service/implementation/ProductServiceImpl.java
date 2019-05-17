package net.thumbtack.onlineshop.service.implementation;

import net.thumbtack.onlineshop.database.dao.*;
import net.thumbtack.onlineshop.dto.edit.EditProductRequest;
import net.thumbtack.onlineshop.dto.product.AddProductRequest;
import net.thumbtack.onlineshop.dto.product.ProductWithCategoryNameResponse;
import net.thumbtack.onlineshop.dto.product.SimpleProductResponse;
import net.thumbtack.onlineshop.entity.Category;
import net.thumbtack.onlineshop.entity.Order;
import net.thumbtack.onlineshop.entity.Product;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {

    @Autowired
    ProductServiceImpl(AdministratorDao administratorDao, ClientDao clientDao, UserDao userDao, CategoryDao categoryDao, ProductDao productDao) {
        super(administratorDao, clientDao, userDao, categoryDao, productDao);
    }

    @Override
    public SimpleProductResponse addProduct(String javaSessionId, AddProductRequest request) throws OnlineShopException {
        getAdminByCookie(javaSessionId);
        Product product = new Product(0, request.getName(), request.getPrice(), request.getCount());
        if (request.getCategories() != null) {

            for (int id : request.getCategories()) {
                product.addCategories(categoryDao.findCategoryById(id));
            }
        } else {
            request.setCategories(new int[0]);
        }
        product = productDao.insertProduct(product);
        return new SimpleProductResponse(product.getId(), product.getName(), product.getPrice(), product.getCount(), request.getCategories());
    }

    @Override
    public SimpleProductResponse editProduct(String javaSessionId, EditProductRequest request, int number) throws OnlineShopException {
        getAdminByCookie(javaSessionId);
        boolean emptyRequest = true;
        Product product = productDao.findProductById(number);
        if (request.getName() != null && !request.getName().isEmpty()) {
            product.setName(request.getName());
            emptyRequest = false;
        }
        if (request.getPrice() != null && request.getPrice() != 0) {
            product.setPrice(request.getPrice());
            emptyRequest = false;
        }
        if (request.getCount() != null) {
            product.setCount(request.getCount());
            emptyRequest = false;
        }
        if (request.getCategories() != null) {
            List<Category> categoryList = new ArrayList<>();
            for (int id : request.getCategories()) {
                categoryList.add(categoryDao.findCategoryById(id));
            }
            product.setCategories(categoryList);
            emptyRequest = false;
        }
        if (!emptyRequest) {
            product = productDao.updateProduct(product);
        }
        int[] categoriesId = new int[product.getCategories().size()];
        for (int i = 0; i < product.getCategories().size(); i++) {
            categoriesId[i] = product.getCategories().get(i).getId();
        }
        return new SimpleProductResponse(product.getId(), product.getName(), product.getPrice(), product.getCount(), categoriesId);
    }

    @Override
    public void deleteProductById(String javaSessionId, int number) throws OnlineShopException {
        getAdminByCookie(javaSessionId);
        productDao.deleteProductById(number);
    }

    @Override
    public ProductWithCategoryNameResponse getProductById(String javaSessionId, int number) throws OnlineShopException {
        getUserByCookie(javaSessionId);
        Product product = productDao.findProductById(number);
        List<String> categoryName = new ArrayList<>();
        for (Category category : product.getCategories()) {
            categoryName.add(category.getName());
        }
        return new ProductWithCategoryNameResponse(product.getId(), product.getName(), product.getPrice(), product.getCount(), categoryName.toArray(new String[0]));
    }

    @Override
    public List<ProductWithCategoryNameResponse> getProductByParam(String javaSessionId, int[] category, String paramOrder) throws OnlineShopException {
        Order order;
        try {
            order = Order.valueOf(paramOrder);
        } catch (IllegalArgumentException ex) {
            throw new OnlineShopException("order", OnlineShopErrorCode.WRONG_PARAM);
        }
        getUserByCookie(javaSessionId);
        List<Product> products = null;
        switch (order) {
            case product: {
                if (category == null) {
                    products = productDao.findAllProduct();
                } else {
                    products = productDao.findProductsSortedByProductName(category);
                }
                break;
            }
            case category: {
                products = productDao.findProductsSortedByCategory(category);
                break;
            }
            default: {
                throw new OnlineShopException("order", OnlineShopErrorCode.WRONG_PARAM);
            }
        }
        if (products == null) {
            throw new OnlineShopException("category", OnlineShopErrorCode.PRODUCT_NOT_FOUND);
        }
        List<ProductWithCategoryNameResponse> responses = new ArrayList<>();
        for (Product product : products) {
            responses.add(getProductWithCategoryNameResponse(product));
        }
        return responses;
    }

    private ProductWithCategoryNameResponse getProductWithCategoryNameResponse(Product product) {
        if (product.getCategories() == null) {
            return new ProductWithCategoryNameResponse(product.getId(), product.getName(), product.getPrice(), product.getCount(), null);
        }
        List<String> categoryName = new ArrayList<>();
        for (Category category : product.getCategories()) {
            categoryName.add(category.getName());
        }
        return new ProductWithCategoryNameResponse(product.getId(), product.getName(), product.getPrice(), product.getCount(), categoryName.toArray(new String[0]));
    }

}
