package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.entity.BasketItem;
import net.thumbtack.onlineshop.entity.Client;
import net.thumbtack.onlineshop.entity.Product;
import net.thumbtack.onlineshop.entity.Purchase;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface PurchaseMapper {

    @Update("UPDATE deposit SET " +
            "money = money - #{totalPrice}, " +
            " version = version + 1" +
            " WHERE id = #{client.id} AND version = #{client.deposit.version}")
    int updateDepositBeforePurchase(@Param("client") Client client, @Param("totalPrice") int totalPrice);

    @Update("UPDATE product SET " +
            "product_count = product_count - #{count}, " +
            "version = version + 1 " +
            "WHERE id = #{product.id} AND version = #{product.version}")
    int updateProductCountAfterPurchase(@Param("product") Product product, @Param("count") int count);

    @Insert("INSERT INTO purchase (client_id, product_id, purchase_name, purchase_price, product_count) " +
            "VALUES (#{client.id}, #{product.id}, #{product.name}, #{product.price}, #{count})")
    @Options(useGeneratedKeys = true)
    int insertPurchase(@Param("client") Client client, @Param("product") Product product, @Param("count") int count);

    @Insert("INSERT INTO basket_item (client_id, product_id, count_product) VALUES (#{client.id}, #{product.id}, #{count})")
    void addProductToBasket(@Param("client") Client client, @Param("product") Product product, @Param("count") int count);

    @Select("SELECT product_id, count_product FROM basket_item WHERE client_id = #{id}")
    @Results({
            @Result(property = "count", column = "count_product"),
            @Result(property = "product", column = "product_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.findProductById", fetchType = FetchType.EAGER))})
    List<BasketItem> getBasketByClientId(@Param("id") int id);

    @Update("UPDATE basket_item SET " +
            "count_product = #{count} " +
            "WHERE client_id = #{client.id} AND product_id = #{product.id}")
    void changeProductToBasket(@Param("client") Client client, @Param("product") Product product, @Param("count") int count);

    @Delete("DELETE FROM basket_item WHERE client_id = #{client.id} AND product_id = #{id}")
    int deleteProductFromBasket(@Param("client") Client client, @Param("id") int id);

    @Delete("DELETE FROM purchase")
    void deleteAllPurchase();

    @Delete("DELETE FROM basket_item")
    void deleteAllBasketItem();

    @Select({"<script>",
            "SELECT client_id, product_id, purchase_name, purchase_price, product_count ",
            " FROM purchase ",
            " ORDER BY purchase_name ",
            "<if test='offset != null and limit != null'> LIMIT #{offset},#{limit} </if>",
            "<if test='offset == null and limit != null'> LIMIT #{limit} </if>",
            "<if test='offset != null and limit == null'> LIMIT #{offset},100 </if>",
            "</script>"})
    @Results({
            @Result(property = "name", column = "purchase_name"),
            @Result(property = "price", column = "purchase_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "client", column = "client_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ClientMapper.findClientById")),
            @Result(property = "product", column = "product_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.findProductById"))
    })
    List<Purchase> findPurchaseSortedByProduct(@Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select({"<script>",
            " SELECT client_id, purchase.product_id, purchase_name, purchase_price, product_count, category_id, category_name ",
            " FROM purchase",
            " LEFT JOIN product_category AS pc ON purchase.product_id = pc.product_id ",
            " LEFT JOIN category ON category_id = category.id ",
            " ORDER BY category_name ",
            "<if test='offset != null and limit != null'> LIMIT #{offset},#{limit} </if>",
            "<if test='offset == null and limit != null'> LIMIT #{limit} </if>",
            "<if test='offset != null and limit == null'> LIMIT #{offset},100 </if>",
            "</script>"})
    @Results({
            @Result(property = "name", column = "purchase_name"),
            @Result(property = "price", column = "purchase_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "client", column = "client_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ClientMapper.findClientById")),
            @Result(property = "product", column = "product_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.PurchaseMapper.findProductById")),
            @Result(property = "categories", column = "category_id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.getCategoryById"))
    })
    List<Purchase> findPurchaseSortedByCategory(@Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select("SELECT id, product_name, product_count, product_price, version, deleted FROM product WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "product_name"),
            @Result(property = "price", column = "product_price"),
            @Result(property = "count", column = "product_count")})
    Product findProductById(@Param("id") int id);

    @Select({"<script>",
            "SELECT client_id, product_id, purchase_name, purchase_price, product_count ",
            " FROM purchase ",
            "<choose>",
            "<when test='mas == null'>",
            "</when>",
            "<when test='mas.length != 0'>",    // NPE
            "WHERE client_id IN " +
                    "(<foreach item='item' collection='mas' separator=','> #{item} </foreach>)",
            "</when>",
            "</choose>",
            " ORDER BY client_id, purchase_name ",
            "<if test='offset != null and limit != null'> LIMIT #{offset},#{limit} </if>",
            "<if test='offset == null and limit != null'> LIMIT #{limit} </if>",
            "<if test='offset != null and limit == null'> LIMIT #{offset},100 </if>",
            "</script>"})
    @Results({
            @Result(property = "name", column = "purchase_name"),
            @Result(property = "price", column = "purchase_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "client", column = "client_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ClientMapper.findClientById")),
            @Result(property = "product", column = "product_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.findProductById"))
    })
    List<Purchase> findPurchaseByClientsSortedByProduct(@Param("mas") int[] masId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select({"<script>",
            "SELECT client_id, purchase.product_id, purchase_name, purchase_price, product_count, category_id, category_name ",
            " FROM purchase ",
            " LEFT JOIN product_category ON purchase.product_id = product_category.product_id ",
            " LEFT JOIN category ON category_id = category.id ",
            "<choose>",
            "<when test='mas == null'>",
            "</when>",
            "<when test='mas.length != 0'>",    // NPE
            "WHERE client_id IN " +
                    "(<foreach item='item' collection='mas' separator=','> #{item} </foreach>)",
            "</when>",
            "</choose>",
            " ORDER BY client_id, category_name",
            "<if test='offset != null and limit != null'> LIMIT #{offset},#{limit} </if>",
            "<if test='offset == null and limit != null'> LIMIT #{limit} </if>",
            "<if test='offset != null and limit == null'> LIMIT #{offset},100 </if>",
            "</script>"})
    @Results({
            @Result(property = "name", column = "purchase_name"),
            @Result(property = "price", column = "purchase_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "client", column = "client_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ClientMapper.findClientById")),
            @Result(property = "product", column = "product_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.PurchaseMapper.findProductById")),
            @Result(property = "categories", column = "category_id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.getCategoryById"))
    })
    List<Purchase> findPurchaseByClientsSortedByCategory(@Param("mas") int[] masId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select({"<script>",
            "SELECT client_id, product_id, purchase_name, purchase_price, product_count ",
            " FROM purchase ",
            "<choose>",
            "<when test='mas == null'>",
            "</when>",
            "<when test='mas.length != 0'>",
            "WHERE product_id IN " +
                    "(<foreach item='item' collection='mas' separator=','> #{item} </foreach>)",
            "</when>",
            "</choose>",
            " ORDER BY purchase_name ",
            "<if test='offset != null and limit != null'> LIMIT #{offset},#{limit} </if>",
            "<if test='offset == null and limit != null'> LIMIT #{limit} </if>",
            "<if test='offset != null and limit == null'> LIMIT #{offset},100 </if>",
            "</script>"})
    @Results({
            @Result(property = "name", column = "purchase_name"),
            @Result(property = "price", column = "purchase_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "client", column = "client_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ClientMapper.findClientById")),
            @Result(property = "product", column = "product_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.findProductById"))
    })
    List<Purchase> findPurchaseByProductsSortedByProduct(@Param("mas") int[] masId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select({"<script>",
            "SELECT client_id, purchase.product_id, purchase_name, purchase_price, product_count, category_id, category_name ",
            " FROM purchase ",
            " LEFT JOIN product_category ON purchase.product_id = product_category.product_id ",
            " LEFT JOIN category ON category_id = category.id ",
            "<choose>",
            "<when test='mas == null'>",
            "</when>",
            "<when test='mas.length != 0'>",
            "WHERE purchase.product_id IN " +
                    "(<foreach item='item' collection='mas' separator=','> #{item} </foreach>)",
            "</when>",
            "</choose>",
            " ORDER BY category_name, purchase_name ",
            "<if test='offset != null and limit != null'> LIMIT #{offset},#{limit} </if>",
            "<if test='offset == null and limit != null'> LIMIT #{limit} </if>",
            "<if test='offset != null and limit == null'> LIMIT #{offset},100 </if>",
            "</script>"})
    @Results({
            @Result(property = "name", column = "purchase_name"),
            @Result(property = "price", column = "purchase_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "client", column = "client_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ClientMapper.findClientById")),
            @Result(property = "product", column = "product_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.PurchaseMapper.findProductById")),
            @Result(property = "categories", column = "category_id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.getCategoryById"))
    })
    List<Purchase> findPurchaseByProductsSortedByCategory(@Param("mas") int[] masId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select({"<script>",
            " SELECT category.id, category_name, client_id, purchase.product_id, purchase_name, purchase_price, product_count",
            " FROM category ",
            " LEFT JOIN product_category AS pc ON category.id = pc.category_id",
            "left join purchase on purchase.product_id = pc.product_id",
            "<choose>",
            "<when test='mas == null'>",
            "</when>",
            "<when test='mas.length != 0'>",
            "WHERE category.id IN " +
                    "(<foreach item='item' collection='mas' separator=','> #{item} </foreach>)",
            "</when>",
            "</choose>",
            "<if test=\"order == 'product'\"> ORDER BY purchase_name, category_name </if>",
            "<if test=\"order == 'category'\"> ORDER BY category_name, purchase_name </if>",
            "<if test='offset != null and limit != null'> LIMIT #{offset},#{limit} </if>",
            "<if test='offset == null and limit != null'> LIMIT #{limit} </if>",
            "<if test='offset != null and limit == null'> LIMIT #{offset},100 </if>",
            "</script>"})
    @Results({
            @Result(property = "name", column = "purchase_name"),
            @Result(property = "price", column = "purchase_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "client", column = "client_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.ClientMapper.findClientById")),
            @Result(property = "product", column = "product_id",
                    one = @One(select = "net.thumbtack.onlineshop.database.mapper.PurchaseMapper.findProductById")),
            @Result(property = "categories", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.getCategoryById"))
    })
    List<Purchase> findPurchaseByCategory(@Param("mas") int[] masId, @Param("order") String order, @Param("offset") Integer offset, @Param("limit") Integer limit);

}
