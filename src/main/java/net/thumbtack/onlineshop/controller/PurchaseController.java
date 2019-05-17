package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.EmptyResponse;
import net.thumbtack.onlineshop.dto.deposit.AddMoneyRequest;
import net.thumbtack.onlineshop.dto.product.BuyProductRequest;
import net.thumbtack.onlineshop.dto.product.BuyProductResponse;
import net.thumbtack.onlineshop.dto.product.BuyProductsResponse;
import net.thumbtack.onlineshop.dto.product.ChangeProductRequest;
import net.thumbtack.onlineshop.dto.report.ReportResponse;
import net.thumbtack.onlineshop.dto.user.ClientRegistrationResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;

@RestController
@RequestMapping("/api")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PutMapping("/deposits")
    public ResponseEntity addMoney(@CookieValue(COOKIE_NAME) String javaSessionId, @Valid @RequestBody AddMoneyRequest request) throws OnlineShopException {
        ClientRegistrationResponse response = purchaseService.addMoney(javaSessionId, request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/deposits")
    public ResponseEntity getDeposit(@CookieValue(COOKIE_NAME) String javaSessionId) throws OnlineShopException {
        ClientRegistrationResponse response = purchaseService.getDeposit(javaSessionId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/baskets")
    public ResponseEntity addProductToBasket(@CookieValue(COOKIE_NAME) String javaSessionId, @Valid @RequestBody BuyProductRequest request) throws OnlineShopException {
        List<BuyProductResponse> responses = purchaseService.addProductToBasket(javaSessionId, request);
        return ResponseEntity.ok().body(responses);
    }

    @DeleteMapping("/baskets/{id}")
    public ResponseEntity deleteProductFromBasket(@CookieValue(COOKIE_NAME) String javaSessionId, @PathVariable("id") int id) throws OnlineShopException {
        purchaseService.deleteProductFromBasket(javaSessionId, id);
        return ResponseEntity.ok().body(new EmptyResponse());
    }


    @PutMapping("/baskets")
    public ResponseEntity changeProductCount(@CookieValue(COOKIE_NAME) String javaSessionId, @Valid @RequestBody ChangeProductRequest request) throws OnlineShopException {
        List<BuyProductResponse> responses = purchaseService.changeProductCount(javaSessionId, request);
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/baskets")
    public ResponseEntity getProductsFromBasket(@CookieValue(COOKIE_NAME) String javaSessionId) throws OnlineShopException {
        List<BuyProductResponse> responses = purchaseService.getProductsFromBasket(javaSessionId);
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping("/purchases")
    public ResponseEntity buyProduct(@CookieValue(COOKIE_NAME) String javaSessionId, @Valid @RequestBody BuyProductRequest request) throws OnlineShopException {
        BuyProductResponse response = purchaseService.buyProduct(javaSessionId, request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/purchases/baskets")
    public ResponseEntity buyProductsFromBasket(@CookieValue(COOKIE_NAME) String javaSessionId, @Valid @RequestBody List<BuyProductRequest> requests) throws OnlineShopException {
        BuyProductsResponse responses = purchaseService.buyProduct(javaSessionId, requests);
        return ResponseEntity.ok().body(responses);
    }

    //Необходимо предусмотреть как выдачу всей информации,
// так и выдачу информации по отдельным категориям или списку категорий (в соответствии с тем, к каким категориям принадлежат товары сейчас, а не в момент покупки),
// по товару или списку товаров,
// по клиентам и т.д.
// Желательно предусмотреть критерии упорядочения результирующей выборки. Ответ должен также содержать итоговые значения по выборке. Например,
// если возвращается список покупок некоторого клиента, в ответ надо включить их суммарную стоимость.  Также необходимо предусмотреть вариант,
// когда выдаются только итоговые значения, без подробностей - в тех случаях, когда это имеет смысл.
// Ввиду того, что данный запрос может возвращать очень много данных, следует предусмотреть пагинацию результатов,
// введя параметры запроса “offset” (номер строки результата, с которой начать выдачу) и “limit” (количество строк).
// Итоговые значения при этом приводятся для возвращаемой выборки, а не для всего списка.
    @GetMapping("/purchases")
    public ResponseEntity getReport(@CookieValue(COOKIE_NAME) String javaSessionId,
                                    @RequestParam(value = "type", required = false, defaultValue = "undefined") String type,
                                    @RequestParam(value = "id", required = false) int[] masId,
                                    @RequestParam(value = "order", required = false, defaultValue = "product") String order,
                                    @RequestParam(value = "onlyTotal", required = false, defaultValue = "false") boolean onlyTotal,
                                    @RequestParam(value = "offset", required = false) Integer offset,
                                    @RequestParam(value = "limit", required = false) Integer limit) throws OnlineShopException {
        ReportResponse responses = purchaseService.getReport(javaSessionId, type, masId, order, onlyTotal, offset, limit);
        return ResponseEntity.ok().body(responses);
    }
}
