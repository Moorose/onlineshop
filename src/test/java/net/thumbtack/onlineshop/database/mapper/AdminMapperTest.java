package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.entity.Admin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminMapperTest {

    @Autowired
    private AdministratorMapper administratorMapper;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private CommonClearDatabaseNode clearDatabaseNode;

    @Before
    public void clear(){
        clearDatabaseNode.clearDatabase();
    }

    @Test
    public void testInsertAndFindById() {
        Admin admin = new Admin(0, "user", "last_name", null, "login", "password", "administrator");
        userMapper.insertUser(admin);
        assertTrue(admin.getId() != 0);
        administratorMapper.insertAdmin(admin);
        Admin adminFromDB = administratorMapper.findAdminById(admin.getId());
        assertEquals(admin, adminFromDB);
    }

    @Test
    public void updateAdmin() {
        Admin admin = new Admin(0, "user", "last_name", null, "login", "password", "administrator");
        userMapper.insertUser(admin);
        assertTrue(admin.getId() != 0);
        administratorMapper.insertAdmin(admin);
        admin.setFirstName("UserForever");
        userMapper.updateUser(admin);
        Admin updateAdmin = administratorMapper.findAdminById(admin.getId());
        assertEquals(admin, updateAdmin);    }
}