package net.thumbtack.onlineshop.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ProductWithCategoryNameResponse {

        private int id;
        private String name;
        private int price;
        private int count;
        private String[] categories;

}
