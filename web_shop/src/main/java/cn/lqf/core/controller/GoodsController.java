package cn.lqf.core.controller;

import cn.lqf.core.entity.GoodsEntity;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.entity.Result;
import cn.lqf.core.pojo.good.Goods;
import cn.lqf.core.pojo.item.ItemQuery;
import cn.lqf.core.service.CmsService;
import cn.lqf.core.service.GoodsService;
import cn.lqf.core.service.SolrManagerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

/*    @Reference
    private CmsService cmsService;
    @Reference
    private SolrManagerService solrManagerService;*/
    @Reference
    private GoodsService goodsService;
    @RequestMapping("/add")
    public Result add(@RequestBody GoodsEntity goodsEntity){
        try {
            // 卖家名称初始化 不让用户填写
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsEntity.getGoods().setSellerId(userName);
            goodsService.add(goodsEntity);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods,int page,int rows){
        //获取商家ID
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //添加查询条件
        goods.setSellerId(sellerId);
        return   goodsService.search(goods,page,rows);
    }

    //查询单个用于回显
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id){
        GoodsEntity one = goodsService.findOne(id);
        return one;
    }

    //修改
   @RequestMapping("/update")
    public Result update(@RequestBody GoodsEntity goodsEntity){
        try {
            //获取当前的登录名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //商品所有者
            String sellerId = goodsEntity.getGoods().getSellerId();
            if (!userName.equals(sellerId)){
                return  new Result(false,"您没有权限修改");
            }
            Long goodsId = goodsEntity.getGoods().getId();
            goodsService.update(goodsEntity);
            //商家在修改后台数据时，要从新查找数据，并且生成相应的详情页面才能够保证
            //数据更新后详情页面也更新
            /*Map<String, Object> goodsData = cmsService.findGoodsData(goodsId);
            cmsService.createStaticPage(goodsId,goodsData);
            if ("1".equals(goodsEntity.getGoods().getIsMarketable())){
                solrManagerService.deleteItemFromSolr(goodsId);
                solrManagerService.saveItemToSolr(goodsId);
            }*/

            return  new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return  new Result(false,"修改失败");
        }

    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            if (ids != null){
                for (Long id : ids) {
                    goodsService.delete(id);
                    //根据商品id 删除solr索引库中的数据
                    //solrManagerService.deleteItemFromSolr(id);
                }
            }
            return  new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return  new Result(false,"删除失败");
        }
    }

    //商品上下架

    @RequestMapping("/updateIsMarkeTable")
    public Result updateIsMarkeTable(Long[] ids,String isMarkeTable){
        try {
            if (ids !=null){
                for (Long id : ids) {
                    goodsService.updateisMarkeTable(ids,isMarkeTable);
                }
            }

            return new Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
