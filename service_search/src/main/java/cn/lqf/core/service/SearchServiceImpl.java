package cn.lqf.core.service;

import cn.lqf.core.pojo.item.Item;
import cn.lqf.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService{

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

 /*   @Override
    public Map<String, Object> search(Map paramMap) {
        //获取查询条件
        String  keywords =(String)paramMap.get("keywords");
        Integer pageNo = (Integer)paramMap.get("pageNo");
        //每页查询多少条
        Integer pageSize =(Integer)paramMap.get("pageSize");
        //封装查询对象
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入查询的对象
        query.addCriteria(criteria);
        if (pageNo == null || pageNo < 0){
            pageNo=1;
        }
        //当前页
        Integer start=(pageNo-1)*pageSize;
        //设置从第几条开始查询
        query.setOffset(start);
        //每页查询多少条数据
        query.setRows(pageSize);
        //去solr索引库查询并返回结果
        ScoredPage<Item> items = solrTemplate.queryForPage(query, Item.class);
        HashMap<String , Object> resultMap = new HashMap<>();
        resultMap.put("rows",items.getContent());
        resultMap.put("totalPages",items.getTotalPages());
        resultMap.put("total",items.getTotalElements());
        return  resultMap;
    }*/

    @Override
    public Map<String, Object> search(Map paramMap) {
        //1.根据参数关键字 到solr中（带分页）总条数、总页数
        Map<String, Object> resultMap = highlightSearch(paramMap);

        //2.根据查询的参数 到solr中获取对应的分类结果，因为分类有重复，按分组的方式去重
        List<String> groupCatgroupList = findGroupCatgroupList(paramMap);
        resultMap.put("categoryList",groupCatgroupList);
        //3.判断paramMap传入的参数中是否有分类的名称
        String category = String.valueOf(paramMap.get("category"));
        if (category != null && !"".equals(category)){
            //5.如果有分类参数  根据分类查询对应的品牌集合
            Map specListAndBrandList = findSpecListBrand(category);
            resultMap.putAll(specListAndBrandList);

        }else {
            //4.如果没有根据第一个分类查询对相应的商品集合
            Map specListBrand = findSpecListBrand(groupCatgroupList.get(0));
            resultMap.putAll(specListBrand);
        }

        return resultMap;
    }


    //高亮显示方法
    private Map<String,Object> highlightSearch(Map paramMap){
        //获取关键字
        String keywords = (String)paramMap.get("keywords");
        //当前页
        Integer pageNo =(Integer) paramMap.get("pageNo");
        //每页查询多少条
        Integer pageSize = (Integer)paramMap.get("pageSize");
        //封装查询的对象
        HighlightQuery query = new SimpleHighlightQuery();
        //查询的条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入查询对象中
        query.addCriteria(criteria);
        //从第几条查询
        if (pageNo == null || pageNo <= 0){
            pageNo=1;
        }
        Integer start=(pageNo-1)*pageSize;
        //设置从第几条查询
        query.setOffset(start);
        //设置没页多少条
        query.setRows(pageSize);

        //按分类筛选
        if (paramMap.get("category") != null &&!"".equals(paramMap.get("category"))){
            Criteria filterCriteria = new Criteria("item_category").is(paramMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //按照品牌筛选
        if (paramMap.get("brand") != null && !"".equals(paramMap.get("brand"))){
            Criteria filterCriteria = new Criteria("item_brand").is(paramMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //按照规格筛选
        if (paramMap.get("spec") != null && !"".equals(paramMap.get("spec"))){
            Map<String,String> specMap = (Map<String, String>) paramMap.get("spec");
            for (String key : specMap.keySet()){
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //按价格
        if(!"".equals(paramMap.get("price"))){
            String[] price = ((String) paramMap.get("price")).split("-");
            if(!price[0].equals("0")){//如果区间起点不等于0
                Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){//如果区间终点不等于*
                Criteria filterCriteria=new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }
        //排序
        String sortValue = (String) paramMap.get("sort");//ASC  DESC
        String sortField = (String) paramMap.get("sortField");//排序字段
        if (sortValue != null && !sortValue.equals("")) {
            if (sortValue.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }


        //创建高亮显示对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置那个域需要高亮显示
        highlightOptions.addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置高亮的后缀
        highlightOptions.setSimplePostfix("</em>");
        //将高亮加入到查询对象中
        query.setHighlightOptions(highlightOptions);

        //查询并返回结果
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);
        //获取带高亮的集合
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        //创建一个新的集合，用来接收高亮集合
        List<Item> itemList = new ArrayList<>();
        //遍历高亮集合
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //获取到不带高亮的集合
            Item item = itemHighlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if (highlights != null && highlights.size() > 0){
                //获取高亮的标题集合
                List<String> highlightTitle = highlights.get(0).getSnipplets();
                if (highlightTitle != null && highlightTitle.size() > 0){
                    //获取高亮的标题
                    String title=highlightTitle.get(0);
                    item.setTitle(title);
                }
            }
            itemList.add(item);
        }
        HashMap<String, Object> resultMap = new HashMap<>();
        //查询到的结果集
        resultMap.put("rows",itemList);
        //总页数
        resultMap.put("totalPages",items.getTotalPages());
        //总条数
        resultMap.put("total",items.getTotalElements());

        return resultMap;
    }

    //2.根据查询的参数 到solr中获取对应的分类结果，印分类有重复，按分组的方式去重
    private List<String> findGroupCatgroupList(Map paramMap){
        //声明一个空集合用来接收groupCategory
        List<String> resultList = new ArrayList<>();

        //获取关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        //创建查询对象
        SimpleQuery query = new SimpleQuery();
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入查询对象中
        query.addCriteria(criteria);

        //创建分组对象
        GroupOptions groupOptions = new GroupOptions();
        //设置根据分类域进行分组
        groupOptions.addGroupByField("item_category");
        //将分组对象放入查询对象中
        query.setGroupOptions(groupOptions);
        //使用分组查询  分类集合
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        //获得结果集合中的分类域集合
        GroupResult<Item> item_category = items.getGroupResult("item_category");
        //获得分类域中实体集合
        Page<GroupEntry<Item>> groupEntries = item_category.getGroupEntries();
        //遍历实体集合 得到实体对象
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupCategory = groupEntry.getGroupValue();
            //组装到空集合中
            resultList.add(groupCategory);
        }
        return resultList;


    }

    //4.根据分类名称查询对应品牌集合  规格集合
    private  Map findSpecListBrand(String categoryName){
        //a.根据分类名称到 redis中查询对相应的模板id
        Long templateId =(Long) redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).get(categoryName);
        System.out.println(templateId);
        //b.根据模板id去redis中去查询对应的品牌集合
        List<Map> brandList = (List<Map>)redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).get(templateId);
        //c.根据模板id去redis去查询对应的规格集合
        List<Map> specList =(List<Map>) redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).get(templateId);
        //d.将品牌集合和规格集合封装到Map集合中  返回
        Map resultMap = new HashMap<>();
        resultMap.put("brandList",brandList);
        resultMap.put("specList",specList);
        return resultMap;
    }

}
