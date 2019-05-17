package net.thumbtack.onlineshop.database.support;


import net.thumbtack.onlineshop.database.mapper.*;
import org.mybatis.spring.SqlSessionTemplate;

public class DaoImplBase {

    private final SqlSessionTemplate sqlSessionTemplate;

    public DaoImplBase(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    protected UserMapper getUserMapper() {
        return sqlSessionTemplate.getMapper(UserMapper.class);
    }

    protected AdministratorMapper getAdministratorMapper() {
        return sqlSessionTemplate.getMapper(AdministratorMapper.class);
    }

    protected ClientMapper getClientMapper() {
        return sqlSessionTemplate.getMapper(ClientMapper.class);
    }

    protected CategoryMapper getCategoryMapper() {
        return sqlSessionTemplate.getMapper(CategoryMapper.class);
    }

    protected ProductMapper getProductMapper() {
        return sqlSessionTemplate.getMapper(ProductMapper.class);
    }

    protected PurchaseMapper getPurchaseMapper() {
        return sqlSessionTemplate.getMapper(PurchaseMapper.class);
    }
}