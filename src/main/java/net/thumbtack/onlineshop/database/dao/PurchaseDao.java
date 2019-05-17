package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.entity.*;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

import java.util.List;

public interface PurchaseDao {

    void buyProduct(Client client, Product product, int count) throws OnlineShopException;

    void addProductToBasket(Client client, Product product, int count);

    Basket getBasketByClient(Client client);

    void changeProductToBasket(Client client, Product product, int count);

    void deleteProductFromBasket(Client client, int id);

    void buyProductFromBasket(Client client, List<BasketItem> productsFromRequest, int totalCount) throws OnlineShopException;

    List<Purchase> getGlobalReport(Order order, Integer offset, Integer limit);

    List<Purchase> getReportByClients(int[] masId, Order order, Integer offset, Integer limit);

    List<Purchase> getReportByProducts(int[] masId, Order order, Integer offset, Integer limit);

    List<Purchase> getReportByCategory(int[] masId, Order order, Integer offset, Integer limit);

}
