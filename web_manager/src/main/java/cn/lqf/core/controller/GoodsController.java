package cn.lqf.core.controller;

import cn.lqf.core.entity.GoodsEntity;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.entity.Result;
import cn.lqf.core.pojo.good.Goods;
import cn.lqf.core.service.CmsService;
import cn.lqf.core.service.GoodsService;
import cn.lqf.core.service.SolrManagerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    /*@Reference
    private CmsService cmsService;
    @Reference
    private SolrManagerService solrManagerService;*/
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
                    /*if ("1".equals(status)){
                        //3.根据商品的id获取库存的数据  放入solr 索引库中 供搜索使用
                        solrManagerService.saveItemToSolr(id);
                        //4.根据商品的id 获取商品的详情数据 并且根据详情的数据和模板生成详情的页面
                        Map<String, Object> goodsData = cmsService.findGoodsData(id);
                        cmsService.createStaticPage(id,goodsData);
                    }*/
                }
            }
            return new Result(true, "审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败");
        }
    }
/*        //测试生成的静态页面
    @RequestMapping("/testPage")
    public Boolean testCreatePage(Long goodsId){
        try {
            Map<String, Object> goodsData = cmsService.findGoodsData(goodsId);
            cmsService.createStaticPage(goodsId,goodsData);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }*/
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            if (ids != null){
                for (Long id : ids) {
                    goodsService.delete(id);
                    //solrManagerService.deleteItemFromSolr(id);
                }
            }

            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
}

