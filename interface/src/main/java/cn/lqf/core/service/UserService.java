package cn.lqf.core.service;

import cn.lqf.core.pojo.user.User;

/**
 * @Author: Lqf
 * @Date: 2019/11/28 15:35
 */
public interface UserService {
    //发送验证码
    public void sendCode(String phone);

    //注册=验证码校验+添加
    public Boolean checkSmsCode(String phone,String smsCode);
    //添加
    public void add(User user);
}
