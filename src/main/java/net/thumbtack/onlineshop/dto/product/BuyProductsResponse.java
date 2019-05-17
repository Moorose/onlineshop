package net.thumbtack.onlineshop.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BuyProductsResponse {

    List<BuyProductResponse> bought;
    List<BuyProductResponse> remaining;

}
