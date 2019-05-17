package net.thumbtack.onlineshop.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductRequest {

    @Min(value = 1, message = "Id could not be 0")
    private int id;

    private String name;

    @Min(value = 1, message = "Price could not be 0")
    private int price;

    @Min(value = 1, message = "Count could not be 0")
    private int count;

}
