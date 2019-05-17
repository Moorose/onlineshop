package net.thumbtack.onlineshop.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCategoryRequest {

    @NotNull(message = "Field could not be empty")
    private String name;

    @Min(value = 0, message = "Parent Id should be more than 0 or equals it")
    private int parentId;
}
