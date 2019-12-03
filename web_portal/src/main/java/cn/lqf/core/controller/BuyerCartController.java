package cn.lqf.core.controller;

import cn.lqf.core.entity.BuyerCart;
import cn.lqf.core.entity.Result;
import cn.lqf.core.service.CartService;
import cn.lqf.core.util.Constants;
import cn.lqf.core.util.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: Lqf
 * @Date: 2019/12/2 19:22
 */
@RestController
@RequestMapping("/cart")
public class BuyerCartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    // 添加商品到购物车 @CrossOrigin 相当于设置了响应头的信息
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:8085",allowCredentials = "true")
    //       库存id       数量
    public Result addGoodsToCarList(Long itemId,Integer num){
        try{
            //1获取当前用户登录名称
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //System.out.println(userName);
            //2  获取购物车列表
            List<BuyerCart> cartList = findCartList();
            //3 将当前商品加入到购物车列表中
            cartList = cartService.addItemToCartList(cartList,itemId,num);
            // 4 判断用户是否登录  未登录用户名为anonymouseUser
            if("anonymousUser".equals(userName)){
               // System.out.println("1233214325");
                //4.1 如果未登录   则将购物车列表存入 cookie中
                CookieUtil.setCookie(request,response, Constants.CART_LIST_COOKIE, JSON.toJSONString(cartList),60*60*24*30,"utf-8");
                //4.2 如果已经登录 将购物车列表存入redis中
            }else {
                //4.2 如果已经登录 将购物车列表存入redis中
                cartService.setCartListRedis(userName,cartList);

            }

            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
    //获取购物车所有的返回数据
    @RequestMapping("/findCartList")
    public List<BuyerCart> findCartList(){
        //1获取当前用户登录名称
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //2  从cookie 中  获取列表
        String cookieCarListStr = CookieUtil.getCookieValue(request, Constants.CART_LIST_COOKIE, "UTF-8");

        //3 如果购物陈列表 json  为空
        if(cookieCarListStr==null||"".equals(cookieCarListStr)){
            cookieCarListStr="[]";
        }

        //3.1将购物裂变转换为对象
        List<BuyerCart> cookieCarList = JSON.parseArray(cookieCarListStr, BuyerCart.class);
        //  4 判断 用户是否登录
        if ("anonymousUser".equals(userName)) {
            return cookieCarList;

        }else{
            //已经登录 从redis中  获取数据
            List<BuyerCart> rediscartList= cartService.getCartListFromRedis(userName);

            if(cookieCarList.size()>0){
                // redis中和cookie 中 完全合并一个对象
                cartService.megreCookieCarListFrmoRedis(cookieCarList,rediscartList);
                // 删除cookie中购物列表
                CookieUtil.deleteCookie(request,response,Constants.CART_LIST_COOKIE);
                //将合并后的购物车列表存入到redis 中
                cartService.setCartListRedis(userName,rediscartList);
            }
            //返回购物车列表对象
            return rediscartList;
        }



    }
}
