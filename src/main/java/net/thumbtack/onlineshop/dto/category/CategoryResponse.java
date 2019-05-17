package net.thumbtack.onlineshop.dto.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private int id;
    private String name;
    private int parentId;
    private String parentName;

    public CategoryResponse(int id, String name, int parentId, String parentName) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.parentName = parentName;
    }

    public CategoryResponse() {
    }
}
