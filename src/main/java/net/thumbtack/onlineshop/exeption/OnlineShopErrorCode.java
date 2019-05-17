package net.thumbtack.onlineshop.exeption;

public enum OnlineShopErrorCode {
    ADMIN_ERROR_AUTHENTICATION("The cookie is not relevant"),
    CLIENT_ERROR_AUTHENTICATION("The cookie is not relevant"),
    DATABASE_DUPLICATE_KEY("This value already exists"),
    DATABASE_FIND_CATEGORY_BY_ID_ERROR("We can`t find category"),
    DATABASE_FIND_PRODUCT_BY_ID_ERROR("We can`t find product"),
    DATABASE_INSERT_PURCHASE("Purchase data is irrelevant"),
    DATABASE_UNAVAILABLE("Could not open connection to the database"),
    DATABASE_UPDATE_BASKET_ERROR("The basket have error"),
    DATABASE_UPDATE_DEPOSIT_BEFORE_PURCHASE("Purchase data is irrelevant"),
    DATABASE_UPDATE_DEPOSIT_ERROR("The deposit is not relevant"),
    DATABASE_UPDATE_PRODUCT("The product have error"),
    DATABASE_UPDATE_PRODUCT_COUNT_FOR_PURCHASE("Purchase data is irrelevant"),
    EMPTY_REQUEST("Request's body is empty"),
    ERROR_ADD_CATEGORY("The category have error"),
    ERROR_CHANGE_CATEGORY_TYPE("Category can't change own type"),
    IDENTICAL_PASSWORD("The old and new passwords are identical"),
    INTERNAL_SERVER_ERROR("Something went wrong"),
    NOT_ENOUGH_MONEY_ON_DEPOSIT("Not enough money in deposit"),
    NOT_ENOUGH_PRODUCT_IN_STORAGE("Not enough product in warehouse"),
    PARAMS_FOR_PRODUCT_ARE_WRONG("This parameters is not correct"),
    PRODUCT_NOT_FOUND("The product is not exist"),
    REQUEST_NOT_READABLE("We can't read the request"),
    THIS_PRODUCT_IS_NOT_AVAILABLE("The product have been deleted"),
    UNEXPECTED_SERVER_ERROR("Don't worry, we'll think of something"),
    UNKNOWN_LOGIN("The login is wrong"),
    UNKNOWN_TOKEN("The cookie is not accepted"),
    UNKNOWN_USER("This user is not exist"),
    VALIDATION_ERROR("The field have error"),
    WRONG_CATEGORY_ID("This id is not exist"),
    WRONG_PARAM("The parameter is wrong"),
    WRONG_PASSWORD("The password is wrong"),
    WRONG_REQUEST("The request is wrong"),
    WRONG_URL("The url is wrong");

    private String errorString;

    private OnlineShopErrorCode(String errorString){
        this.errorString = errorString;
    }

    public String getErrorString() {
        return errorString;
    }


}
