package net.thumbtack.onlineshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Setter
@Getter
public class Category {

    private int id;
    private String name;
    private Integer parentId;
    private List<Category> subCategories;

    public Category(int id, String name, Integer parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        subCategories = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "\tCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", subCategories=" + subCategories.size() +
                "}";
    }
}
