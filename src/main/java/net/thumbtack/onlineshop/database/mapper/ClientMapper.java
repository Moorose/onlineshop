package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.entity.Client;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClientMapper {

    @Insert("INSERT INTO client (id, phone, email, address)" +
            " VALUES (#{client.id}, #{client.phone}, #{client.email}, #{client.address})")
    void insertClient(@Param("client") Client client);

    @Insert("INSERT INTO deposit (id, money, version) VALUES (#{client.id}, #{client.deposit.money}, 1)")
    void insertClientDeposit(@Param("client") Client client);

    @Update("UPDATE deposit SET money = money + #{money}, version = version + 1 WHERE id = #{client.id}")
    int addMoneyToDeposit(@Param("client") Client client, @Param("money") int money);

    @Select("SELECT user.id, firstname, lastname, patronymic, login, password, phone, email, address, money, version " +
            "FROM user INNER JOIN client ON user.id = client.id " +
            "LEFT JOIN deposit ON user.id = deposit.id " +
            "WHERE user.id = #{id}")
    Client findClientById(int id);

    @Select("SELECT user.id, firstname, lastname, patronymic, login, password, phone, email, address, money, version " +
            "FROM user INNER JOIN client ON user.id = client.id LEFT JOIN deposit ON user.id = deposit.id")
    List<Client> findAllClient();

    @Update("UPDATE client SET " +
            "phone = #{client.phone}, " +
            "email = #{client.email}, " +
            "address = #{client.address} " +
            "WHERE id = #{client.id}")
    void updateClient(@Param("client")Client client);

}
