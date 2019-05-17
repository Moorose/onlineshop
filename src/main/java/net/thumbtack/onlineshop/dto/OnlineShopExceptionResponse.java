package net.thumbtack.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineShopExceptionResponse {

    private OnlineShopErrorCode errorCode;
    private String field;
    private String message;

    public OnlineShopExceptionResponse(OnlineShopException exc) {
        errorCode = exc.getOnlineShopErrorCode();
        field = exc.getField();
        message = errorCode.getErrorString();
    }
}
