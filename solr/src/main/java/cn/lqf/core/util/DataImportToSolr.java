package cn.lqf.core.util;

import cn.lqf.core.dao.item.ItemDao;
import cn.lqf.core.pojo.item.Item;
import cn.lqf.core.pojo.item.ItemQuery;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataImportToSolr {

    @Autowired
    private SolrTemplate solrTemplate;

    //查询库存表中的所有数据
    @Autowired
    private ItemDao itemDao;

    private void importItemDataSolr(){
        ItemQuery query = new ItemQuery();
        //mybatis查询
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo("1");
        List<Item> items = itemDao.selectByExample(query);
        if (items != null){
            for (Item item : items) {
                //获取规格json字符串
                String specJsonStr = item.getSpec();
                Map map = JSON.parseObject(specJsonStr, Map.class);
                item.setSpecMap(map);
            }
            //放入solr中
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
        }
    }


    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        DataImportToSolr bean = (DataImportToSolr)context.getBean("dataImportToSolr");
        bean.importItemDataSolr();
    }
}
