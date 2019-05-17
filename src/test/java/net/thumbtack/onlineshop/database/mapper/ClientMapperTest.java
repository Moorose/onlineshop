package net.thumbtack.onlineshop.database.mapper;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.entity.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientMapperTest {

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private CommonClearDatabaseNode clearDatabaseNode;

    @Before
    public void clear() {
        clearDatabaseNode.clearDatabase();
    }

    @Test
    public void testInsertAndFindAll() {
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            clients.add(new Client(0, "user" + i, "last_name" + i, null, "LOGIN" + i, "password", "7913153346" + i, "box_" + i + "@yandex.com", "address" + i, 1111));
        }
        for (Client client : clients) {
            userMapper.insertUser(client);
            assertTrue(client.getId() != 0);
            clientMapper.insertClient(client);
            clientMapper.insertClientDeposit(client);
        }
        List<Client> allClient = clientMapper.findAllClient();
        for (int i = 0; i < 10; i++) {
            assertEquals(clients.get(i).getLogin(), allClient.get(i).getLogin());
            assertEquals("LOGIN" + i, allClient.get(i).getLogin());
            assertEquals(1111, allClient.get(i).getDeposit().getMoney());
        }
    }

    @Test
    public void testFindClientById() {
        Client client = new Client(0, "userTest", "last_nameTest", null, "loginTest", "passwordTest", "11111111111", "box_Test@yandex.com", "address", 1000);
        userMapper.insertUser(client);
        assertTrue(client.getId() != 0);
        clientMapper.insertClient(client);
        clientMapper.insertClientDeposit(client);
        Client clientFromDB = clientMapper.findClientById(client.getId());
        assertEquals(1000, clientFromDB.getDeposit().getMoney());
        assertEquals(1, clientFromDB.getDeposit().getVersion());
    }


    @Test
    public void testUpdateClient() {
        Client client = new Client(0, "userTest", "last_nameTest", null, "loginTest", "passwordTest", "11111111111", "box_Test@yandex.com", "address", 0);
        userMapper.insertUser(client);
        assertTrue(client.getId() != 0);
        clientMapper.insertClient(client);
        clientMapper.insertClientDeposit(client);
        client.setPhone("");
        client.setEmail("");
        client.setAddress("");
        clientMapper.updateClient(client);
        Client clientUpdate = clientMapper.findClientById(client.getId());
        assertTrue(clientUpdate.getPhone().isEmpty());
        assertTrue(clientUpdate.getEmail().isEmpty());
        assertTrue(clientUpdate.getAddress().isEmpty());
        assertEquals(1, clientUpdate.getDeposit().getVersion());
    }

    @Test
    public void testUpdateDeposit() {
        Client client = new Client(0, "userTest", "last_nameTest", null, "loginTest", "passwordTest", "11111111111", "box_Test@yandex.com", "address", 0);
        userMapper.insertUser(client);
        assertTrue(client.getId() != 0);
        clientMapper.insertClient(client);
        clientMapper.insertClientDeposit(client);
        int countTransaction = 0;
        for (int i = 0; i < 5; i++) {
            countTransaction += clientMapper.addMoneyToDeposit(client, 100);
        }
        Client clientUpdate = clientMapper.findClientById(client.getId());
        assertEquals(5, countTransaction);
        assertEquals(6, clientUpdate.getDeposit().getVersion());
        assertEquals(500, clientUpdate.getDeposit().getMoney());
    }

}
