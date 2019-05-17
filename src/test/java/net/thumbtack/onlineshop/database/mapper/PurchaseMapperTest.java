package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.entity.Basket;
import net.thumbtack.onlineshop.entity.BasketItem;
import net.thumbtack.onlineshop.entity.Client;
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
public class PurchaseMapperTest {

    @Autowired
    private CommonClearDatabaseNode clearDatabaseNode;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private UserMapper userMapper;

    Client client;
    private List<Product> products;

    @Before
    public void clear() {
        clearDatabaseNode.clearDatabase();

        client = new Client(0, "userTest", "last_nameTest", null, "loginTest", "passwordTest", "11111111111", "box_Test@yandex.com", "address", 1000);
        userMapper.insertUser(client);
        assertTrue(client.getId() != 0);
        clientMapper.insertClient(client);
        clientMapper.insertClientDeposit(client);
        products = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            products.add(new Product(0, "prod_" + i, 10, 1000));
        }
        for (Product product : products) {
            productMapper.insertProduct(product);
            assertTrue(product.getId() != 0);
        }
    }

    @Test
    public void testAddProductToBasketAndGetBasket() {
        purchaseMapper.addProductToBasket(client, products.get(0), 10);
        purchaseMapper.addProductToBasket(client, products.get(1), 2);
        purchaseMapper.addProductToBasket(client, products.get(2), 1);
        purchaseMapper.addProductToBasket(client, products.get(3), 5);
        List<BasketItem> basketItems = purchaseMapper.getBasketByClientId(client.getId());
//        basketItems.forEach(System.out::println);
        Basket basket = new Basket(basketItems);
        assertFalse(basket.getBasketItems().isEmpty());
    }

    @Test
    public void buyProduct() {
        client = clientMapper.findClientById(client.getId());
        Product product = productMapper.findProductById(products.get(0).getId());
        int count = 10;
        if (purchaseMapper.updateDepositBeforePurchase(client, count * product.getPrice()) != 1) {
            fail();
        }
        if (purchaseMapper.insertPurchase(client, product, count) != 1) {
            fail();
        }
        if (purchaseMapper.updateProductCountAfterPurchase(product, count) != 1) {
            fail();
        }
        Client clientAfterPurchase = clientMapper.findClientById(client.getId());
        Product productAfterPurchase = productMapper.findProductById(product.getId());
        assertEquals(900, clientAfterPurchase.getMoney());
        assertEquals(990, productAfterPurchase.getCount());
    }

    @Test
    public void deleteProductWithRefKey() {
        client = clientMapper.findClientById(client.getId());
        Product product = productMapper.findProductById(products.get(0).getId());
        Product product2 = productMapper.findProductById(products.get(1).getId());
        purchaseMapper.insertPurchase(client, product, 100);
        purchaseMapper.insertPurchase(client, product2, 100);
        purchaseMapper.addProductToBasket(client, product, 10000);
        purchaseMapper.addProductToBasket(client, product2, 20000);

        List<BasketItem> basketItems = purchaseMapper.getBasketByClientId(client.getId());
        productMapper.deleteProductById(product.getId());
        productMapper.deleteProductById(product2.getId());
        List<BasketItem> basketItemsAfterDelete = purchaseMapper.getBasketByClientId(client.getId());

        assertTrue(productMapper.findProductById(product.getId()).isDeleted());
        assertTrue(productMapper.findProductById(product2.getId()).isDeleted());

        assertEquals(2, basketItems.size());
        assertEquals(10000, basketItems.get(0).getCount());
        assertEquals(20000, basketItems.get(1).getCount());

        assertEquals(2, basketItemsAfterDelete.size());
        assertEquals(10000, basketItemsAfterDelete.get(0).getCount());
        assertTrue(basketItemsAfterDelete.get(0).getProduct().isDeleted());
        assertTrue(basketItemsAfterDelete.get(1).getProduct().isDeleted());
        assertEquals(20000, basketItemsAfterDelete.get(1).getCount());
    }

    @Test
    public void testGetBasketByClient() {
        List<BasketItem> basketItems = purchaseMapper.getBasketByClientId(client.getId());
        if (basketItems == null) {
            fail();
        }
        purchaseMapper.addProductToBasket(client, products.get(0), 10);
        basketItems = purchaseMapper.getBasketByClientId(client.getId());
        assertEquals(1, basketItems.size());
        assertEquals(products.get(0).getName(), basketItems.get(0).getProduct().getName());
    }

    @Test
    public void changeProductToBasket() {
        purchaseMapper.addProductToBasket(client, products.get(0), 10);
        purchaseMapper.changeProductToBasket(client, products.get(0), 100);
        List<BasketItem> basketItems = purchaseMapper.getBasketByClientId(client.getId());
        assertFalse(basketItems.isEmpty());
        assertEquals(100, basketItems.get(0).getCount());
    }

    @Test
    public void deleteProductFromBasket() {
        purchaseMapper.addProductToBasket(client, products.get(0), 10);
        List<BasketItem> basketItems = purchaseMapper.getBasketByClientId(client.getId());
        assertFalse(basketItems.isEmpty());
        if (purchaseMapper.deleteProductFromBasket(client, products.get(0).getId()) != 1) {
            fail();
        }
        basketItems = purchaseMapper.getBasketByClientId(client.getId());
        assertTrue(basketItems.isEmpty());
    }


}