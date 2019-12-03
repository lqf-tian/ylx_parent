package cn.lqf.core.service;

import cn.lqf.core.entity.BuyerCart;

import java.util.List;

/**
 * @Author: Lqf
 * @Date: 2019/12/3 9:13
 */
public interface CartService {
    //将商品加入这个人现有的购物车列表中
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList,Long itemId,Integer num);
    //将购物车添加到redis集合中
    public void setCartListRedis(String userName,List<BuyerCart> cartList);
    //从redis中查询购物车列表
    public List<BuyerCart> getCartListFromRedis(String userName);
    //合并购物车
    public List<BuyerCart> megreCookieCarListFrmoRedis(List<BuyerCart> cookieCartLidst,List<BuyerCart> redisCartiList);
}
