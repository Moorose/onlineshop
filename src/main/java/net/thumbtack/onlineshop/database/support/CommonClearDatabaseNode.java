package net.thumbtack.onlineshop.database.support;

import net.thumbtack.onlineshop.database.mapper.CategoryMapper;
import net.thumbtack.onlineshop.database.mapper.ProductMapper;
import net.thumbtack.onlineshop.database.mapper.PurchaseMapper;
import net.thumbtack.onlineshop.database.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CommonClearDatabaseNode {

    private ProductMapper productMapper;
    private PurchaseMapper purchaseMapper;
    private CategoryMapper categoryMapper;
    private UserMapper userMapper;

    @Autowired
    public CommonClearDatabaseNode(ProductMapper productMapper, PurchaseMapper purchaseMapper, CategoryMapper categoryMapper, UserMapper userMapper) {
        this.productMapper = productMapper;
        this.purchaseMapper = purchaseMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public void clearDatabase() {
        purchaseMapper.deleteAllPurchase();
        userMapper.deleteAllUser();
        categoryMapper.deleteAllCategory();
        productMapper.deleteAllProduct();
        purchaseMapper.deleteAllBasketItem();
    }
}
