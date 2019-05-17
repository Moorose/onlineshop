package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.EmptyResponse;
import net.thumbtack.onlineshop.dto.edit.EditProductRequest;
import net.thumbtack.onlineshop.dto.product.AddProductRequest;
import net.thumbtack.onlineshop.dto.product.ProductWithCategoryNameResponse;
import net.thumbtack.onlineshop.dto.product.SimpleProductResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/products")
    public ResponseEntity addProduct(@CookieValue(COOKIE_NAME) String javaSessionId, @Valid @RequestBody AddProductRequest request) throws OnlineShopException {
        SimpleProductResponse simpleProductResponse = productService.addProduct(javaSessionId, request);
        return ResponseEntity.ok().body(simpleProductResponse);
    }

    @PutMapping("/products/{product_number}")
    public ResponseEntity putProduct(@CookieValue(COOKIE_NAME) String javaSessionId, @PathVariable("product_number") int number, @Valid @RequestBody EditProductRequest request) throws OnlineShopException {
        SimpleProductResponse simpleProductResponse = productService.editProduct(javaSessionId, request, number);
        return ResponseEntity.ok().body(simpleProductResponse);
    }

    @DeleteMapping("/products/{product_number}")
    public ResponseEntity deleteProduct(@CookieValue(COOKIE_NAME) String javaSessionId, @PathVariable("product_number") int number) throws OnlineShopException {
        productService.deleteProductById(javaSessionId, number);
        return ResponseEntity.ok().body(new EmptyResponse());
    }

    @GetMapping("/products/{product_number}")
    public ResponseEntity getProduct(@CookieValue(COOKIE_NAME) String javaSessionId, @PathVariable("product_number") int number) throws OnlineShopException {
        ProductWithCategoryNameResponse productWithCategoryNameResponse = productService.getProductById(javaSessionId, number);
        return ResponseEntity.ok().body(productWithCategoryNameResponse);
    }

    @GetMapping("/products")
    public ResponseEntity getProductWithParam(@CookieValue(COOKIE_NAME) String javaSessionId,
                                              @RequestParam(value = "category", required = false) int[] category,
                                              @RequestParam(value = "order", required = false, defaultValue = "product") String order) throws OnlineShopException {
        List<ProductWithCategoryNameResponse> responseList = productService.getProductByParam(javaSessionId, category, order);
        return ResponseEntity.ok().body(responseList);
    }
}
