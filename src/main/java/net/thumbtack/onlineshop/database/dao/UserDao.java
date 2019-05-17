package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.entity.User;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

public interface UserDao {

    void deleteServerSession(String javaSessionId);

    User findUserByLogin(String login) throws OnlineShopException;

    String openServerSession(User user, String javaSessionId);

    String findTokenByUserId(int user);

    User findUserByToken(String javaSessionId) throws OnlineShopException;
}
