package cn.lqf.core.controller;

import cn.lqf.core.entity.GoodsEntity;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.entity.Result;
import cn.lqf.core.pojo.good.Goods;
import cn.lqf.core.service.GoodsService;
import cn.lqf.core.service.SolrManagerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private SolrManagerService solrManagerService;
    @Reference
    private GoodsService goodsService;
    //查询待审核的
    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods, int page, int rows){
        return goodsService.search(goods,page,rows);
    }
    //查看详情回显用的
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id){
        return goodsService.findOne(id);
    }

    //审核
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status){
        try {
            if (ids!=null){
                for (Long id : ids) {
                    //1.根据商品的id 改变商品的上架状态
                    goodsService.updateStatus(id,status);
                    //2.根据商品的id 到solr中删除对应的数据
                    if ("1".equals(status)){
                        solrManagerService.saveItemToSolr(id);
                    }
                }
            }
            System.out.println("12432543");
            return new Result(true, "审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            if (ids != null){
                for (Long id : ids) {
                    goodsService.delete(id);
                    solrManagerService.deleteItemFromSolr(id);
                }
            }

            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
}

