package cn.lqf.core.service;

import cn.lqf.core.dao.item.ItemCatDao;
import cn.lqf.core.pojo.item.Item;
import cn.lqf.core.pojo.item.ItemCat;
import cn.lqf.core.pojo.item.ItemCatQuery;
import cn.lqf.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService{

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<ItemCat> findByParentId(Long parentId) {

        //获取所有分类数据
        List<ItemCat> itemCatsAll = itemCatDao.selectByExample(null);
        //分类名称作为key  模板的id作为value
        for (ItemCat itemCat : itemCatsAll) {
            redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).put(itemCat.getName(),itemCat.getTypeId());
        }
        //跟据父级id  查询他的子集 展示到页面
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
