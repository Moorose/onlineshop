package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.entity.Client;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

import java.util.List;

public interface ClientDao {

    Client findClientById(int id) throws OnlineShopException;

    Client insertClient(Client client);

    List<Client> findAllClient();

    Client updateClient(Client client);

    void addMoneyToDeposit(Client client, int deposit) throws OnlineShopException;
}
