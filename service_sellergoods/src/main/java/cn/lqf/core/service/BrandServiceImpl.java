package cn.lqf.core.service;

import cn.lqf.core.dao.good.BrandDao;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.good.Brand;
import cn.lqf.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService{

    @Autowired
    private BrandDao brandDao;

    //查询
    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    //添加
    @Override
    public void add(Brand brand) {
        brandDao.insert(brand);
    }

    //通过id查询用于回显
    @Override
    public Brand findOne(Long id) {
        Brand brand = brandDao.selectByPrimaryKey(id);
        return brand;
    }

    //修改
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    //分页
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<Brand> page = (Page<Brand>)brandDao.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //条件查询
    @Override
    public PageResult findPage(Brand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        BrandQuery query = new BrandQuery();
        BrandQuery.Criteria criteria = query.createCriteria();
        if(brand!= null){
            if (brand.getName() != null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar() != null && brand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<Brand> page = (Page<Brand>)brandDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());

    }
    //删除
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandDao.deleteByPrimaryKey(id);
        }
    }
    //三级联动所用的查询
    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }
}
