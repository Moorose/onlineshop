package net.thumbtack.onlineshop.dto.edit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditCategoryRequest {

    private String name;

    @Min(value = 0, message = "Parent Id should be more than 0 or equals it")
    private int parentId;
}
