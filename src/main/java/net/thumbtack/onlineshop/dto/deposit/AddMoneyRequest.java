package net.thumbtack.onlineshop.dto.deposit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMoneyRequest {

    @Min(value = 1, message = "Price could not be <=0")
    private int deposit;
}
