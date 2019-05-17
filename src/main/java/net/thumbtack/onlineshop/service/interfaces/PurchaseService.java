package net.thumbtack.onlineshop.service.interfaces;

import net.thumbtack.onlineshop.dto.deposit.AddMoneyRequest;
import net.thumbtack.onlineshop.dto.product.BuyProductRequest;
import net.thumbtack.onlineshop.dto.product.BuyProductResponse;
import net.thumbtack.onlineshop.dto.product.BuyProductsResponse;
import net.thumbtack.onlineshop.dto.product.ChangeProductRequest;
import net.thumbtack.onlineshop.dto.report.ReportResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

import java.util.List;

public interface PurchaseService {
    ClientRegistrationResponse addMoney(String javaSessionId, AddMoneyRequest request) throws OnlineShopException;

    ClientRegistrationResponse getDeposit(String javaSessionId) throws OnlineShopException;

    BuyProductResponse buyProduct(String javaSessionId, BuyProductRequest request) throws OnlineShopException;

    BuyProductsResponse buyProduct(String javaSessionId, List<BuyProductRequest> request) throws OnlineShopException;

    List<BuyProductResponse> addProductToBasket(String javaSessionId, BuyProductRequest request) throws OnlineShopException;

    void deleteProductFromBasket(String javaSessionId, int id) throws OnlineShopException;

    List<BuyProductResponse> changeProductCount(String javaSessionId, ChangeProductRequest request) throws OnlineShopException;

    List<BuyProductResponse> getProductsFromBasket(String javaSessionId) throws OnlineShopException;

    ReportResponse getReport(String javaSessionId, String type, int[] masId, String order, boolean onlyTotal, Integer offset, Integer limit) throws OnlineShopException;
}
