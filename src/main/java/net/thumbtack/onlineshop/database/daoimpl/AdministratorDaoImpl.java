package net.thumbtack.onlineshop.database.daoimpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.database.dao.AdministratorDao;
import net.thumbtack.onlineshop.database.support.DaoImplBase;
import net.thumbtack.onlineshop.entity.Admin;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Slf4j
public class AdministratorDaoImpl extends DaoImplBase implements AdministratorDao {

    @Autowired
    public AdministratorDaoImpl(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate);
    }

    @Override
    @Transactional
    public Admin insertAdmin(Admin admin) {
        log.debug("AdministratorDao insert admin: {}", admin);
        getUserMapper().insertUser(admin);
        getAdministratorMapper().insertAdmin(admin);
        return admin;
    }

    @Override
    @Transactional
    public Admin findAdminById(int id) {
        log.debug("AdministratorDao find admin by Id = {}", id);
        return getAdministratorMapper().findAdminById(id);
    }


    @Override
    @Transactional
    public Admin updateAdmin(Admin admin) {
        log.debug("AdministratorDao update admin: {}", admin);
        getUserMapper().updateUser(admin);
        getAdministratorMapper().updateUser(admin);
        return admin;
    }

}