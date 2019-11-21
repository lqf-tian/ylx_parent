package cn.lqf.core.service;

import cn.lqf.core.dao.item.ItemCatDao;
import cn.lqf.core.pojo.item.Item;
import cn.lqf.core.pojo.item.ItemCat;
import cn.lqf.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService{

    @Autowired
    private ItemCatDao itemCatDao;
    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(query);
    }

    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public void add(ItemCat itemCat) {
        //ItemCatQuery query = new ItemCatQuery();
        //ItemCatQuery.Criteria criteria = query.createCriteria();
        itemCat.setParentId(itemCat.getParentId());
        itemCatDao.insert(itemCat);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            itemCatDao.deleteByPrimaryKey(id);
        }

    }

    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    //商品管理查询所有
    @Override
    public List<ItemCat> findAll() {
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        return itemCats;
    }


}
