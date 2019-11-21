package cn.lqf.core.service;

import cn.lqf.core.dao.ad.ContentDao;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.ad.Content;
import cn.lqf.core.pojo.ad.ContentQuery;
import cn.lqf.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ContentDao contentDao;
    @Override
    public List<Content> findAll() {
        return contentDao.selectByExample(null);
    }

    @Override
    public void add(Content content) {
        //1.添加了广告 到mysql数据库
        contentDao.insert(content);
        //2.根据分类id到redis中删除对应分类的广告集合
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
    }

    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Content content) {
        //1.根据广告的id  到mysql中查询原来的广告对象
        Content oldContent = contentDao.selectByPrimaryKey(content.getId());
        //2.根据原来广告对象中的分类id到redis中删除对应的广告集合
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(oldContent);
        //3.根据传入的最新的广告分类对象的id删除redis中对应的广告数据集合
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
        //4.将新的广告集合对象跟新到mysql
        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public PageResult findPage(Content content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        if (content!=null){
            if (content.getTitle() != null && content.getTitle().length()>0){
                criteria.andTitleLike("%"+content.getTitle()+"%");

            }
        }
        Page<Content> page = (Page<Content>)contentDao.selectByExample(query);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null){
            for (Long id : ids) {
                Content content = contentDao.selectByPrimaryKey(id);
                //根据广告对象中的分类id删除redis中的对应的广告集合数据
                redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<Content> list = contentDao.selectByExample(query);
        return list;
    }

    @Override
    public List<Content> findByCategoryFormRedis(Long categoryId) {
        //1.根据分类的id 到redis中取数据
        List<Content> contentList =(List<Content>) redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).get(categoryId);
        //2.如果redis中没有数据 到数据库中去取
        if (contentList == null){
            //3.如果数据库中获取到数据，存入redis中一份
            contentList=findByCategoryId(categoryId);
            redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).put(categoryId,contentList);
        }

        return contentList;
    }
}
