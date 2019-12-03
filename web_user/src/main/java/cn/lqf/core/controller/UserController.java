package cn.lqf.core.controller;

import cn.lqf.core.entity.Result;
import cn.lqf.core.pojo.user.User;
import cn.lqf.core.service.UserService;
import cn.lqf.core.util.PhoneFormatCheckUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Author: Lqf
 * @Date: 2019/11/28 15:31
 */
@RequestMapping("/user")
@RestController
public class UserController {
    @Reference
    private UserService userService;
    //发送短信验证码    手机号
    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        //System.out.println("ye921843174");
        try {
            if (phone==null || "".equals(phone)){
                return new Result(false,"手机号码不能为空");
            }
            if (!PhoneFormatCheckUtils.isPhoneLegal(phone)){
                return new Result(false,"手机格式不正确");
            }
            userService.sendCode(phone);
            return new Result(true,"发送成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"发送失败");
        }
    }

    //注册
    @RequestMapping("/add")
    public Result add(@RequestBody User user, String smscode){
        try {
            Boolean isCheck = userService.checkSmsCode(user.getPhone(), smscode);
            //System.out.println(smscode);
            //System.out.println(user.getPhone());
            //System.out.println(isCheck);
            if (!isCheck){
                return new Result(false,"手机或者验证码不正确");
            }
            user.setSourceType("1");
            user.setStatus("Y");
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userService.add(user);
            return new Result(true,"注册成功");
        }catch (Exception e){
            return new Result(false,"注册失败");
        }
    }
}
