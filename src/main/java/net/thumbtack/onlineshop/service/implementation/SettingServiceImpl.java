package net.thumbtack.onlineshop.service.implementation;

import net.thumbtack.onlineshop.database.dao.AdministratorDao;
import net.thumbtack.onlineshop.database.dao.ClientDao;
import net.thumbtack.onlineshop.database.dao.UserDao;
import net.thumbtack.onlineshop.dto.SettingResponse;
import net.thumbtack.onlineshop.entity.Admin;
import net.thumbtack.onlineshop.entity.User;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import net.thumbtack.onlineshop.service.interfaces.SettingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceImpl extends BaseService implements SettingService {

    @Value("${min_password_length}")
    private int min_password_length;
    @Value("${max_name_length}")
    private int max_name_length;

    public SettingServiceImpl(AdministratorDao administratorDao, ClientDao clientDao, UserDao userDao) {
        super(administratorDao, clientDao, userDao, null, null);
    }

    @Override
    public SettingResponse getSetting(String javaSessionId) throws OnlineShopException {
        if (javaSessionId == null) {
            return new SettingResponse(max_name_length, min_password_length);
        }
        User user = getUserByCookie(javaSessionId);
        if (user instanceof Admin) {
            return new SettingResponse(max_name_length, min_password_length);
        }
        return new SettingResponse(max_name_length, min_password_length);

    }
}
