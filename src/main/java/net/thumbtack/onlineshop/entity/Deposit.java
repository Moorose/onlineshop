package net.thumbtack.onlineshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Deposit {

    private int money;
    private int version;

}
