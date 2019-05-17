package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.entity.Category;
import net.thumbtack.onlineshop.entity.Product;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO product (product_name, product_price, product_count, version, deleted)" +
            " VALUES (#{product.name}, #{product.price}, #{product.count}, 1, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertProduct(@Param("product") Product product);

    @Insert({"<script>",
            "INSERT INTO product_category (product_id, category_id) VALUES ",
            "<foreach item='item' collection='list' separator=','> ",
            " (#{product.id}, #{item.id}) ",
            "</foreach>",
            "</script>"})
    void insertProductToCategory(@Param("product") Product product, @Param("list") List<Category> categoryList);

    @Update("UPDATE product SET " +
            "product_name = #{product.name}, " +
            "product_price = #{product.price}, " +
            "product_count = #{product.count}, " +
            "version = version + 1 " +
            "WHERE id = #{product.id} AND version = #{product.version}")
    int updateProduct(@Param("product") Product product);

    //    @Select("SELECT id, product_name, product_count, product_price, version, deleted FROM product WHERE id = #{id} AND deleted = 0")
    @Select("SELECT id, product_name, product_count, product_price, version, deleted FROM product WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "product_name"),
            @Result(property = "price", column = "product_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "categories", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.getCategoryByProductId", fetchType = FetchType.EAGER))})
    Product findProductById(@Param("id") int id);

    @Select("SELECT id, category_name, root_id  FROM category WHERE category.id IN " +
            "(SELECT category_id  FROM product_category WHERE product_id = #{product.id})")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "category_name"),
            @Result(property = "parentId", column = "root_id"),
            @Result(property = "subCategories", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.CategoryMapper.getByCategory", fetchType = FetchType.EAGER))})
    List<Category> getCategoryByProductId(@Param("product") Product product);


    @Select("SELECT id, product_name, product_count, product_price, version, deleted FROM product ORDER BY product_name")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "product_name"),
            @Result(property = "price", column = "product_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "categories", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.getCategoryByProductId", fetchType = FetchType.EAGER))})
    List<Product> findAllProduct();

    //    @Delete("DELETE FROM product WHERE id = #{id}")
    @Update("UPDATE product SET " +
            "product_name = CONCAT(product_name,' (IsDELETED)'), " +
            "deleted = 1, " +
            "version = version + 1 " +
            "WHERE id = #{id}")
    void deleteProductById(@Param("id") int id);

    @Delete("DELETE FROM product_category WHERE product_id = #{product.id}")
    void deleteCategoriesFromProduct(@Param("product") Product product);

    @Delete("DELETE FROM product")
    void deleteAllProduct();

    @Select({"<script>",
            "SELECT id, product_name, product_count, product_price, version, deleted FROM product",
            "<choose>",
            "<when test='category.length == 0'>",
            "WHERE id NOT IN ( SELECT product_id FROM product_category ) AND deleted = 0",
            "</when>",
            "<when test='category.length != 0'>",
            "WHERE deleted = 0 AND id IN ( SELECT product_id FROM product_category WHERE  category_id IN " +
                    "(<foreach item='item' collection='category' separator=','> #{item} </foreach>))",
            "</when>",
            "</choose>",
            " ORDER BY product_name",
            "</script>"})
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "product_name"),
            @Result(property = "price", column = "product_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "categories", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.getCategoryByProductId", fetchType = FetchType.EAGER))})
    List<Product> findProductByCategorySortedByProductName(@Param("category") int[] category);

    @Select({"<script>",
            "SELECT * FROM (",
            "SELECT product.id, product_name, product_count, product_price, version, deleted, category_id, category_name ",
            "FROM product ",
            "LEFT JOIN product_category ON product.id = product_id ",
            "LEFT JOIN category ON category_id = category.id ",
            "ORDER BY category_name, product_name ",
            ") AS blank",
            "<choose>",
            "<when test='category == null'>",
            "WHERE deleted = 0",
            "</when>",
            "<when test='category.length  == 0'>",
            "WHERE deleted = 0 AND blank.category_id NOT IN ( SELECT product_id FROM product_category )",
            "</when>",
            "<when test='category.length != 0'>",
            "WHERE deleted = 0 AND blank.category_id IN " +
                    "(<foreach item='item' collection='category' separator=','> #{item} </foreach>)",
            "</when>",
            "</choose>",
            "</script>"})
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "product_name"),
            @Result(property = "price", column = "product_price"),
            @Result(property = "count", column = "product_count"),
            @Result(property = "categories", column = "category_id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.ProductMapper.getCategoryById", fetchType = FetchType.EAGER))})
    List<Product> findProductByCategorySortedByCategory(@Param("category") int[] category);

    @Select("SELECT id, category_name, root_id  FROM category WHERE category.id = #{category_id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "category_name"),
            @Result(property = "parentId", column = "root_id"),
            @Result(property = "subCategories", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.CategoryMapper.getByCategory", fetchType = FetchType.EAGER))})
    List<Category> getCategoryById(@Param("category_id") Integer category_id);

}
