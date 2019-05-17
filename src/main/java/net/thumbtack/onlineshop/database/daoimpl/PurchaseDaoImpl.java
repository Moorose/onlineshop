package net.thumbtack.onlineshop.database.daoimpl;


import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.database.dao.PurchaseDao;
import net.thumbtack.onlineshop.database.support.DaoImplBase;
import net.thumbtack.onlineshop.entity.*;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Slf4j
public class PurchaseDaoImpl extends DaoImplBase implements PurchaseDao {

    @Autowired
    public PurchaseDaoImpl(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate);
    }

    @Override
    @Transactional(rollbackFor = OnlineShopException.class)
    public void buyProduct(Client client, Product product, int count) throws OnlineShopException {
        log.debug("PurchaseDao buy product for user : {} -> {}", product, client);
        if (getPurchaseMapper().updateDepositBeforePurchase(client, count * product.getPrice()) != 1) {
            throw new OnlineShopException("version", OnlineShopErrorCode.DATABASE_UPDATE_DEPOSIT_BEFORE_PURCHASE);
        }
        if (getPurchaseMapper().insertPurchase(client, product, count) != 1) {
            throw new OnlineShopException("product", OnlineShopErrorCode.DATABASE_INSERT_PURCHASE);
        }
        if (getPurchaseMapper().updateProductCountAfterPurchase(product, count) != 1) {
            throw new OnlineShopException("version", OnlineShopErrorCode.DATABASE_UPDATE_PRODUCT_COUNT_FOR_PURCHASE);
        }
    }

    @Override
    @Transactional(rollbackFor = OnlineShopException.class)
    public void buyProductFromBasket(Client client, List<BasketItem> productsFromRequest, int totalCount) throws OnlineShopException {
        log.debug("PurchaseDao buy products from user`s  basket: {} -> {}", productsFromRequest, client);
        if (getPurchaseMapper().updateDepositBeforePurchase(client, totalCount) != 1) {
            throw new OnlineShopException("version", OnlineShopErrorCode.DATABASE_UPDATE_DEPOSIT_BEFORE_PURCHASE);
        }
        for (BasketItem item : productsFromRequest) {
            if (getPurchaseMapper().insertPurchase(client, item.getProduct(), item.getCount()) != 1) {
                throw new OnlineShopException("product", OnlineShopErrorCode.DATABASE_INSERT_PURCHASE);
            }
            if (getPurchaseMapper().updateProductCountAfterPurchase(item.getProduct(), item.getCount()) != 1) {
                throw new OnlineShopException("version", OnlineShopErrorCode.DATABASE_UPDATE_PRODUCT_COUNT_FOR_PURCHASE);
            }
            if (getPurchaseMapper().deleteProductFromBasket(client, item.getProduct().getId()) != 1) {
                throw new OnlineShopException("product", OnlineShopErrorCode.DATABASE_UPDATE_BASKET_ERROR);
            }
        }
    }


    @Override
    @Transactional
    public void addProductToBasket(Client client, Product product, int count) {
        log.debug("PurchaseDao add product to basket: {}, {} -> {}", product, count, client);
        getPurchaseMapper().addProductToBasket(client, product, count);
    }

    @Override
    @Transactional
    public Basket getBasketByClient(Client client) {
        log.debug("PurchaseDao get basket for client : {} ", client);
        List<BasketItem> basketItems = getPurchaseMapper().getBasketByClientId(client.getId());
        return new Basket(basketItems);
    }

    @Override
    @Transactional
    public void changeProductToBasket(Client client, Product product, int count) {
        log.debug("PurchaseDao change product count to basket: {}, {} -> {}", product, count, client);
        getPurchaseMapper().changeProductToBasket(client, product, count);
    }

    @Override
    @Transactional
    public void deleteProductFromBasket(Client client, int id) {
        log.debug("PurchaseDao delete product from basket by id : {}, {}", client, id);
        getPurchaseMapper().deleteProductFromBasket(client, id);
    }

    @Override
    @Transactional
    public List<Purchase> getGlobalReport(Order order, Integer offset, Integer limit) {
        log.debug("PurchaseDao create global report : {}  offset={}, limit={}", order, offset, limit);
        if (order.equals(Order.category)) {
            return getPurchaseMapper().findPurchaseSortedByCategory(offset, limit);
        } else {
            return getPurchaseMapper().findPurchaseSortedByProduct(offset, limit);
        }
    }

    @Override
    @Transactional
    public List<Purchase> getReportByClients(int[] masId, Order order, Integer offset, Integer limit) {
        log.debug("PurchaseDao create report by clients : {}, {}  offset={}, limit={}", masId, order, offset, limit);
        if (order.equals(Order.category)) {
            return getPurchaseMapper().findPurchaseByClientsSortedByCategory(masId, offset, limit);
        } else {
            return getPurchaseMapper().findPurchaseByClientsSortedByProduct(masId, offset, limit);
        }
    }

    @Override
    @Transactional
    public List<Purchase> getReportByProducts(int[] masId, Order order, Integer offset, Integer limit) {
        log.debug("PurchaseDao create report by products : {} ,{}  offset={}, limit={}", masId, order, offset, limit);
        if (order.equals(Order.category)) {
            return getPurchaseMapper().findPurchaseByProductsSortedByCategory(masId, offset, limit);
        } else {
            return getPurchaseMapper().findPurchaseByProductsSortedByProduct(masId, offset, limit);
        }
    }

    @Override
    @Transactional
    public List<Purchase> getReportByCategory(int[] masId, Order order, Integer offset, Integer limit) {
        log.debug("PurchaseDao create report by category : {} ,{}  offset={}, limit={}", masId, order, offset, limit);
        return getPurchaseMapper().findPurchaseByCategory(masId, order.toString(), offset, limit);
    }
}
