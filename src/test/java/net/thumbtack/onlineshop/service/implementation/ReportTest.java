package net.thumbtack.onlineshop.service.implementation;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.category.AddCategoryRequest;
import net.thumbtack.onlineshop.dto.category.CategoryResponse;
import net.thumbtack.onlineshop.dto.deposit.AddMoneyRequest;
import net.thumbtack.onlineshop.dto.product.AddProductRequest;
import net.thumbtack.onlineshop.dto.product.BuyProductRequest;
import net.thumbtack.onlineshop.dto.product.BuyProductResponse;
import net.thumbtack.onlineshop.dto.product.SimpleProductResponse;
import net.thumbtack.onlineshop.dto.report.ReportResponse;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.AdminRegistrationResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationRequest;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.CategoriesService;
import net.thumbtack.onlineshop.service.interfaces.ProductService;
import net.thumbtack.onlineshop.service.interfaces.PurchaseService;
import net.thumbtack.onlineshop.service.interfaces.RegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportTest {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    private static String adminCookie;
    private static List<String> clientCookie;
    private static List<Integer> clientId;
    private static List<SimpleProductResponse> productList;
    private static List<CategoryResponse> rootCategory;
    private static List<CategoryResponse> childCategory;
    private static boolean key;


    @Before
    public void beforeClass() throws OnlineShopException {
        if (key) {       // 19.445s vs 2.7s
            return;
        }
        commonClearDatabaseNode.clearDatabase();

        AdminRegistrationRequest adminRegistrationRequest = new AdminRegistrationRequest("Администратор", "Фамилия", null, "admin", "AdminLogin", "password");
        AdminRegistrationResponse adminRegistrationResponse = registrationService.adminRegistration(adminRegistrationRequest);
        assertTrue(adminRegistrationResponse.getId() != 0);
        adminCookie = adminRegistrationResponse.getJavaSessionId();

        clientCookie = new ArrayList<>();
        clientId = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientRegistrationRequest clientRegistrationRequest = new ClientRegistrationRequest("Клиент", "Фамилия", "Отчество", i + "client@gmail.com", "ClientAddress", "7913153346" + i, i + "_ClientLogin", "password123");
            ClientRegistrationResponse clientRegistrationResponse = registrationService.clientRegistration(clientRegistrationRequest);
            assertTrue(clientRegistrationResponse.getId() != 0);
            clientCookie.add(clientRegistrationResponse.getJavaSessionId());
            clientId.add(clientRegistrationResponse.getId());
            ClientRegistrationResponse response = purchaseService.addMoney(clientRegistrationResponse.getJavaSessionId(), new AddMoneyRequest(100000));
            assertEquals(100000, response.getDeposit());
        }

        rootCategory = new ArrayList<>();
        childCategory = new ArrayList<>();
        for (int i = 3; i > 0; i--) {
            CategoryResponse categoryResponse = categoriesService.addCategory(adminCookie, new AddCategoryRequest("RootCat_" + i, 0));
            assertTrue(categoryResponse.getId() != 0);
            rootCategory.add(categoryResponse);
        }
        CategoryResponse catResponse = categoriesService.addCategory(adminCookie, new AddCategoryRequest("RootCatWithoutProduct", 0));
        assertTrue(catResponse.getId() != 0);
        for (CategoryResponse root : rootCategory) {
            for (int i = 3; i > 0; i--) {
                CategoryResponse categoryResponse = categoriesService.addCategory(adminCookie, new AddCategoryRequest("ChildCat_" + i + "_ParentID=" + root.getId(), root.getId()));
                assertTrue(categoryResponse.getId() != 0);
                childCategory.add(categoryResponse);
            }
        }
        productList = new ArrayList<>();
        Queue<CategoryResponse> rootQueue = new ArrayDeque<>(rootCategory);
        Queue<CategoryResponse> childQueue = new ArrayDeque<>(childCategory);
        for (int i = 0; i < 10; i++) {
            int[] catId = new int[4];
            CategoryResponse currentRoot = rootQueue.poll();
            catId[0] = currentRoot.getId();
            rootQueue.offer(currentRoot);
            for (int y = 1; y < catId.length; y++) {
                CategoryResponse currentChild = childQueue.poll();
                catId[y] = currentChild.getId();
                childQueue.offer(currentChild);
            }
            AddProductRequest addProductRequest = new AddProductRequest("Product_" + i, 100, 1000, catId);
            productList.add(productService.addProduct(adminCookie, addProductRequest));
            assertTrue(productList.get(i).getId() != 0);
        }
        AddProductRequest addProductRequest = new AddProductRequest("Product_WithoutCategory_1", 100, 1000, null);
        productList.add(productService.addProduct(adminCookie, addProductRequest));
        addProductRequest = new AddProductRequest("Product_WithoutCategory_2", 100, 1000, null);
        productList.add(productService.addProduct(adminCookie, addProductRequest));

        for (SimpleProductResponse product : productList) {
            for (int i = 0; i < 4; i++) {
                for (String cookie : clientCookie) {
                    BuyProductResponse buyProduct = purchaseService.buyProduct(cookie, new BuyProductRequest(product.getId(), product.getName(), product.getPrice(), 5));
                }
            }
        }
        key = true;
    }

    @Test
    public void report_test_with_all_order_product() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "undefined", null, "product", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(144, report.getTotalCount());
        assertEquals(72000, report.getTotalPrise());
    }

    @Test
    public void report_test_with_all_order_product_with_offset() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "undefined", null, "product", false, 10, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(100, report.getTotalCount());
        assertEquals(50000, report.getTotalPrise());
    }


    @Test
    public void report_test_with_all_order_product_with_limit() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "undefined", null, "product", false, null, 10);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(10, report.getTotalCount());
        assertEquals(5000, report.getTotalPrise());
    }

    @Test
    public void report_test_with_all_order_product_with_offset_and_limit() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "undefined", null, "product", false, 100, 10);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(10, report.getTotalCount());
        assertEquals(5000, report.getTotalPrise());
    }

    @Test
    public void report_test_with_all_order_category() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "undefined", null, "category", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(504, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_all_clients_order_product() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "client", null, "product", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(144, report.getTotalCount());
        assertEquals(72000, report.getTotalPrise());
    }

    @Test
    public void report_test_with_2_client_order_product() throws OnlineShopException {
        int[] mas = new int[]{clientId.get(0), clientId.get(1)};
        ReportResponse report = purchaseService.getReport(adminCookie, "client", mas, "product", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(96, report.getTotalCount());
        assertEquals(48000, report.getTotalPrise());
    }

    @Test
    public void report_test_with_empty_client_order_product() throws OnlineShopException {
        int[] mas = new int[0];
        ReportResponse report = purchaseService.getReport(adminCookie, "client", mas, "product", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(144, report.getTotalCount());
        assertEquals(72000, report.getTotalPrise());
    }

    @Test
    public void report_test_with_all_clients_order_category() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "client", null, "category", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(504, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_2_client_order_category() throws OnlineShopException {
        int[] mas = new int[]{clientId.get(0), clientId.get(1)};
        ReportResponse report = purchaseService.getReport(adminCookie, "client", mas, "category", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(336, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_empty_client_order_category() throws OnlineShopException {
        int[] mas = new int[0];
        ReportResponse report = purchaseService.getReport(adminCookie, "client", mas, "category", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(504, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_product_order_product() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "product", null, "product", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = "+report.getTotalCount());
//        System.out.println("getTotalPrise = "+report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(144, report.getTotalCount());
        assertEquals(72000, report.getTotalPrise());
    }

    @Test
    public void report_test_with_empty_products_order_product() throws OnlineShopException {
        int[] mas = new int[0];
        ReportResponse report = purchaseService.getReport(adminCookie, "product", mas, "product", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(144, report.getTotalCount());
        assertEquals(72000, report.getTotalPrise());
    }

    @Test
    public void report_test_with_2_products_order_product() throws OnlineShopException {
        int[] mas = new int[]{productList.get(0).getId(), productList.get(1).getId(),};
        ReportResponse report = purchaseService.getReport(adminCookie, "product", mas, "product", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(24, report.getTotalCount());
        assertEquals(12000, report.getTotalPrise());
    }

    @Test
    public void report_test_with_product_order_category() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "product", null, "category", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(504, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_empty_products_order_category() throws OnlineShopException {
        int[] mas = new int[0];
        ReportResponse report = purchaseService.getReport(adminCookie, "product", mas, "category", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(504, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_2_products_order_category() throws OnlineShopException {
        int[] mas = new int[]{productList.get(0).getId(), productList.get(1).getId(),};
        ReportResponse report = purchaseService.getReport(adminCookie, "product", mas, "category", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(96, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_category_order_product() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "category", null, "product", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(481, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_empty_category_order_product() throws OnlineShopException {
        int[] mas = new int[0];
        ReportResponse report = purchaseService.getReport(adminCookie, "category", mas, "product", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(481, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_2_category_order_product() throws OnlineShopException {
        int[] mas = new int[]{childCategory.get(0).getId(), rootCategory.get(0).getId(),};
        ReportResponse report = purchaseService.getReport(adminCookie, "category", mas, "product", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(96, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_category_order_category() throws OnlineShopException {
        ReportResponse report = purchaseService.getReport(adminCookie, "category", null, "category", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(481, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_empty_category_order_category() throws OnlineShopException {
        int[] mas = new int[0];
        ReportResponse report = purchaseService.getReport(adminCookie, "category", mas, "category", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(481, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

    @Test
    public void report_test_with_2_category_order_category() throws OnlineShopException {
        int[] mas = new int[]{childCategory.get(0).getId(), rootCategory.get(0).getId()};
        ReportResponse report = purchaseService.getReport(adminCookie, "category", mas, "category", false, null, null);
//        report.getReportRows().forEach(System.out::println);
//        System.out.println("getTotalCount = " + report.getTotalCount());
//        System.out.println("getTotalPrise = " + report.getTotalPrise());
        assertFalse(report.getReportRows().isEmpty());
        assertEquals(96, report.getTotalCount());
        assertEquals(0, report.getTotalPrise());
    }

}
