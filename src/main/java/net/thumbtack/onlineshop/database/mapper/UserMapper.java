package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user (firstname, lastname, patronymic, login, password)" +
            " VALUES (#{user.firstName}, #{user.lastName}, #{user.patronymic}, #{user.login}, #{user.password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(@Param("user") User user);

    @Update("UPDATE user SET " +
            "firstname = #{user.firstName}, " +
            "lastname = #{user.lastName}, " +
            "patronymic = #{user.patronymic}, " +
            "password = #{user.password} " +
            "WHERE id = #{user.id}")
    void updateUser(@Param("user") User user);

    @Select("SELECT id, firstname, lastname, patronymic, login, password FROM user WHERE user.login = #{login}")
    User findUserByLogin(@Param("login") String login);

    @Insert("INSERT INTO server_session (token, id) VALUES (#{token}, #{id})")
    void openSession(@Param("id") int id, @Param("token") String uuid);

    @Delete("DELETE FROM server_session WHERE token=#{token}")
    void deleteSession(@Param("token") String token);

    @Select("SELECT user.id, firstname, lastname, patronymic, login, password " +
            "FROM server_session INNER JOIN user ON server_session.id = user.id " +
            "WHERE token = #{token}")
    User findByToken(@Param("token") String token);

    @Select("SELECT token FROM server_session WHERE id = #{id}")
    String findTokenByUserId(@Param("id") int id);

    @Delete("DELETE FROM user")
    void deleteAllUser();
}
