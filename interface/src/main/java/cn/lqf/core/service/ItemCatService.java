package cn.lqf.core.service;


import cn.lqf.core.pojo.item.Item;
import cn.lqf.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {

    List<ItemCat> findByParentId(Long parentId);
    //查找单个用于回显
    ItemCat findOne(Long id);

    //添加
    void add(ItemCat itemCat);
    //删除
    void delete(Long[] ids);

    //修改
    void update(ItemCat itemCat);

    //商品管理查询所有
    List<ItemCat> findAll();


}
