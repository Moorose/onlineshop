package net.thumbtack.onlineshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Basket {

    List<BasketItem> basketItems;

}


