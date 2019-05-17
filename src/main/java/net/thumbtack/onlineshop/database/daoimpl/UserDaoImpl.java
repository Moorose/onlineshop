package net.thumbtack.onlineshop.database.daoimpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.database.dao.UserDao;
import net.thumbtack.onlineshop.database.support.DaoImplBase;
import net.thumbtack.onlineshop.entity.User;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static net.thumbtack.onlineshop.OnlineShopServer.COOKIE_NAME;

@Repository
@Slf4j
public class UserDaoImpl extends DaoImplBase implements UserDao {

    @Autowired
    public UserDaoImpl(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate);
    }

    @Override
    @Transactional
    public void deleteServerSession(String token) {
        log.debug("UserDaoImpl delete session for user with token: {}", token);
        getUserMapper().deleteSession(token);
    }

    @Override
    @Transactional
    public String openServerSession(User user, String token) {
        log.debug("UserDaoImpl open session for user: {}", user);
        getUserMapper().openSession(user.getId(), token);
        return token;
    }

    @Override
    @Transactional
    public String findTokenByUserId(int id) {
        log.debug("UserDaoImpl find token for user with id: {}", id);
        return getUserMapper().findTokenByUserId(id);

    }

    @Override
    @Transactional(rollbackFor = OnlineShopException.class)
    public User findUserByToken(String javaSessionId) throws OnlineShopException {
        log.debug("UserDaoImpl find user by token: {}", javaSessionId);
        User user = getUserMapper().findByToken(javaSessionId);
        if (user == null) {
            throw new OnlineShopException(COOKIE_NAME, OnlineShopErrorCode.UNKNOWN_TOKEN);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = OnlineShopException.class)
    public User findUserByLogin(String login) throws OnlineShopException {
        log.debug("UserDaoImpl find user by login: {}", login);
        User userByLogin = getUserMapper().findUserByLogin(login);
        if (userByLogin == null) {
            throw new OnlineShopException("login", OnlineShopErrorCode.UNKNOWN_LOGIN);
        }
        return userByLogin;
    }
}