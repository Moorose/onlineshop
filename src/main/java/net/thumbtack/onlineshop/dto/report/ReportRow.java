package net.thumbtack.onlineshop.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRow {

    //    Client
    private int userId;

    //   Product
    private int productId;
    private String currentName;
    private int currentPrice;
    private boolean deleted;

    //   List<Category> categories;
    //         id, name
    private Map<Integer, String> category;

    //         purchase
    private String name;
    private int price;
    private int count;

}
