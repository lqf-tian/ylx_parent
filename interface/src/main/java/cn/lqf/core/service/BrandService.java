package cn.lqf.core.service;

import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<Brand> findAll();
    public void add(Brand brand);
    public Brand findOne(Long id);
    public void update(Brand brand);
    public PageResult findPage(int pageNum, int pageSize);
    public PageResult findPage(Brand brand, int pageNum,int pageSize);
    public void delete(Long[] ids);
    List<Map> selectOptionList();
}
