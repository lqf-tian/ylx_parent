package cn.lqf.core.service;

import cn.lqf.core.dao.good.BrandDao;
import cn.lqf.core.dao.good.GoodsDao;
import cn.lqf.core.dao.good.GoodsDescDao;
import cn.lqf.core.dao.item.ItemCatDao;
import cn.lqf.core.dao.item.ItemDao;
import cn.lqf.core.dao.seller.SellerDao;
import cn.lqf.core.entity.GoodsEntity;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.good.Brand;
import cn.lqf.core.pojo.good.Goods;
import cn.lqf.core.pojo.good.GoodsDesc;
import cn.lqf.core.pojo.good.GoodsQuery;
import cn.lqf.core.pojo.item.Item;
import cn.lqf.core.pojo.item.ItemCat;
import cn.lqf.core.pojo.item.ItemCatQuery;
import cn.lqf.core.pojo.item.ItemQuery;
import cn.lqf.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService{
    @Reference
    private SolrManagerService solrManagerService;
    @Autowired
    private GoodsDao goodsDao; //商品
    @Autowired
    private GoodsDescDao goodsDescDao; //商品详情
    @Autowired
    private ItemDao itemDao;//库存
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private ActiveMQTopic topicPageAndSolrDestination;   //上架使用
    @Autowired
    private ActiveMQQueue queueSolrDeleteDestination;    //下架使用
    @Autowired
    private JmsTemplate jmsTemplate;
    @Override
    public void add(GoodsEntity goodsEntity) {
        //1.保存商品对象
        goodsEntity.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(goodsEntity.getGoods());
        //2.保存商品详情对象
        //商品的主键作为商品详情的主键
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        goodsDescDao.insertSelective(goodsEntity.getGoodsDesc());
        //3.保存库存集合对象
        insertItem(goodsEntity,"add");
    }




    public void insertItem(GoodsEntity goodsEntity,Object method){
        if("1".equals(goodsEntity.getGoods().getIsEnableSpec())){
            //勾选复选框 库存有数据
            if (goodsEntity.getItemList() != null){
                //库存对象
                for (Item item: goodsEntity.getItemList()){
                    //标题由商品名 + 规格组成 供消费者搜索使用
                    String title = goodsEntity.getGoods().getGoodsName();
                    String specJson = item.getSpec();
                    //将json转成对象
                    Map specMap=JSON.parseObject(specJson, Map.class);

                    //获取map specMap中的value集合
                    Collection<String> values = specMap.values();
                    for (String value : values) {
                        //title+=value;
                        title+=" "+value;
                    }
                    item.setTitle(title);
                    //设置库存的对象的属性值
                    setItemValue(goodsEntity,item,method);
                    itemDao.insert(item);
                }
            }

        }else {
            //没有勾选 没有库存 但是初始化一条
            Item item = new Item();
            item.setPrice(new BigDecimal("9999999999999"));
            //规格
            item.setNum(0);
            item.setSpec("{}");
            //标题
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            //设置库存对象的属性
            setItemValue(goodsEntity,item,method);
            itemDao.insert(item);
        }
    }

    private Item setItemValue(GoodsEntity goodsEntity,Item item,Object method){
        //商品的id
        item.setGoodsId(goodsEntity.getGoods().getId());
        //创建的时间
        if ("add".equals(method)){
            item.setCreateTime(new Date());
        }else {
            item.setCreateTime((Date) method);

        }
        //更新时间
        item.setUpdateTime(new Date());
        //库存的状态
        //item.setStatus("0");
        //分类的id 库存分类
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        //分类的名称
        ItemCat itemCat = itemCatDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //品牌的名称
        Brand brand = brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //卖家的名称
        Seller seller = sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId());
        item.setSeller(seller.getName());
        //实例图片
        String itemImages = goodsEntity.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(itemImages, Map.class);
        if (maps != null && maps.size()>0){
            String url = String.valueOf(maps.get(0).get("url"));
            item.setImage(url);
        }
        return item;
    }

    //分有条件查询
    @Override
    public PageResult search(Goods goods,int page,int rows) {
        PageHelper.startPage(page,rows);
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        criteria.andIsDeleteIsNull();
        if (goods !=null ){
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length()>0){
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length()>0){
                criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
                //criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
        }
        Page<Goods> goodsPage = (Page<Goods>)goodsDao.selectByExample(query);
        return new PageResult(goodsPage.getTotal(),goodsPage.getResult());
    }

    //查询一个用于回显
    @Override
    public GoodsEntity findOne(Long id) {
        GoodsEntity goodsEntity = new GoodsEntity();

        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsEntity.setGoods(goods);
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goodsEntity.setGoodsDesc(goodsDesc);
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(itemQuery);
        goodsEntity.setItemList(items);

        return goodsEntity;
    }

    //修改
    @Override
    public void update(final GoodsEntity goodsEntity) {
        Goods goods = goodsEntity.getGoods();
        goodsDao.updateByPrimaryKeySelective(goods);
        GoodsDesc goodsDesc = goodsEntity.getGoodsDesc();
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        List<Item> items = itemDao.selectByExample(itemQuery);
        Date time=null;
        for (Item item : items) {
           time = item.getCreateTime();
        }
        itemDao.deleteByExample(itemQuery);
        insertItem(goodsEntity,time);
        if ("1".equals(goodsEntity.getGoods().getIsMarketable())){
            //先删除solr中的数据，在从新添加
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(String.valueOf(goodsEntity.getGoods().getId()));
                    return textMessage;

                }
            });

            jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(String.valueOf(goodsEntity.getGoods().getId()));
                    return textMessage;
                }
            });

        }

    }

    //删除
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            goodsDao.updateByPrimaryKeySelective(goods);

        }
    }

    //逻辑删除
    @Override
    public void delete(final Long id) {
        Goods goods = new Goods();
        goods.setId(id);
        goods.setIsDelete("1");
        goodsDao.updateByPrimaryKeySelective(goods);

        //将商品的id作为消息发送给服务器

        jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                return textMessage;

            }
        });
    }

    //审核
    @Override
    public void updateStatus(final Long id, String status) {
        //1.根据商品的id 修改商品的状态码
        Goods goods = new Goods();
        goods.setId(id);
        goods.setAuditStatus(status);
        goodsDao.updateByPrimaryKeySelective(goods);
        //2根据商品的id  修改库存对象的状态码
        Item item = new Item();
        item.setStatus(status);
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        itemDao.updateByExampleSelective(item,query);
        //将商品的id作为消息发送给消息服务器
        if ("1".equals(status)){
            jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                    return textMessage;
                    //接收方有两个  一个search 一个page
                }
            });
        }
    }

    //商品上下架
    @Override
    public void updateisMarkeTable(Long[] ids, String isMarkeTable) {
        for (final Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);
            if ("1".equals(goods.getAuditStatus())){
                goods.setIsMarketable(isMarkeTable);
                if ("1".equals(isMarkeTable)){
                    jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                            return textMessage;
                        }
                    });
                }else {
                    jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                            return textMessage;

                        }
                    });
                }
            }
            goodsDao.updateByPrimaryKeySelective(goods);
        }
    }


}
