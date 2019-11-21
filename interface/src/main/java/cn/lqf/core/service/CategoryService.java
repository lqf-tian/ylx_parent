package cn.lqf.core.service;

import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.ad.ContentCategory;
import cn.lqf.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    //查所有
    public List<ContentCategory> findAll();
    //添加
    public void add(ContentCategory contentCategory);
    //
    public ContentCategory findOne(Long id);
    public void update(ContentCategory contentCategory);
    public PageResult findPage(ContentCategory contentCategory, int pageNum,int pageSize);
    public void delete(Long[] ids);

}
