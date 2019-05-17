package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.entity.Category;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Insert({"<script>",
            "INSERT INTO category (category_name, root_id) VALUES " +
                    "<if test='category.parentId == 0'> (#{category.name}, null) </if>",
            "<if test='category.parentId != 0'> (#{category.name}, #{category.parentId}) </if>",
            "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCategory(@Param("category") Category category);

    @Select("SELECT id, category_name, root_id  FROM category WHERE category.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "category_name"),
            @Result(property = "parentId", column = "root_id"),
            @Result(property = "subCategories", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.CategoryMapper.getByCategory", fetchType = FetchType.EAGER))})
    Category findCategoryById(@Param("id") int id);

    @Select("SELECT id, category_name, root_id FROM category WHERE root_id = #{category.id} ORDER BY category_name")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "category_name"),
            @Result(property = "parentId", column = "root_id"),
            @Result(property = "subCategories", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.CategoryMapper.getByCategory", fetchType = FetchType.EAGER))})
    List<Category> getByCategory(@Param("category") Category Category);


    @Select("SELECT id, category_name, root_id FROM category WHERE root_id IS NULL  ORDER BY category_name")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "category_name"),
            @Result(property = "parentId", column = "root_id"),
            @Result(property = "subCategories", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.onlineshop.database.mapper.CategoryMapper.getByCategory", fetchType = FetchType.EAGER))})
    List<Category> findAllCategory();

    @Update({"<script>",
            "UPDATE category SET " +
                    "category_name = #{category.name}," +
                    "root_id =<if test='category.parentId != 0'> #{category.parentId} </if> <if test='category.parentId == 0'> null </if>" +
                    "WHERE id = #{category.id}",
            "</script>"})
    void updateCategory(@Param("category") Category category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    void deleteCategoryById(@Param("id") int id);

    @Delete("DELETE FROM category")
    void deleteAllCategory();
}
