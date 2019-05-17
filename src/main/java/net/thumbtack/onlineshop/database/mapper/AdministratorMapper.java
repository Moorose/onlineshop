package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.entity.Admin;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdministratorMapper {

    @Insert("INSERT INTO admin (id, position) VALUES (#{admin.id}, #{admin.position})")
    void insertAdmin(@Param("admin") Admin admin);

    @Select("SELECT user.id,  firstname, lastname, patronymic, login, password, position " +
            "FROM user INNER JOIN admin ON user.id = admin.id WHERE user.id = #{id}")
    Admin findAdminById(int id);

    @Update("UPDATE admin SET " +
            "position = #{admin.position} " +
            "WHERE id = #{admin.id}")
    void updateUser(@Param("admin") Admin admin);
}
