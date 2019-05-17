package net.thumbtack.onlineshop.database.daoimpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.support.DaoImplBase;
import net.thumbtack.onlineshop.entity.Product;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Slf4j
public class ProductDaoImpl extends DaoImplBase implements ProductDao {

    @Autowired
    public ProductDaoImpl(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate);
    }

    @Override
    @Transactional
    public Product insertProduct(Product product) {
        log.debug("ProductDao insert product: {}", product);
        getProductMapper().insertProduct(product);
        if (!product.getCategories().isEmpty()) {
            getProductMapper().insertProductToCategory(product, product.getCategories());
        }
        return product;
    }

    @Override
    @Transactional(rollbackFor = OnlineShopException.class)
    public Product updateProduct(Product product) throws OnlineShopException {
        log.debug("ProductDao update product: {}", product);
        getProductMapper().deleteCategoriesFromProduct(product);
        if (getProductMapper().updateProduct(product) != 1) {
            throw new OnlineShopException("priduct", OnlineShopErrorCode.DATABASE_UPDATE_PRODUCT);
        }
        if (!product.getCategories().isEmpty()) {
            getProductMapper().insertProductToCategory(product, product.getCategories());
        }
        return product;
    }

    @Override
    @Transactional(rollbackFor = OnlineShopException.class)
    public Product findProductById(int id) throws OnlineShopException {
        log.debug("ProductDao find product by id: {}", id);
        Product product = getProductMapper().findProductById(id);
        if (product == null) {
            throw new OnlineShopException("id", OnlineShopErrorCode.DATABASE_FIND_PRODUCT_BY_ID_ERROR);
        }
        return product;
    }

    @Override
    @Transactional
    public void deleteProductById(int id) {
        log.debug("ProductDao delete product by id: {}", id);
        getProductMapper().deleteProductById(id);
    }

    @Override
    @Transactional
    public List<Product> findAllProduct() {
        log.debug("ProductDao find undefined product");
        return getProductMapper().findAllProduct();
    }

    @Override
    @Transactional
    public List<Product> findProductsSortedByProductName(int[] category) {
        log.debug("ProductDao find undefined product sorted by name");
        return getProductMapper().findProductByCategorySortedByProductName(category);
    }

    @Override
    @Transactional
    public List<Product> findProductsSortedByCategory(int[] category) {
        log.debug("ProductDao find undefined product sorted by category");
        return getProductMapper().findProductByCategorySortedByCategory(category);
    }

}
