package net.thumbtack.onlineshop.database.daoimpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.database.dao.ClientDao;
import net.thumbtack.onlineshop.database.support.DaoImplBase;
import net.thumbtack.onlineshop.entity.Client;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Slf4j
public class ClientDaoImpl extends DaoImplBase implements ClientDao {

    @Autowired
    public ClientDaoImpl(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate);
    }

    @Override
    @Transactional
    public Client insertClient(Client client) {
        log.debug("ClientDao insert client: {}", client);
        getUserMapper().insertUser(client);
        getClientMapper().insertClient(client);
        getClientMapper().insertClientDeposit(client);
        return client;
    }

    @Override
    @Transactional
    public Client findClientById(int id) {
        log.debug("ClientDao find client by Id = {}", id);
        return getClientMapper().findClientById(id);
    }

    @Override
    @Transactional
    public List<Client> findAllClient() {
        log.debug("ClientDao find undefined clients");
        return getClientMapper().findAllClient();
    }

    @Override
    @Transactional
    public Client updateClient(Client client) {
        log.debug("ClientDao update client: {}", client);
        getUserMapper().updateUser(client);
        getClientMapper().updateClient(client);
        return client;
    }

    @Override
    @Transactional(rollbackFor = OnlineShopException.class)
    public void addMoneyToDeposit(Client client, int money) throws OnlineShopException {
        log.debug("ClientDao add money to deposit for client: {} -> {}", money, client);
        int countRow = getClientMapper().addMoneyToDeposit(client, money);
        if (countRow != 1) {
            throw new OnlineShopException("version", OnlineShopErrorCode.DATABASE_UPDATE_DEPOSIT_ERROR);
        }
    }
}
