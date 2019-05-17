package net.thumbtack.onlineshop.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationErrorResponse {

    private List<OnlineShopExceptionResponse> responses;

    public ValidationErrorResponse() {
        responses = new ArrayList<>();
    }
}