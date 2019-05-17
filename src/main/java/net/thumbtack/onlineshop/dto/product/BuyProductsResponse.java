package net.thumbtack.onlineshop.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyProductsResponse {

    List<BuyProductResponse> bought;
    List<BuyProductResponse> remaining;

}
