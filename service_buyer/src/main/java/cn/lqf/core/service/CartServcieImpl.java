package cn.lqf.core.service;

import cn.lqf.core.dao.item.ItemDao;
import cn.lqf.core.entity.BuyerCart;
import cn.lqf.core.pojo.item.Item;
import cn.lqf.core.pojo.order.OrderItem;
import cn.lqf.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lqf
 * @Date: 2019/12/3 9:22
 */
@Service
public class CartServcieImpl implements CartService{
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num) {
        //  1 根据商品ID 查询商品的信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        //2 判断商品是否存在  抛异常
        if(item==null){
            throw new RuntimeException("此商品不存在");
        }
        //3判断该商品是否为1   已经审核状态  状态不对抛异常
        if(!"1".equals(item.getStatus())){
            throw new RuntimeException("此商品未通过审核 不允许买卖");
        }
        //4 获取商家的id
        String sellerId = item.getSellerId();
        // 5 根据商家的ID 查询购物车列表中是否存在该商家的购物车
        BuyerCart byerCart = findBuyerCartBySellerId(cartList, sellerId);
        //6 判断如果该购物车列表中不存在该商家的购物车
        if(byerCart==null){
            //6.1  新建购物车对象
            byerCart = new BuyerCart();
            //  6.2  创建购物车对象卖家id
            byerCart.setSellerId(sellerId);
            //  6.3  创建购物车对象卖家名称
            byerCart.setSellerName(item.getSeller());
            //  6.4  创建购物项集合
            ArrayList<OrderItem> orderItemList = new ArrayList<>();
            //  6.5  创建购物项
            OrderItem orderItem = createOrderItem(item, num);
            //  6.6  将购物项加入到购物项集合中
            orderItemList.add(orderItem);
            //  6.7  将购物项集合加进购物车中
            byerCart.setOrderItemList(orderItemList);
            //  6.8  将新建的购物车对象添加到购物车列表中
            cartList.add(byerCart);
        }else{
            //   否则如果购物车列表中存在着商家的购物车
            List<OrderItem> orderItemList = byerCart.getOrderItemList();
            OrderItem orderIrtem= findOneIrtemId(orderItemList,itemId);
            //  6.9  判断购物车明细是否为空
            if(orderIrtem==null){
                //  6.10为空  则添加新的明细
                orderIrtem  = createOrderItem(item,num);
                orderItemList.add(orderIrtem);;
            }else {
                //  6. 11不为空 在原来购物车的基础上   添加商品的数量 更改金额
                orderIrtem.setNum(orderIrtem.getNum()+num);

                //  6.  12  设置总价
                orderIrtem.setTotalFee(orderIrtem.getPrice().multiply(new BigDecimal(orderIrtem.getNum())));
                //  6.  1.3 如果购物车明细数量《=0  则删除
                if(orderIrtem.getNum()<=0){
                    orderItemList.remove(orderIrtem);
                }
                //  6.  9  如果购物车明细表数量为空   则移除
                if(orderItemList.size()<=0){
                    cartList.remove(byerCart);
                }
            }
        }
        //7 返回购物车列表对象
        return cartList;
    }



    @Override
    public List<BuyerCart> megreCookieCarListFrmoRedis(List<BuyerCart> cookieCartLidst, List<BuyerCart> redisCartiList) {
        if(cookieCartLidst!=null){
            // 遍历购物车集合
            for(BuyerCart cookieCart:cookieCartLidst){
                for(OrderItem cookieOrderItem:cookieCart.getOrderItemList()){
                    // 将购物车集合加入到  redis购物车集合
                    redisCartiList = addItemToCartList(redisCartiList,cookieOrderItem.getItemId(),cookieOrderItem.getNum());
                }
            }
        }
        return redisCartiList;
    }


    // 从购物车中 查询是否存在这个商品
    private OrderItem findOneIrtemId(List<OrderItem> orderItemList,Long itemId){
        if(orderItemList!=null){
            for(OrderItem orderItem:orderItemList){
                if(orderItem.getItemId().equals(itemId)){
                    return orderItem;
                }
            }
        }
        return null;
    }
    // 创建购物选项集合
    private OrderItem createOrderItem(Item item,Integer num){
        if(num<0){
            throw new RuntimeException("购买数量非法");
        }
        OrderItem orderItem = new OrderItem();
        // 购买的数量
        orderItem.setNum(num);
        orderItem.setItemId(item.getId());
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        // 卖家的id
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        //总价=单价*个数
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));
        return orderItem;
    }
    // 查询此购物车有没有卖家这个购物车对象 有返回  没有返回空‘
    private BuyerCart findBuyerCartBySellerId(List<BuyerCart> cartList,String sellerId){
        if(cartList!=null){
            for (BuyerCart cart:cartList){
                if(cart.getSellerId().equals(sellerId)){
                    return cart;
                }
            }
        }
        return null;
    }
    @Override
    public void setCartListRedis(String userName, List<BuyerCart> cartList) {
        redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).put(userName,cartList);
    }

    @Override
    public List<BuyerCart> getCartListFromRedis(String userName) {
        List<BuyerCart> cartList = (List<BuyerCart>)redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).get(userName);
        if(cartList==null){
            cartList=new ArrayList<>();
        }
        return cartList;
    }


}
