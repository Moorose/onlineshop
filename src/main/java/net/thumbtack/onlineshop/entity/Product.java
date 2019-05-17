package net.thumbtack.onlineshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private int id;
    private String name;
    private int price;

    private int count;
    private List<Category> categories;
    private int version;
    private boolean deleted;

    public Product(int id, String name, int price, int count) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.count = count;
        categories = new ArrayList<>();
    }

    public Product(int id, String name, int price) { // Base param
        this(id, name, price, 0);
    }

    public void addCategories(Category category) throws OnlineShopException {
        if (categories == null) {
            throw new OnlineShopException("category", OnlineShopErrorCode.ERROR_ADD_CATEGORY);
        }
        categories.add(category);
    }

    public boolean equalsWithBaseParam(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return getId() == product.getId() &&
                getPrice() == product.getPrice() &&
                Objects.equals(getName(), product.getName());
    }
}
