package net.thumbtack.onlineshop.database.daoimpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.support.DaoImplBase;
import net.thumbtack.onlineshop.entity.Category;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Slf4j
public class CategoryDaoImpl extends DaoImplBase implements CategoryDao {

    @Autowired
    public CategoryDaoImpl(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate);
    }

    @Override
    @Transactional
    public Category addCategory(Category category) {
        log.debug("CategoryDao insert category: {}", category);
        getCategoryMapper().insertCategory(category);
        return category;
    }

    @Override
    @Transactional(rollbackFor = OnlineShopException.class)
    public Category findCategoryById(int id) throws OnlineShopException {
        log.debug("CategoryDao find category by id: {}", id);
        Category category = getCategoryMapper().findCategoryById(id);
        if (category == null) {
            throw new OnlineShopException("id", OnlineShopErrorCode.DATABASE_FIND_CATEGORY_BY_ID_ERROR);
        }
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {
        log.debug("CategoryDao update category: {}", category);
        getCategoryMapper().updateCategory(category);
        return category;
    }

    @Override
    @Transactional
    public void deleteCategoryById(int id) {
        log.debug("CategoryDao delete category by id: {}", id);
        getCategoryMapper().deleteCategoryById(id);
    }

    @Override
    @Transactional
    public List<Category> findAllCategory() {
        log.debug("CategoryDao find undefined category");
        return getCategoryMapper().findAllCategory();
    }
}
