package cn.lqf.core.controller;

import cn.lqf.core.entity.PageResult;
import cn.lqf.core.entity.Result;
import cn.lqf.core.pojo.seller.Seller;
import cn.lqf.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seller")
public class SellerController {

    @Reference
    private SellerService sellerService;
    //用户审核
    @RequestMapping("updateStatus")
    public Result updateStaus(String sellerId,String status){

        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //分页条件查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller, int page, int rows){
        return sellerService.search(seller,page,rows);
    }

    //查询单个用于回显
    @RequestMapping("findOne")
    public Seller findOne(String sellerId){
        return sellerService.findOne(sellerId);
    }
}
