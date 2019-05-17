package net.thumbtack.onlineshop.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class BuyProductRequest {

    @Min(value = 1, message = "Id could not be 0")
    private int id;

    @NotNull(message = "Field could not be empty")
    private String name;

    @Min(value = 1, message = "Price could not be <=0")
    private int price;

    //    @Min(value = 0, message = "Count should be more than 0 or equals it")
    private Integer count;

    public Integer getCountWithSafe() {
        if (count != null) {
            return count;
        }
        return 1;
    }

    public Integer getCountWithoutNull() {
        if (count != null) {
            return count;
        }
        return 0;
    }

    public BuyProductRequest(int id, String name, int price, Integer count) {
        this(id, name, price);
        this.count = count;
    }

    public BuyProductRequest(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

}
