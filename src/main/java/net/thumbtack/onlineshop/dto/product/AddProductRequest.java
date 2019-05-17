package net.thumbtack.onlineshop.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductRequest {

    @NotNull(message = "Field could not be empty")
    private String name;

    @Min(value = 1, message = "Price could not be <=0")
    private int price;

    @Min(value = 0, message = "Count should be more than 0 or equals it")
    private int count;

    private int[] categories;

    public AddProductRequest(String name, int price) {
        this.name = name;
        this.price = price;
    }
}