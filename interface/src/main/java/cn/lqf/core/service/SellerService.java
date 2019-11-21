package cn.lqf.core.service;

import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.seller.Seller;

public interface SellerService {

    //用户注册
    public void add(Seller seller);

    //审核
    public void updateStatus(String sellerId,String status);

    //分页条件查询
    public PageResult search(Seller seller,int pageNum,int pageSize);

    //查询单个用于回显
    public Seller findOne(String sellerId);
}
