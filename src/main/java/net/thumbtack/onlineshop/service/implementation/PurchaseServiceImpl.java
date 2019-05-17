package net.thumbtack.onlineshop.service.implementation;

import net.thumbtack.onlineshop.database.dao.*;
import net.thumbtack.onlineshop.dto.deposit.AddMoneyRequest;
import net.thumbtack.onlineshop.dto.product.BuyProductRequest;
import net.thumbtack.onlineshop.dto.product.BuyProductResponse;
import net.thumbtack.onlineshop.dto.product.BuyProductsResponse;
import net.thumbtack.onlineshop.dto.product.ChangeProductRequest;
import net.thumbtack.onlineshop.dto.report.ReportResponse;
import net.thumbtack.onlineshop.dto.report.ReportRow;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.entity.*;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl extends BaseService implements PurchaseService {

    @Autowired
    private PurchaseDao purchaseDao;

    @Autowired
    PurchaseServiceImpl(AdministratorDao administratorDao, ClientDao clientDao, UserDao userDao, ProductDao productDao) {
        super(administratorDao, clientDao, userDao, null, productDao);
    }

    @Override
    public ClientRegistrationResponse addMoney(String javaSessionId, AddMoneyRequest request) throws OnlineShopException {
        Client client = getClientByCookie(javaSessionId);
        clientDao.addMoneyToDeposit(client, request.getDeposit());
        client = getClientByCookie(javaSessionId);
        return responseBuilder(client, null);
    }

    @Override
    public ClientRegistrationResponse getDeposit(String javaSessionId) throws OnlineShopException {
        Client client = getClientByCookie(javaSessionId);
        return responseBuilder(client, null);
    }

    @Override
    public BuyProductResponse buyProduct(String javaSessionId, BuyProductRequest request) throws OnlineShopException {
        Client client = getClientByCookie(javaSessionId);
        Product product = productDao.findProductById(request.getId());
        if (product.isDeleted()) {
            throw new OnlineShopException("deleted", OnlineShopErrorCode.THIS_PRODUCT_IS_NOT_AVAILABLE);
        }
        if (product.getName().equals(request.getName()) && product.getPrice() == request.getPrice()) {
            if (product.getCount() < request.getCountWithSafe()) {
                throw new OnlineShopException("count", OnlineShopErrorCode.NOT_ENOUGH_PRODUCT_IN_STORAGE);
            }
            int totalCheck = product.getPrice() * request.getCountWithSafe();
            if (totalCheck > client.getDeposit().getMoney()) {
                throw new OnlineShopException("deposit", OnlineShopErrorCode.NOT_ENOUGH_MONEY_ON_DEPOSIT);
            }
            purchaseDao.buyProduct(client, product, request.getCountWithSafe());
        } else {
            throw new OnlineShopException("name,price", OnlineShopErrorCode.PARAMS_FOR_PRODUCT_ARE_WRONG);
        }
        return new BuyProductResponse(request.getId(), request.getName(), request.getPrice(), request.getCountWithSafe());
    }

    @Override
    public BuyProductsResponse buyProduct(String javaSessionId, List<BuyProductRequest> requests) throws OnlineShopException {
        Client client = getClientByCookie(javaSessionId);
        List<BasketItem> productsFromRequest = new ArrayList<>();
        for (BuyProductRequest request : requests) {
            productsFromRequest.add(new BasketItem(new Product(request.getId(), request.getName(), request.getPrice()), request.getCountWithoutNull()));
        }
        Basket basket = purchaseDao.getBasketByClient(client);
        int totalCount = compareAndSelectProducts(productsFromRequest, basket.getBasketItems());
        if (productsFromRequest.isEmpty()) {
            throw new OnlineShopException("name,price", OnlineShopErrorCode.PARAMS_FOR_PRODUCT_ARE_WRONG);
        }
        if (totalCount > client.getMoney()) {
            throw new OnlineShopException("deposit", OnlineShopErrorCode.NOT_ENOUGH_MONEY_ON_DEPOSIT);
        }
        purchaseDao.buyProductFromBasket(client, productsFromRequest, totalCount);
        basket = purchaseDao.getBasketByClient(client);
        return new BuyProductsResponse(buildBuyProductResponse(new Basket(productsFromRequest)), buildBuyProductResponse(basket));
    }

    private int compareAndSelectProducts(List<BasketItem> productsFromRequest, List<BasketItem> actualBasketItems) throws OnlineShopException {
        int totalCount = 0;
        boolean valid;
        ListIterator<BasketItem> iterator = productsFromRequest.listIterator();
        while (iterator.hasNext()) {    //check product from request
            BasketItem item = iterator.next();
            valid = true;
            for (BasketItem actualItem : actualBasketItems) {
                if (actualItem.validProduct(item)) {    // validProduct compare product from request with actual product from basket
                    if (item.getCount() == 0) {     // if client didn't add count in request, we should get position`s count from basket
                        item.setCount(actualItem.getCount());
                    } else if (item.getCount() > actualItem.getCount()) {    // Не допускается покупка большего числа единиц, чем имеется в корзине. Если такое имеет место
                        item.setCount(actualItem.getCount());              // для какого-то товара, то покупается то количество единиц, которое есть сейчас в корзине
                    }
                    if (item.getCount() > productDao.findProductById(item.getProduct().getId()).getCount()) {
                        break;
                    }
                    totalCount += item.getCount() * item.getProduct().getPrice();
//                    actualBasketItems.remove(actualItem); // if request have couple same products, it will write in different purchase
                    item.getProduct().setVersion(actualItem.getProduct().getVersion());
                    valid = false;
                    break;
                }
            }
            if (valid) {
                iterator.remove();
            }
        }
        return totalCount;
    }

    @Override
    public List<BuyProductResponse> addProductToBasket(String javaSessionId, BuyProductRequest request) throws OnlineShopException {
        Client client = getClientByCookie(javaSessionId);
        Product product = productDao.findProductById(request.getId());
        if (product.isDeleted()) {
            throw new OnlineShopException("deleted", OnlineShopErrorCode.THIS_PRODUCT_IS_NOT_AVAILABLE);
        }
        if (!product.getName().equals(request.getName())) {
            throw new OnlineShopException("name", OnlineShopErrorCode.PARAMS_FOR_PRODUCT_ARE_WRONG);
        }
        if (product.getPrice() != request.getPrice()) {
            throw new OnlineShopException("price", OnlineShopErrorCode.PARAMS_FOR_PRODUCT_ARE_WRONG);
        }
        purchaseDao.addProductToBasket(client, product, request.getCountWithSafe());
        return buildBuyProductResponse(purchaseDao.getBasketByClient(client));
    }

    @Override
    public void deleteProductFromBasket(String javaSessionId, int id) throws OnlineShopException {
        Client client = getClientByCookie(javaSessionId);
        purchaseDao.deleteProductFromBasket(client, id);
    }

    @Override
    public List<BuyProductResponse> changeProductCount(String javaSessionId, ChangeProductRequest request) throws OnlineShopException {
        Client client = getClientByCookie(javaSessionId);
        Product product = productDao.findProductById(request.getId());
        if (!product.getName().equals(request.getName())) {
            throw new OnlineShopException("name", OnlineShopErrorCode.PARAMS_FOR_PRODUCT_ARE_WRONG);
        }
        if (product.getPrice() != request.getPrice()) {
            throw new OnlineShopException("price", OnlineShopErrorCode.PARAMS_FOR_PRODUCT_ARE_WRONG);
        }
        purchaseDao.changeProductToBasket(client, product, request.getCount());
        return buildBuyProductResponse(purchaseDao.getBasketByClient(client));
    }

    @Override
    public List<BuyProductResponse> getProductsFromBasket(String javaSessionId) throws OnlineShopException {
        Client client = getClientByCookie(javaSessionId);
        return buildBuyProductResponse(purchaseDao.getBasketByClient(client));
    }

    private List<BuyProductResponse> buildBuyProductResponse(Basket basket) {
        List<BuyProductResponse> responses = new ArrayList<>();
        for (BasketItem item : basket.getBasketItems()) {
            responses.add(new BuyProductResponse(item.getProduct().getId(), item.getProduct().getName(), item.getProduct().getPrice(), item.getCount()));
        }
        return responses;
    }

    @Override
    public ReportResponse getReport(String javaSessionId, String typeReq, int[] masId, String orderReq, boolean onlyTotal, Integer offset, Integer limit) throws OnlineShopException {
        getAdminByCookie(javaSessionId);
        Type type;
        Order order;
        try {
            order = Order.valueOf(orderReq);
            type = Type.valueOf(typeReq);
        } catch (IllegalArgumentException ex) {
            throw new OnlineShopException("order", OnlineShopErrorCode.WRONG_PARAM);
        }
        List<Purchase> report = null;
        switch (type) {
            case undefined: {
                report = purchaseDao.getGlobalReport(order, offset, limit);
                break;
            }
            case client: {
                report = purchaseDao.getReportByClients(masId, order, offset, limit);
                break;
            }
            case product: {
                report = purchaseDao.getReportByProducts(masId, order, offset, limit);
                break;
            }
            case category: {
                report = purchaseDao.getReportByCategory(masId, order, offset, limit);
                break;
            }
        }
        int totalPrise = 0;
        if (order != Order.category && type != Type.category) {
            totalPrise = report.stream().mapToInt(purchase -> purchase.getPrice() * purchase.getCount()).sum();
        }
        ////////////////////////////////////
//        report.forEach(System.out::println);
        ////////////////////////////////////
        if (onlyTotal) {
            return new ReportResponse(totalPrise, report.size());
        }
        List<ReportRow> row = new ArrayList();
        for (Purchase purchase : report) {
            Optional<User> optionalUser = Optional.ofNullable(purchase.getClient());
            Product product;
            if ((product = purchase.getProduct()) == null) {
                product = new Product();
            }
            Map<Integer, String> categoryCollect = null;
            if (order == Order.category || type == Type.category) {
                if (purchase.getCategories() != null) {
                    categoryCollect = purchase.getCategories().stream().collect(Collectors.toMap(Category::getId, Category::getName));
                }
            } else {
                if (product.getCategories() != null) {
                    categoryCollect = product.getCategories().stream().collect(Collectors.toMap(Category::getId, Category::getName));
                }
            }
            ReportRow reportRow = ReportRow.builder()
                    .userId(optionalUser.map(User::getId).orElse(0))
                    .productId(product.getId())
                    .currentName(product.getName())
                    .currentPrice(product.getPrice())
                    .deleted(product.isDeleted())
                    .category(categoryCollect)
                    .name(purchase.getName())
                    .price(purchase.getPrice())
                    .count(purchase.getCount())
                    .build();
            row.add(reportRow);
        }
        ReportResponse response = new ReportResponse(totalPrise, report.size(), row);
        return response;
    }
}
