package net.thumbtack.onlineshop.dto.edit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditProductRequest {

    private String name;

    @Min(value = 1, message = "Price should be more than 0")
    private Integer price;

    @Min(value = 0, message = "Count should be more than 0 or equals it")
    private Integer count;

    private int[] categories;
}