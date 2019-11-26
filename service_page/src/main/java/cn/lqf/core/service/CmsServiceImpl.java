package cn.lqf.core.service;

import cn.lqf.core.dao.good.GoodsDao;
import cn.lqf.core.dao.good.GoodsDescDao;
import cn.lqf.core.dao.item.ItemCatDao;
import cn.lqf.core.dao.item.ItemDao;
import cn.lqf.core.pojo.good.Goods;
import cn.lqf.core.pojo.good.GoodsDesc;
import cn.lqf.core.pojo.item.Item;
import cn.lqf.core.pojo.item.ItemCat;
import cn.lqf.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {

    @Autowired
    private ServletContext servletContext;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    //取数据
    @Override
    public Map<String, Object> findGoodsData(Long goodsid) {
        HashMap<String, Object> resultMap = new HashMap<>();
        //1.获取商品的数据
        Goods goods = goodsDao.selectByPrimaryKey(goodsid);
        //2.获取商品的详情数据
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goodsid);
        //3.获取库存集合数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsid);
        List<Item> itemList = itemDao.selectByExample(query);
        //4.获取商品对应分类
        if (goodsid != null){
            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
            //封装数据
            resultMap.put("itemCat1",itemCat1.getName());
            resultMap.put("itemCat2",itemCat2.getName());
            resultMap.put("itemCat3",itemCat3.getName());

        }
        //5.将商品的所有数据封装成map返回   key---->需要看模板
        resultMap.put("goods",goods);
        resultMap.put("goodsDesc",goodsDesc);
        resultMap.put("itemList",itemList);
        return resultMap;
    }

    //根据取到的数据生成页面
    @Override
    public void createStaticPage(Long goodsId, Map<String, Object> rootmap) throws Exception {

        //1.获取模板的初始化
        Configuration configuration = freeMarkerConfig.getConfiguration();
        //2.获取模板对象
        Template template = configuration.getTemplate("item.ftl");
        //3.创建输出流  指定生成静态页面的位置和名称
        String path=goodsId+".html";
        System.out.println("*****"+path);
        //获取绝对路径
        String realPath = getRealPath(path);
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)), "UTF-8");
        //4.生成
        template.process(rootmap,out);
        //5.关闭流资源
        out.close();
    }


    //将相对路径转换成绝对路径
    private String getRealPath(String path){
        String realPath = servletContext.getRealPath(path);
        System.out.println("++++++"+realPath+"++++++");
        return realPath;
    }

    @Override
    public void setServletContext(javax.servlet.ServletContext servletContext) {

    }
}
