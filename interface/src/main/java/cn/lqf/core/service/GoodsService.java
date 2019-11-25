package cn.lqf.core.service;

import cn.lqf.core.entity.GoodsEntity;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.good.Goods;

public interface GoodsService {
    //添加
    public void add (GoodsEntity goodsEntity);
    //分页条件查询
    public PageResult search(Goods goods,int pageNum,int pageSize);

    //查询一个用于回显
    GoodsEntity findOne(Long id);
    //修改
    void update(GoodsEntity goodsEntity);
    //删除
    void delete(Long[] ids);
    void delete(Long id);

    //审核updateStatus
    void updateStatus(Long id,String status);

    //商品上下架
    void updateisMarkeTable(Long[] ids, String isMarkeTable);
}
