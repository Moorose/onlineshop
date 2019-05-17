package net.thumbtack.onlineshop.exeption;

public class OnlineShopException extends Exception{

    private final OnlineShopErrorCode onlineShopErrorCode;
    private final String field;


    public OnlineShopException(String field, OnlineShopErrorCode onlineShopErrorCode) {
        this.field = field;
        this.onlineShopErrorCode = onlineShopErrorCode;
    }

    public String getField() {
        return field;
    }
    public OnlineShopErrorCode getOnlineShopErrorCode() {
        return onlineShopErrorCode;
    }

}
