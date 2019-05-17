package net.thumbtack.onlineshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketItem {
    private Product product;
    private int count;

    public boolean validProduct(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasketItem)) return false;
        BasketItem itemFromRequest = (BasketItem) o;
        return product != null &&  //  product isn`t deleted
                !product.isDeleted() && // product isn`t available
                product.equalsWithBaseParam(itemFromRequest.getProduct()); // product have same params
    }
}
