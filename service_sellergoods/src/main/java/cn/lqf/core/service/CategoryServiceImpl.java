package cn.lqf.core.service;

import cn.lqf.core.dao.ad.ContentCategoryDao;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.ad.ContentCategory;
import cn.lqf.core.pojo.ad.ContentCategoryQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private ContentCategoryDao categoryDao;

    //查询所有
    @Override
    public List<ContentCategory> findAll() {
        return categoryDao.selectByExample(null);
    }

    //添加
    @Override
    public void add(ContentCategory contentCategory) {
        categoryDao.insertSelective(contentCategory);
    }

    //
    @Override
    public ContentCategory findOne(Long id) {
        return categoryDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(ContentCategory contentCategory) {
        categoryDao.updateByPrimaryKeySelective(contentCategory);
    }

    //分页查询
    @Override
    public PageResult findPage(ContentCategory contentCategory, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        ContentCategoryQuery query = new ContentCategoryQuery();
        ContentCategoryQuery.Criteria criteria = query.createCriteria();
        if (contentCategory != null){
            if (contentCategory.getName() != null && contentCategory.getName().length()>0){
                criteria.andNameLike("%"+contentCategory.getName()+"%");
            }
        }
        Page<ContentCategory> page = (Page<ContentCategory>)categoryDao.selectByExample(query);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            categoryDao.deleteByPrimaryKey(id);
        }
    }
}
