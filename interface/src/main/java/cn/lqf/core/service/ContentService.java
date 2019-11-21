package cn.lqf.core.service;

import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.ad.Content;
import cn.lqf.core.pojo.ad.ContentCategory;

import java.util.List;

public interface ContentService {
    //查所有
    public List<Content> findAll();
    //添加
    public void add(Content content);
    //
    public Content findOne(Long id);
    public void update(Content content);
    public PageResult findPage(Content content, int pageNum, int pageSize);
    public void delete(Long[] ids);

    //广告查询
    public List<Content> findByCategoryId(Long categoryId);

    List<Content> findByCategoryFormRedis(Long categoryId);
}
