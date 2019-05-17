package net.thumbtack.onlineshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {

    private Client client;
    private Product product;
    private String name;
    private int price;
    private int count;

    private List<Category> categories;

}
