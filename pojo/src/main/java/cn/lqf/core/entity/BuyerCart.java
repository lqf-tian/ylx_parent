package cn.lqf.core.entity;

import cn.lqf.core.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Lqf
 * @Date: 2019/12/2 15:02
 *
 * 自定义封装购物车
 */
public class BuyerCart implements Serializable {
    private String SellerId;//上架Id
    private String SellerName;//上架名称
    private List<OrderItem> orderItemList;//购物明细

    public String getSellerId() {
        return SellerId;
    }

    public void setSellerId(String sellerId) {
        SellerId = sellerId;
    }

    public String getSellerName() {
        return SellerName;
    }

    public void setSellerName(String sellerName) {
        SellerName = sellerName;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public String toString() {
        return "BuyerCart{" +
                "SellerId='" + SellerId + '\'' +
                ", SellerName='" + SellerName + '\'' +
                ", orderItemList=" + orderItemList +
                '}';
    }
}
