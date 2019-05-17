package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.entity.Admin;
import net.thumbtack.onlineshop.exeption.OnlineShopException;

public interface AdministratorDao {

    Admin findAdminById(int id) throws OnlineShopException;

    Admin insertAdmin(Admin admin);

    Admin updateAdmin(Admin admin);
}
