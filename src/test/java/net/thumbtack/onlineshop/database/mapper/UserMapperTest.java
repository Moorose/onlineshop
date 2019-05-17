package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest

public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private CommonClearDatabaseNode clearDatabaseNode;

    @Before
    public void clear() {
        clearDatabaseNode.clearDatabase();
    }

    @Test
    public void testInsert() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new User(0, "user" + i, "last_name" + i, null, "login" + i, "password"));
        }
        for (User user : users) {
            userMapper.insertUser(user);
            assertTrue(user.getId() != 0);
        }
        for (User user : users) {
            System.out.println(user.toString());
        }
    }

    @Test
    public void updateUser() {
        User user = new User(0, "userUpdate", "last_nameUpdate", null, "loginUpdate", "passwordUpdate");
        userMapper.insertUser(user);
        assertTrue(user.getId() != 0);
        user.setPassword("password_password");
        userMapper.updateUser(user);
        User userUpdate = userMapper.findUserByLogin(user.getLogin());
        assertEquals(user, userUpdate);

    }

    @Test
    public void openAndDeleteSession() {
        User user = new User(0, "userSession", "last_nameSession", null, "loginSession", "passwordSession");
        userMapper.insertUser(user);
        assertTrue(user.getId() != 0);
        String uuid = UUID.randomUUID().toString();
        userMapper.openSession(user.getId(), uuid);

        String token = userMapper.findTokenByUserId(user.getId());
        assertEquals(uuid, token);

        User userFromDB = userMapper.findByToken(uuid);
        assertEquals(user, userFromDB);

        userMapper.deleteSession(uuid);

        String oldToken = userMapper.findTokenByUserId(user.getId());
        assertNull(oldToken);

        User releasedUser = userMapper.findByToken(uuid);
        assertNull(releasedUser);
    }

}
